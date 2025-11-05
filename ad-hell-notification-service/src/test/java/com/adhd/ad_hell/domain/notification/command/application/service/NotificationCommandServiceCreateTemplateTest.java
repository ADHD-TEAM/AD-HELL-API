package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.application.controller.NotificationCommandController;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationTemplateCreateRequest;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceCreateTemplateTest {

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
    NotificationCommandService sut; // System Under Test

    @DisplayName("공지 알림 템플릿을 정상적으로 등록한다")
    @Test
    void NotificationTemplateCreateRequest() {
        // given
        NotificationTemplateCreateRequest request = NotificationTemplateCreateRequest.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 제목")
                .templateBody("공지 내용입니다.")
                .build();

        // templateRepo.save(...) 가 호출되면, 저장된 엔티티에 id 를 세팅해서 돌려주도록 스텁
        when(templateRepo.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> {
                    NotificationTemplate entity = invocation.getArgument(0);
                    // JPA가 해줄 일을 테스트에서 대신 해줌
                    ReflectionTestUtils.setField(entity, "id", 1L);
                    return entity;
                });

        // when
        NotificationTemplateResponse response = sut.createTemplate(request);

        // then
        // 1) save 에 들어간 엔티티가 올바른지 ArgumentCaptor 로 검증
        ArgumentCaptor<NotificationTemplate> captor =
                ArgumentCaptor.forClass(NotificationTemplate.class);

        verify(templateRepo, times(1)).save(captor.capture());
        NotificationTemplate savedEntity = captor.getValue();

        assertEquals(NotificationTemplateKind.NORMAL, savedEntity.getTemplateKind());
        assertEquals("공지 제목", savedEntity.getTemplateTitle());
        assertEquals("공지 내용입니다.", savedEntity.getTemplateBody());
        assertEquals(YnType.no(), savedEntity.getDeletedYn(), "생성 시 deletedYn 은 N 이어야 한다.");

        // 2) 반환값 검증
        assertNotNull(response);
        assertEquals(1L, response.getTemplateId());
        assertEquals(request.getTemplateKind(), response.getTemplateKind());
        assertEquals(request.getTemplateTitle(), response.getTemplateTitle());
        assertEquals(request.getTemplateBody(), response.getTemplateBody());
    }
}
