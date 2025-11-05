package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationPushToggleRequest;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceUpdatePushSettingTest {

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
    @DisplayName("푸시 ON 요청 시 pushPref.setEnabled(memberId, true)가 호출된다")
    void updatePushSettingOn() {
        // given
        Long memberId = 100L;
        NotificationPushToggleRequest request = NotificationPushToggleRequest.builder()
                .memberId(memberId)
                .pushEnabled(true)
                .build();

        // when
        sut.updatePushSetting(request);

        // then
        verify(pushPref, times(1))
                .setEnabled(memberId, true);

        verifyNoMoreInteractions(pushPref);
    }

    @Test
    @DisplayName("푸시 OFF 요청 시 pushPref.setEnabled(memberId, false)가 호출된다")
    void updatePushSettingOff() {
        // given
        Long memberId = 101L;
        NotificationPushToggleRequest request = NotificationPushToggleRequest.builder()
                .memberId(memberId)
                .pushEnabled(false)
                .build();

        // when
        sut.updatePushSetting(request);

        // then
        verify(pushPref, times(1))
                .setEnabled(memberId, false);

        verifyNoMoreInteractions(pushPref);
    }

    @Test
    @DisplayName("pushEnabled 가 null이면 Boolean.TRUE.equals(null) 이므로 false 로 간주된다")
    void updatePushSettingNullTreatedAsFalse() {
        // given
        Long memberId = 102L;
        NotificationPushToggleRequest request = NotificationPushToggleRequest.builder()
                .memberId(memberId)
                .pushEnabled(null)   // @Valid 레이어를 우회한 상황을 가정 (방어 테스트)
                .build();

        // when
        sut.updatePushSetting(request);

        // then
        verify(pushPref, times(1))
                .setEnabled(memberId, false);

        verifyNoMoreInteractions(pushPref);
    }
}
