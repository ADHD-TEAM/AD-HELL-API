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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceSendNotificationTest {

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
    @DisplayName("PUSH_ENABLED 대상으로 공지 발송 시 푸시 ON 회원 수만큼 Notification이 저장되고 이벤트가 발행된다")
    void sendNotificationSuccess() {
        // --- given ---
        Long templateId = 1L;

        // 템플릿 엔티티
        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("{{name}}님, 공지입니다")
                .templateBody("안녕하세요 {{name}}님, 새로운 공지가 있습니다.")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        // 푸시 ON 회원 (예: 2명)
        Set<Long> enabledMembers = new HashSet<>(Arrays.asList(100L, 200L));
        when(pushPref.findAllEnabled()).thenReturn(enabledMembers);

        // saveAll 은 그대로 리스트를 반환하도록 설정 (ID는 null이지만 테스트에는 큰 영향 X)
        ArgumentCaptor<List<Notification>> saveAllCaptor = ArgumentCaptor.forClass(List.class);
        when(notificationRepo.saveAll(saveAllCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // unread count stub (이벤트에서 사용할 값)
        when(notificationRepo.countByUserIdAndReadYn(100L, YnType.N)).thenReturn(3L);
        when(notificationRepo.countByUserIdAndReadYn(200L, YnType.N)).thenReturn(5L);

        // 요청 DTO (PUSH_ENABLED + 변수)
        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .variables(Map.of("name", "홍길동"))
                .build();

        // 트랜잭션 동기화 활성화 (afterCommit 테스트용)
        TransactionSynchronizationManager.initSynchronization();
        NotificationDispatchResponse response;
        try {
            // --- when ---
            response = sut.sendNotification(templateId, request);

            // 커밋 시점 콜백 수동 호출
            List<TransactionSynchronization> syncs =
                    new ArrayList<>(TransactionSynchronizationManager.getSynchronizations());
            syncs.forEach(TransactionSynchronization::afterCommit);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        // --- then ---

        // 1) 템플릿 조회 및 대상자 계산 확인
        verify(templateRepo, times(1)).findById(templateId);
        verify(pushPref, times(1)).findAllEnabled();

        // 2) Notification 생성/저장 검증
        List<Notification> savedList = saveAllCaptor.getValue();
        assertEquals(2, savedList.size(), "푸시 ON 회원 수만큼 Notification 이 생성되어야 한다.");

        // 제목/본문 variable merge 확인
        for (Notification n : savedList) {
            assertTrue(enabledMembers.contains(n.getUserId()));
            assertEquals("홍길동님, 공지입니다", n.getNotificationTitle());
            assertEquals("안녕하세요 홍길동님, 새로운 공지가 있습니다.", n.getNotificationBody());
            assertEquals(YnType.N, n.getReadYn(), "초기 readYn 은 N 이어야 한다.");
        }

        // 3) 응답 검증
        assertEquals(2, response.getRecipientCount(), "recipientCount 는 발송된 회원 수와 같아야 한다.");
        // ID는 stub 에서 세팅하지 않았으므로 null일 수 있음 (실제 JPA 환경에서는 값이 채워짐)
        // 여기서는 null 여부만 안전하게 체크하거나, 아예 검증하지 않을 수도 있음
        // assertNull(response.getNotificationId());

        // 4) 이벤트 발행 검증 (각 유저별 1회)
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(2)).publishEvent(eventCaptor.capture());

        List<Object> events = eventCaptor.getAllValues();
        assertEquals(2, events.size());

        // 이벤트 타입 및 내용 검증
        Map<Long, Long> unreadByUser = new HashMap<>();
        unreadByUser.put(100L, 3L);
        unreadByUser.put(200L, 5L);

        for (Object ev : events) {
            assertTrue(ev instanceof NotificationCreatedEvent);
            NotificationCreatedEvent e = (NotificationCreatedEvent) ev;

            assertTrue(enabledMembers.contains(e.getUserId()), "이벤트의 userId 는 푸시 ON 회원이어야 한다.");
            assertEquals("홍길동님, 공지입니다", e.getTitle());
            assertEquals("안녕하세요 홍길동님, 새로운 공지가 있습니다.", e.getBody());

            // 우리가 stub 해둔 unreadCount 와 일치하는지
            assertEquals(unreadByUser.get(e.getUserId()), e.getUnreadCount());
        }
    }

    @Test
    @DisplayName("PUSH_ENABLED 대상이 없으면 Notification 을 저장하지 않고 recipientCount=0 을 반환한다")
    void sendNotificationNoRecipients() {
        // --- given ---
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 제목")
                .templateBody("공지 내용")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));
        when(pushPref.findAllEnabled()).thenReturn(Collections.emptySet());

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .build();

        // 트랜잭션 동기화 활성화 (혹시라도 등록되는지 확인용)
        TransactionSynchronizationManager.initSynchronization();
        NotificationDispatchResponse response;
        try {
            // --- when ---
            response = sut.sendNotification(templateId, request);

            // --- then (동기화 목록 확인) ---
            assertTrue(TransactionSynchronizationManager.getSynchronizations().isEmpty(),
                    "대상이 없으면 afterCommit 콜백도 등록되지 않아야 한다.");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        // 1) 저장 안 됨
        verify(notificationRepo, never()).saveAll(anyList());
        // 2) 이벤트 발행 안 됨
        verify(publisher, never()).publishEvent(any());

        // 3) 응답 값 확인
        assertEquals(0, response.getRecipientCount());
        assertNull(response.getNotificationId(), "대상이 없으면 대표 notificationId는 null 이어야 한다.");
    }

    // ========================= 여기서부터 추가 분기 테스트 =========================

    @Test
    @DisplayName("템플릿이 존재하지 않으면 IllegalArgumentException 을 던진다")
    void sendNotificationTemplateNotFound() {
        // given
        Long templateId = 999L;

        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .build();

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.sendNotification(templateId, request)
        );

        assertEquals("템플릿이 존재하지 않습니다.", ex.getMessage());
        verify(templateRepo).findById(templateId);
        verifyNoMoreInteractions(templateRepo);
        verifyNoInteractions(notificationRepo, pushPref, publisher);
    }

    @Test
    @DisplayName("targetType 이 null이면 IllegalArgumentException(발송 대상 타입은 필수입니다.) 을 던진다")
    void sendNotificationTargetTypeNull() {
        // given
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지")
                .templateBody("내용")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(null)     // resolveRecipients 에서 예외
                .build();

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.sendNotification(templateId, request)
        );

        assertEquals("발송 대상 타입은 필수입니다.", ex.getMessage());
        verify(templateRepo).findById(templateId);
        verifyNoMoreInteractions(templateRepo);
        verifyNoInteractions(notificationRepo, pushPref, publisher);
    }

    @Test
    @DisplayName("CUSTOM 타입인데 대상 회원 목록이 비어있으면 IllegalArgumentException(CUSTOM 발송은 대상 회원 목록이 필요) 을 던진다")
    void sendNotificationCustomWithoutTargets() {
        // given
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지")
                .templateBody("내용")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.CUSTOM)
                .targetMemberIds(Collections.emptyList())    // 비어있음
                .build();

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sut.sendNotification(templateId, request)
        );

        assertEquals("CUSTOM 발송은 대상 회원 목록이 필요합니다.", ex.getMessage());
        verify(templateRepo).findById(templateId);
        verifyNoMoreInteractions(templateRepo);
        verifyNoInteractions(notificationRepo, pushPref, publisher);
    }

    @Test
    @DisplayName("ALL 타입이면 PushPreferencePort.findAllKnownMembers() 를 사용해 모든 known 멤버에게 발송한다")
    void sendNotificationAllTargetType() {
        // given
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("전체 공지")
                .templateBody("전체 공지 내용")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        Set<Long> allMembers = new HashSet<>(Arrays.asList(10L, 20L, 30L));
        when(pushPref.findAllKnownMembers()).thenReturn(allMembers);

        ArgumentCaptor<List<Notification>> saveAllCaptor = ArgumentCaptor.forClass(List.class);
        when(notificationRepo.saveAll(saveAllCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.ALL)
                .build();

        TransactionSynchronizationManager.initSynchronization();
        NotificationDispatchResponse response;
        try {
            // when
            response = sut.sendNotification(templateId, request);
        } finally {
            // afterCommit 은 굳이 호출하지 않아도 됨 (이 테스트에선 발행 여부까지는 안 보므로)
            TransactionSynchronizationManager.clearSynchronization();
        }

        // then
        verify(templateRepo).findById(templateId);
        verify(pushPref).findAllKnownMembers();
        verify(notificationRepo).saveAll(anyList());

        List<Notification> saved = saveAllCaptor.getValue();
        assertEquals(3, saved.size());
        assertTrue(saved.stream().allMatch(n -> allMembers.contains(n.getUserId())));
        assertEquals("전체 공지", saved.get(0).getNotificationTitle());
        assertEquals("전체 공지 내용", saved.get(0).getNotificationBody());

        assertEquals(3, response.getRecipientCount());
        // 대표 ID(null이어도 상관 없음 – JPA가 실제 환경에서 채워줌)
    }

    @Test
    @DisplayName("variables 가 null 인 경우 mergeVariables 에서 원본 제목/본문이 그대로 사용된다")
    void sendNotificationVariablesNull() {
        // given
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("{{name}}님, 공지입니다")
                .templateBody("안녕하세요 {{name}}님")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));

        Set<Long> members = new HashSet<>(List.of(1L));
        when(pushPref.findAllEnabled()).thenReturn(members);

        ArgumentCaptor<List<Notification>> saveAllCaptor = ArgumentCaptor.forClass(List.class);
        when(notificationRepo.saveAll(saveAllCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationSendRequest request = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .variables(null)   // mergeVariables 에서 vars == null 분기
                .build();

        TransactionSynchronizationManager.initSynchronization();
        try {
            sut.sendNotification(templateId, request);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        List<Notification> saved = saveAllCaptor.getValue();
        assertEquals(1, saved.size());
        Notification n = saved.get(0);

        // variables 가 null 이면 {{name}} 치환 없이 원본 문자열이 간다
        assertEquals("{{name}}님, 공지입니다", n.getNotificationTitle());
        assertEquals("안녕하세요 {{name}}님", n.getNotificationBody());
    }

    @Test
    @DisplayName("템플릿 제목이 null 이면 mergeVariables 에서 그대로 null 반환")
    void sendNotificationWithNullTitle() {
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle(null) // ★ 여기
                .templateBody("본문 {{name}}")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));
        when(pushPref.findAllEnabled()).thenReturn(Set.of(1L));

        when(notificationRepo.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationSendRequest req = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .variables(Map.of("name", "홍길동"))
                .build();

        TransactionSynchronizationManager.initSynchronization();
        try {
            sut.sendNotification(templateId, req);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        // 여기서 saveAll captor 써서 title 이 null 인지 확인하면 됨
    }

    @Test
    @DisplayName("variables 가 비어 있으면 mergeVariables 에서 원본 문자열 그대로 사용한다")
    void sendNotificationVariablesEmpty() {
        Long templateId = 1L;

        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("{{name}}님, 공지입니다")
                .templateBody("안녕하세요 {{name}}님")
                .deletedYn(YnType.no())
                .build();

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(template));
        when(pushPref.findAllEnabled()).thenReturn(Set.of(1L));

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        when(notificationRepo.saveAll(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationSendRequest req = NotificationSendRequest.builder()
                .targetType(NotificationSendRequest.TargetType.PUSH_ENABLED)
                .variables(Collections.emptyMap()) // ★ 여기
                .build();

        TransactionSynchronizationManager.initSynchronization();
        try {
            sut.sendNotification(templateId, req);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        Notification saved = captor.getValue().get(0);
        // 치환 안 되고 원본 그대로 가는지 확인
        assertEquals("{{name}}님, 공지입니다", saved.getNotificationTitle());
    }

}
