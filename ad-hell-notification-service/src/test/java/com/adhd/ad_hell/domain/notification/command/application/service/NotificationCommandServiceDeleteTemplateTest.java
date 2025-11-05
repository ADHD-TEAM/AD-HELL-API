package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationTemplate;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceDeleteTemplateTest {

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
    @DisplayName("템플릿 삭제 성공 – deletedYn 이 Y 로 변경된다 (soft delete)")
    void deleteTemplateSuccess() {
        // given
        Long templateId = 1L;

        NotificationTemplate existing = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 템플릿")
                .templateBody("내용입니다")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existing));

        // when
        sut.deleteTemplate(templateId);

        // then
        verify(templateRepo, times(1)).findById(templateId);
        // Dirty checking 기반이라면 save 호출은 필요 없음
        verify(templateRepo, never()).save(any(NotificationTemplate.class));

        // soft delete 되었는지 확인
        assertEquals(YnType.Y, existing.getDeletedYn());
    }

    @Test
    @DisplayName("템플릿이 존재하지 않으면 IllegalArgumentException을 던진다")
    void deleteTemplateNotFound() {
        // given
        Long templateId = 999L;
        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.deleteTemplate(templateId)
        );

        assertEquals("템플릿이 존재하지 않습니다.", ex.getMessage());
        verify(templateRepo, times(1)).findById(templateId);
        verify(templateRepo, never()).save(any(NotificationTemplate.class));
    }
}
