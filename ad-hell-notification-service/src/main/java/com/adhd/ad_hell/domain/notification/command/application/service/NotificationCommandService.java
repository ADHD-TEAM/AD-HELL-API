package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.*;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationDispatchResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationScheduleResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationTemplateResponse;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.Notification;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationSchedule;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationTemplate;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationCreatedEvent;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationReadEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.adhd.ad_hell.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final JpaNotificationRepository notificationRepo;
    private final JpaNotificationTemplateRepository templateRepo;
    private final JpaNotificationScheduleRepository scheduleRepo;

    /** 푸시 설정 저장소(메모리/JPA 어떤 구현이든 주입) */
    private final PushPreferencePort pushPref;

    private final ApplicationEventPublisher publisher;

    // === 설정 변경 ===
    @Transactional
    public void updatePushSetting(NotificationPushToggleRequest request) {
        log.debug("Update push setting. memberId={}, enabled={}", request.getMemberId(), request.getPushEnabled());
        pushPref.setEnabled(request.getMemberId(), Boolean.TRUE.equals(request.getPushEnabled()));
    }

    // === 읽음 처리 ===
    @Transactional
    public void markRead(Long userId, Long notificationId) {
        var n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException(NOTI_NOT_FOUND.getMessage()));
        if (!Objects.equals(n.getUserId(), userId)) {
            throw new IllegalStateException(NOTI_UNAUTHORIZED.getMessage());
        }
        if (n.getReadYn() == YnType.N) n.markRead();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                long unread = notificationRepo.countByUserIdAndReadYn(userId, YnType.N);
                publisher.publishEvent(NotificationReadEvent.of(userId, unread));
            }
        });
    }

    // === 즉시 발송 ===
    @Transactional
    public NotificationDispatchResponse sendNotification(Long templateId, NotificationSendRequest request) {
        var template = templateRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException(NOTI_TEMPLATE_NOT_FOUND.getMessage()));

        // 1) 대상자 집합 계산
        Set<Long> recipients = resolveRecipients(request);

        if (recipients.isEmpty()) {
            return NotificationDispatchResponse.builder()
                    .notificationId(null)
                    .recipientCount(0)
                    .build();
        }

        // 2) 본문/제목 변수 머지(단순 {{key}} 치환)
        String mergedTitle = mergeVariables(template.getTemplateTitle(), request.getVariables());
        String mergedBody  = mergeVariables(template.getTemplateBody(),  request.getVariables());

        // 3) Notification 벌크 생성/저장
        List<Notification> toSave = recipients.stream()
                .map(uid -> Notification.builder()
                        .userId(uid)
                        .notificationTitle(mergedTitle)
                        .notificationBody(mergedBody)
                        .readYn(YnType.no())
                        .build())
                .collect(Collectors.toList());

        List<Notification> saved = notificationRepo.saveAll(toSave);

        // 4) 커밋 후 사용자별로 NotificationCreatedEvent 발행 → SSE로 푸시
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                Map<Long, List<Notification>> byUser = saved.stream()
                        .collect(Collectors.groupingBy(Notification::getUserId));

                byUser.forEach((uid, list) -> {
                    // 가장 최근 저장건 기준으로 createdAt/제목/본문 전달
                    Notification last = list.get(list.size() - 1);
                    long unread = notificationRepo.countByUserIdAndReadYn(uid, YnType.N);

                    publisher.publishEvent(NotificationCreatedEvent.of(
                            uid, last.getId(),
                            last.getNotificationTitle(),
                            last.getNotificationBody(),
                            last.getCreatedAt(),
                            unread
                    ));
                });
            }
        });

        // 대표 notificationId는 첫 저장건 사용(호출자 편의)
        Long firstId = saved.get(0).getId();
        return NotificationDispatchResponse.builder()
                .notificationId(firstId)
                .recipientCount(saved.size())
                .build();
    }

    // === 예약 발송 등록 ===
    @Transactional
    public NotificationScheduleResponse reserveNotification(Long templateId, NotificationScheduleRequest request) {
        if (request.getScheduledAt() == null || !request.getScheduledAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(NOTI_TIME_NOT_ALLOWED.getMessage());
        }

        var template = templateRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException(NOTI_TEMPLATE_NOT_FOUND.getMessage()));

        NotificationSchedule schedule = NotificationSchedule.builder()
                .template(template)
                .scheduleStatus(NotificationScheduleStatus.SCHEDULED)
                .scheduledAt(request.getScheduledAt())
                .sentAt(null)
                .deletedYn(YnType.no())
                .build();

        NotificationSchedule saved = scheduleRepo.save(schedule);

        return NotificationScheduleResponse.builder()
                .scheduleId(saved.getId())
                .scheduleStatus(saved.getScheduleStatus())
                .scheduledAt(saved.getScheduledAt())
                .sentAt(saved.getSentAt())
                .build();
    }

    // === 템플릿 CUD ===
    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateCreateRequest request) {
        NotificationTemplate t = NotificationTemplate.builder()
                .templateKind(request.getTemplateKind())
                .templateTitle(request.getTemplateTitle())
                .templateBody(request.getTemplateBody())
                .deletedYn(YnType.no())
                .build();

        NotificationTemplate saved = templateRepo.save(t);

        return NotificationTemplateResponse.builder()
                .templateId(saved.getId())
                .templateKind(saved.getTemplateKind())
                .templateTitle(saved.getTemplateTitle())
                .templateBody(saved.getTemplateBody())
                .build();
    }

    @Transactional
    public NotificationTemplateResponse updateTemplate(Long templateId, NotificationTemplateUpdateRequest request) {
        NotificationTemplate t = templateRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException(NOTI_TEMPLATE_NOT_FOUND.getMessage()));

        t.update(request.getTemplateKind(), request.getTemplateTitle(), request.getTemplateBody());

        return NotificationTemplateResponse.builder()
                .templateId(t.getId())
                .templateKind(t.getTemplateKind())
                .templateTitle(t.getTemplateTitle())
                .templateBody(t.getTemplateBody())
                .build();
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        NotificationTemplate t = templateRepo.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException(NOTI_TEMPLATE_NOT_FOUND.getMessage()));
        t.delete(); // soft delete (deletedYn = Y)
    }

    /* ===================== 헬퍼 ===================== */

    private Set<Long> resolveRecipients(NotificationSendRequest req) {
        NotificationSendRequest.TargetType type = req.getTargetType();
        if (type == null) throw new IllegalArgumentException(NOTI_SENDTYPE_PRESENT.getMessage());

        switch (type) {
            case CUSTOM -> {
                if (req.getTargetMemberIds() == null || req.getTargetMemberIds().isEmpty()) {
                    throw new IllegalArgumentException(NOTI_CUSTOM_PRESENT.getMessage());
                }
                return new HashSet<>(req.getTargetMemberIds());
            }
            case PUSH_ENABLED -> {
                return new HashSet<>(pushPref.findAllEnabled());
            }
            case ALL -> {
                // 시스템에 등록된(푸시 설정 저장소가 알고 있는) 모든 회원
                return new HashSet<>(pushPref.findAllKnownMembers());
            }
            default -> throw new IllegalArgumentException(NOTI_SENDTYPE_NOT_ALLOWED.getMessage() + type);
        }
    }

    private String mergeVariables(String text, Map<String, String> vars) {
        if (text == null || vars == null || vars.isEmpty()) return text;
        String merged = text;
        for (var e : vars.entrySet()) {
            merged = merged.replace("{{" + e.getKey() + "}}", Objects.toString(e.getValue(), ""));
        }
        return merged;
    }
}
