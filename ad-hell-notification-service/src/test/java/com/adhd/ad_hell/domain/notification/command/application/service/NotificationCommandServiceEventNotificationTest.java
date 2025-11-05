package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationSendRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationDispatchResponse;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.Notification;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationTemplate;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationCreatedEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NOTI_PSH_002 – 이벤트 알림 발송
 *
 * 다른 도메인(문의, 댓글 등)에서 NotificationCommandService.sendNotification(...)
 * 을 호출했을 때의 동작을 검증한다.
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationCommandServiceEventNotificationTest {

    @Mock
    JpaNotificationRepository notificationRepo;

    @Mock
    JpaNotificationTemplateRepository templateRepo;

    @Mock
    JpaNotificationScheduleRepository scheduleRepo;

    @Mock
    PushPreferencePort pushPreferencePort;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    NotificationCommandService notificationCommandService;

    @AfterEach
    void tearDown() {
        // 혹시라도 테스트 사이에 Synchronization이 남으면 정리
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("NOTI_PSH_002 - CUSTOM 타겟으로 이벤트 알림 발송 시 Notification 저장 및 이벤트 발행")
    void sendEventNotificationWithCustomTarget() {
        // given
        Long templateId = 100L;
        Long userId = 1L;      // 예: 일반 회원
        Long adminId = 999L;   // 예: 관리자

        // 이벤트용 템플릿 (예: KIND = EVENT)
        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.EVENT)
                .templateTitle("문의에 답변이 등록되었습니다: {{title}}")
                .templateBody("문의 제목: {{title}}")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(java.util.Optional.of(template));

        // sendNotification 내부에서 saveAll 호출 시, ID가 채워진 Notification 리스트를 리턴하도록 스텁
        // (보통 JPA가 ID를 채워주지만, 여기선 순수 단위테스트이므로 우리가 넣어준다)
        ArgumentCaptor<List<Notification>> saveAllCaptor = ArgumentCaptor.forClass(List.class);
        when(notificationRepo.saveAll(saveAllCaptor.capture()))
                .thenAnswer(invocation -> {
                    List<Notification> list = invocation.getArgument(0);
                    // 간단히 첫 번째, 두 번째에 ID 부여
                    setId(list.get(0), 10L);
                    if (list.size() > 1) setId(list.get(1), 11L);
                    return list;
                });

        // unreadCount 계산용 스텁
        when(notificationRepo.countByUserIdAndReadYn(eq(userId), eq(YnType.N))).thenReturn(1L);
        when(notificationRepo.countByUserIdAndReadYn(eq(adminId), eq(YnType.N))).thenReturn(1L);

        // 다른 도메인(Inquiry 등)에서 보낸다고 가정한 요청
        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.CUSTOM)
                .targetMemberIds(List.of(userId, adminId))
                .variables(Map.of("title", "광고 문의 제목"))
                .build();

        // TransactionSynchronizationManager를 직접 활성화해서 afterCommit 콜백 테스트
        TransactionSynchronizationManager.initSynchronization();

        // when
        NotificationDispatchResponse response =
                notificationCommandService.sendNotification(templateId, request);

        // then - 1) Notification 저장 검증
        List<Notification> savedNotifications = saveAllCaptor.getValue();
        assertThat(savedNotifications).hasSize(2);
        assertThat(savedNotifications)
                .extracting("userId")
                .containsExactlyInAnyOrder(userId, adminId);

        // 머지된 제목/본문이 들어갔는지 확인
        assertThat(savedNotifications.get(0).getNotificationTitle())
                .isEqualTo("문의에 답변이 등록되었습니다: 광고 문의 제목");
        assertThat(savedNotifications.get(0).getNotificationBody())
                .isEqualTo("문의 제목: 광고 문의 제목");

        // 응답값 검증
        assertThat(response.getRecipientCount()).isEqualTo(2);
        assertThat(response.getNotificationId()).isEqualTo(10L); // 첫 번째 저장건의 ID

        // then - 2) AFTER_COMMIT 시 이벤트 발행 검증
        // 등록된 TransactionSynchronization을 꺼내서 afterCommit 강제 호출
        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        // 이벤트 2번(각 userId별 1번씩) 발행되었는지 체크
        ArgumentCaptor<NotificationCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(NotificationCreatedEvent.class);
        verify(publisher, times(2)).publishEvent(eventCaptor.capture());

        List<NotificationCreatedEvent> events = eventCaptor.getAllValues();
        assertThat(events)
                .extracting(NotificationCreatedEvent::getUserId)
                .containsExactlyInAnyOrder(userId, adminId);

        // 각 이벤트의 unreadCount는 위에서 스텁한 1L 이어야 함
        assertThat(events)
                .extracting(NotificationCreatedEvent::getUnreadCount)
                .containsOnly(1L);
    }

    /**
     * 테스트 편의를 위해 리플렉션으로 Notification의 id를 세팅한다.
     * (실제 런타임에서는 JPA가 ID를 채워줌)
     */
    private void setId(Notification notification, Long id) {
        try {
            var field = Notification.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(notification, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
