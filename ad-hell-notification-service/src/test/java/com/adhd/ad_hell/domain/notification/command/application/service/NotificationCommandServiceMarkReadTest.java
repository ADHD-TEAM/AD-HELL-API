package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.Notification;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationReadEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationScheduleRepository;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationTemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceMarkReadTest {

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

    @AfterEach
    void tearDown() {
        // 혹시 남아 있을 수 있는 트랜잭션 동기화 상태 정리
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("본인 알림을 읽음 처리하면 readYn=Y 로 변경되고 AFTER_COMMIT 시 NotificationReadEvent 가 발행된다")
    void markReadSuccess() {
        // given
        Long userId = 1L;
        Long notificationId = 10L;

        Notification n = Notification.builder()
                .userId(userId)
                .notificationTitle("제목")
                .notificationBody("내용")
                .readYn(YnType.no())    // 아직 안 읽은 상태
                .build();

        when(notificationRepo.findById(notificationId))
                .thenReturn(Optional.of(n));

        when(notificationRepo.countByUserIdAndReadYn(userId, YnType.N))
                .thenReturn(3L);   // 미읽음 개수 stub

        // @Transactional 환경에서는 자동으로 활성화되지만,
        // 단위 테스트에서는 직접 켜줘야 registerSynchronization 를 사용할 수 있음
        TransactionSynchronizationManager.initSynchronization();

        // when
        sut.markRead(userId, notificationId);

        // then - 트랜잭션 커밋 전 상태 검증
        assertEquals(YnType.Y, n.getReadYn(), "readYn 이 Y 로 변경되어야 한다.");

        // AFTER_COMMIT 콜백 직접 호출
        List<TransactionSynchronization> syncs =
                new ArrayList<>(TransactionSynchronizationManager.getSynchronizations());
        syncs.forEach(TransactionSynchronization::afterCommit);

        // 이벤트 발행 검증
        ArgumentCaptor<NotificationReadEvent> eventCaptor =
                ArgumentCaptor.forClass(NotificationReadEvent.class);

        verify(publisher, times(1))
                .publishEvent(eventCaptor.capture());

        NotificationReadEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(userId, event.getUserId());
        assertEquals(3L, event.getUnreadCount());
    }

    @Test
    @DisplayName("이미 읽은 알림을 다시 읽음 처리해도 상태는 유지되고 AFTER_COMMIT 시 NotificationReadEvent 는 발행된다")
    void markReadAlreadyRead() {
        // given
        Long userId = 2L;
        Long notificationId = 20L;

        Notification n = Notification.builder()
                .userId(userId)
                .notificationTitle("이미 읽은 알림")
                .notificationBody("내용")
                .readYn(YnType.yes())    // 이미 Y 상태
                .build();

        when(notificationRepo.findById(notificationId))
                .thenReturn(Optional.of(n));

        when(notificationRepo.countByUserIdAndReadYn(userId, YnType.N))
                .thenReturn(0L);   // 미읽음 없음 가정

        TransactionSynchronizationManager.initSynchronization();

        // when
        sut.markRead(userId, notificationId);

        // then - 상태값 변경 없음
        assertEquals(YnType.Y, n.getReadYn(), "이미 읽은 알림은 그대로 Y 여야 한다.");

        // AFTER_COMMIT 실행
        List<TransactionSynchronization> syncs =
                new ArrayList<>(TransactionSynchronizationManager.getSynchronizations());
        syncs.forEach(TransactionSynchronization::afterCommit);

        ArgumentCaptor<NotificationReadEvent> eventCaptor =
                ArgumentCaptor.forClass(NotificationReadEvent.class);

        verify(publisher, times(1))
                .publishEvent(eventCaptor.capture());

        NotificationReadEvent event = eventCaptor.getValue();
        assertEquals(userId, event.getUserId());
        assertEquals(0L, event.getUnreadCount(), "미읽음 개수 stub 값과 동일해야 한다.");
    }

    @Test
    @DisplayName("알림이 존재하지 않으면 IllegalArgumentException 을 던진다")
    void markReadNotificationNotFound() {
        // given
        Long userId = 1L;
        Long notificationId = 999L;

        when(notificationRepo.findById(notificationId))
                .thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.markRead(userId, notificationId)
        );

        assertEquals("알림이 존재하지 않습니다.", ex.getMessage());
        verify(notificationRepo, times(1)).findById(notificationId);
        // registerSynchronization 까지 가지 않으므로 TransactionSynchronizationManager 활성화 불필요
        verifyNoMoreInteractions(notificationRepo);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("다른 사용자의 알림은 읽음 처리할 수 없으며 IllegalStateException 을 던진다")
    void markReadOtherUserNotification() {
        // given
        Long userId = 1L;
        Long notificationId = 30L;
        Long otherUserId = 999L;

        Notification n = Notification.builder()
                .userId(otherUserId)  // 다른 사람 것
                .notificationTitle("남의 알림")
                .notificationBody("내용")
                .readYn(YnType.no())
                .build();

        when(notificationRepo.findById(notificationId))
                .thenReturn(Optional.of(n));

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> sut.markRead(userId, notificationId)
        );

        assertEquals("본인 알림만 읽음 처리할 수 있습니다.", ex.getMessage());
        verify(notificationRepo, times(1)).findById(notificationId);
        // userId 불일치에서 바로 예외가 나므로 뒤 로직(카운트/이벤트)은 호출되지 않음
        verifyNoMoreInteractions(notificationRepo);
        verifyNoInteractions(publisher);
    }
}
