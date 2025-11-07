package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationScheduleRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationScheduleResponse;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationSchedule;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationTemplate;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceReserveNotificationTest {

    @Mock
    JpaNotificationRepository notificationRepo;

    @Mock
    JpaNotificationTemplateRepository templateRepo;

    @Mock
    JpaNotificationScheduleRepository scheduleRepo;

    @Mock
    PushPreferencePort pushPref;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    NotificationCommandService sut;

    @Test
    @DisplayName("미래 시각으로 공지 알림 예약 시 SCHEDULED 상태로 예약이 등록되고 응답이 반환된다")
    void reserveNotificationSuccessFutureTime() {
        // --- given ---
        Long templateId = 1L;
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);

        // 템플릿 엔티티
        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 제목")
                .templateBody("공지 내용")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        // scheduleRepo.save(...) 에 들어가는 값을 캡쳐
        ArgumentCaptor<NotificationSchedule> scheduleCaptor =
                ArgumentCaptor.forClass(NotificationSchedule.class);

        // save 호출 시, 전달된 엔티티를 그대로 반환하도록 설정 (id 는 null 이지만 테스트에는 큰 문제 없음)
        when(scheduleRepo.save(scheduleCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .scheduledAt(scheduledAt)
                .build();

        // --- when ---
        NotificationScheduleResponse response = sut.reserveNotification(templateId, request);

        // --- then ---

        // 1) 템플릿 조회 확인
        verify(templateRepo, times(1)).findById(templateId);

        // 2) scheduleRepo.save 호출 확인
        verify(scheduleRepo, times(1)).save(any(NotificationSchedule.class));

        NotificationSchedule savedSchedule = scheduleCaptor.getValue();
        assertNotNull(savedSchedule, "저장 대상 NotificationSchedule 은 null 이 아니어야 한다.");
        assertEquals(template, savedSchedule.getTemplate(), "예약에 사용된 템플릿이 일치해야 한다.");
        assertEquals(NotificationScheduleStatus.SCHEDULED, savedSchedule.getScheduleStatus());
        assertEquals(scheduledAt, savedSchedule.getScheduledAt());
        assertNull(savedSchedule.getSentAt(), "예약 시점에는 sentAt 이 null 이어야 한다.");
        assertEquals(YnType.no(), savedSchedule.getDeletedYn(), "예약은 삭제되지 않은 상태(YnType.N) 이어야 한다.");

        // 3) 응답 DTO 검증
        assertEquals(NotificationScheduleStatus.SCHEDULED, response.getScheduleStatus());
        assertEquals(scheduledAt, response.getScheduledAt());
        assertNull(response.getSentAt(), "응답의 sentAt 도 null 이어야 한다.");
    }

    @Test
    @DisplayName("scheduledAt 이 null 이면 IllegalArgumentException 이 발생한다")
    void reserveNotificationFailNullScheduledAt() {
        // --- given ---
        Long templateId = 1L;
        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .scheduledAt(null)
                .build();

        // --- when & then ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.reserveNotification(templateId, request)
        );

        assertEquals("예약 발송 시각은 현재 이후여야 합니다.", ex.getMessage());
        // 템플릿/리포지토리 호출이 없어야 함
        verifyNoInteractions(templateRepo, scheduleRepo);
    }

    @Test
    @DisplayName("scheduledAt 이 현재 시각 이전이면 IllegalArgumentException 이 발생한다")
    void reserveNotificationFailPastTime() {
        // --- given ---
        Long templateId = 1L;
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(1);

        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .scheduledAt(pastTime)
                .build();

        // --- when & then ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.reserveNotification(templateId, request)
        );

        assertEquals("예약 발송 시각은 현재 이후여야 합니다.", ex.getMessage());
        verifyNoInteractions(templateRepo, scheduleRepo);
    }

    @Test
    @DisplayName("예약 시각은 유효하지만 템플릿이 존재하지 않으면 IllegalArgumentException(템플릿이 존재하지 않습니다.) 을 던진다")
    void reserveNotificationTemplateNotFound() {
        // --- given ---
        Long templateId = 999L;
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);

        NotificationScheduleRequest request = NotificationScheduleRequest.builder()
                .scheduledAt(scheduledAt)
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        // --- when & then ---
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.reserveNotification(templateId, request)
        );

        assertEquals("템플릿이 존재하지 않습니다.", ex.getMessage());

        verify(templateRepo, times(1)).findById(templateId);
        verifyNoInteractions(scheduleRepo);
    }
}
