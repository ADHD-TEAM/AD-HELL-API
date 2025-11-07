package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationTemplateUpdateRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationTemplateResponse;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceUpdateTemplateTest {

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
    @DisplayName("템플릿 수정 성공 – 엔티티가 수정되고 Response에 반영된다")
    void updateTemplateSuccess() {
        // given
        Long templateId = 1L;

        // 기존 템플릿 엔티티 (NORMAL / old title / old body)
        NotificationTemplate existing = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("이전 템플릿 제목")
                .templateBody("이전 템플릿 내용")
                .deletedYn(YnType.no())
                .build();

        // findById 호출 시 기존 엔티티 반환
        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existing));

        // 수정 요청 DTO
        NotificationTemplateUpdateRequest request = NotificationTemplateUpdateRequest.builder()
                .templateKind(NotificationTemplateKind.EVENT)
                .templateTitle("새 템플릿 제목")
                .templateBody("새 템플릿 내용")
                .build();

        // when
        NotificationTemplateResponse result = sut.updateTemplate(templateId, request);

        // then
        // 1) Repo findById 호출 검증
        verify(templateRepo, times(1)).findById(templateId);
        // Dirty checking 이므로 save() 를 직접 호출하지 않는 구현이라면 아래처럼 never()를 걸어도 됨
        verify(templateRepo, never()).save(any(NotificationTemplate.class));

        // 2) 엔티티가 실제로 수정되었는지 검증
        assertEquals(NotificationTemplateKind.EVENT, existing.getTemplateKind());
        assertEquals("새 템플릿 제목", existing.getTemplateTitle());
        assertEquals("새 템플릿 내용", existing.getTemplateBody());

        // 3) 반환 DTO 검증
        assertNotNull(result);
        assertEquals(existing.getId(), result.getTemplateId());      // id는 null일 수 있지만, 구조 검증용
        assertEquals(NotificationTemplateKind.EVENT, result.getTemplateKind());
        assertEquals("새 템플릿 제목", result.getTemplateTitle());
        assertEquals("새 템플릿 내용", result.getTemplateBody());
    }

    @Test
    @DisplayName("템플릿이 존재하지 않으면 IllegalArgumentException을 던진다")
    void updateTemplateNotFound() {
        // given
        Long templateId = 999L;
        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        NotificationTemplateUpdateRequest request = NotificationTemplateUpdateRequest.builder()
                .templateKind(NotificationTemplateKind.EVENT)
                .templateTitle("새 템플릿 제목")
                .templateBody("새 템플릿 내용")
                .build();

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.updateTemplate(templateId, request)
        );

        assertEquals("템플릿이 존재하지 않습니다.", ex.getMessage());
        verify(templateRepo, times(1)).findById(templateId);
        verify(templateRepo, never()).save(any(NotificationTemplate.class));
    }
}
