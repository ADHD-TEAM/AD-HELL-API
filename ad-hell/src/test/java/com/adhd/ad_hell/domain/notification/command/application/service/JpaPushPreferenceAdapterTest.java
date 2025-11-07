    package com.adhd.ad_hell.domain.notification.command.application.service;

    import com.adhd.ad_hell.domain.notification.command.domain.aggregate.MemberPushPreference;
    import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
    import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaMemberPushPreferenceRepository;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.*;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.util.List;
    import java.util.Optional;
    import java.util.Set;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class JpaPushPreferenceAdapterTest {

        @Mock
        JpaMemberPushPreferenceRepository repo;

        @InjectMocks
        JpaPushPreferenceAdapter sut;

        @Test
        @DisplayName("신규 회원의 푸시 설정을 ON 으로 등록하면 Y 상태로 insert 된다")
        void setEnabledNewMemberOn() {
            // given
            Long memberId = 1L;

            when(repo.findById(memberId)).thenReturn(Optional.empty());
            when(repo.existsById(memberId)).thenReturn(false);
            ArgumentCaptor<MemberPushPreference> captor =
                    ArgumentCaptor.forClass(MemberPushPreference.class);

            when(repo.save(captor.capture()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            sut.setEnabled(memberId, true);

            // then
            verify(repo, times(1)).findById(memberId);
            verify(repo, times(1)).existsById(memberId);
            verify(repo, times(1)).save(any(MemberPushPreference.class));
            verifyNoMoreInteractions(repo);

            MemberPushPreference saved = captor.getValue();
            assertNotNull(saved);
            assertEquals(memberId, saved.getMemberId());
            assertEquals(YnType.Y, saved.getPushEnabled());
            assertTrue(saved.isEnabled(), "푸시 설정은 enabled(true) 여야 한다.");
        }

        @Test
        @DisplayName("기존 회원의 푸시 설정을 OFF 로 변경하면 N 상태로 update 된다")
        void setEnabledExistingMemberOff() {
            // given
            Long memberId = 2L;
            // 기존에 Y로 저장되어 있다고 가정
            MemberPushPreference existing = MemberPushPreference.create(memberId, true);

            when(repo.findById(memberId)).thenReturn(Optional.of(existing));
            when(repo.existsById(memberId)).thenReturn(true);

            ArgumentCaptor<MemberPushPreference> captor =
                    ArgumentCaptor.forClass(MemberPushPreference.class);

            when(repo.save(captor.capture()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            sut.setEnabled(memberId, false);

            // then
            verify(repo, times(1)).findById(memberId);
            verify(repo, times(1)).existsById(memberId);
            verify(repo, times(1)).save(any(MemberPushPreference.class));
            verifyNoMoreInteractions(repo);

            MemberPushPreference saved = captor.getValue();
            assertNotNull(saved);
            assertEquals(memberId, saved.getMemberId());
            assertEquals(YnType.N, saved.getPushEnabled());
            assertFalse(saved.isEnabled(), "푸시 설정은 disabled(false) 여야 한다.");
        }

        @Test
        @DisplayName("isEnabled - 레코드가 Y 이면 true 를 반환한다")
        void isEnabledExistingYTrue() {
            // given
            Long memberId = 3L;
            MemberPushPreference pref = MemberPushPreference.create(memberId, true);
            when(repo.findById(memberId)).thenReturn(Optional.of(pref));

            // when
            boolean enabled = sut.isEnabled(memberId);

            // then
            assertTrue(enabled);
            verify(repo, times(1)).findById(memberId);
            verifyNoMoreInteractions(repo);
        }

        @Test
        @DisplayName("isEnabled - 레코드가 없으면 on default 로 true 를 반환한다")
        void isEnabledNoRecordDefaultTrue() {
            // given
            Long memberId = 4L;
            when(repo.findById(memberId)).thenReturn(Optional.empty());

            // when
            boolean enabled = sut.isEnabled(memberId);

            // then
            assertTrue(enabled, "레코드가 없으면 on default 로 true가 되어야 한다.");
            verify(repo, times(1)).findById(memberId);
            verifyNoMoreInteractions(repo);
        }

        @Test
        @DisplayName("findAllEnabled - push_enabled=Y 인 회원만 반환한다")
        void findAllEnabled() {
            // given
            MemberPushPreference m1 = MemberPushPreference.create(10L, true);  // Y
            MemberPushPreference m2 = MemberPushPreference.create(11L, false); // N (실제로는 findByPushEnabled 에 안 나와야 함)
            when(repo.findByPushEnabled(YnType.Y)).thenReturn(List.of(m1));

            // when
            Set<Long> enabled = sut.findAllEnabled();

            // then
            assertEquals(Set.of(10L), enabled);
            verify(repo, times(1)).findByPushEnabled(YnType.Y);
            verifyNoMoreInteractions(repo);
        }

        @Test
        @DisplayName("findAllKnownMembers - 저장소에 존재하는 모든 회원 ID 를 반환한다")
        void findAllKnownMembers() {
            // given
            MemberPushPreference m1 = MemberPushPreference.create(20L, true);
            MemberPushPreference m2 = MemberPushPreference.create(21L, false);
            when(repo.findAll()).thenReturn(List.of(m1, m2));

            // when
            Set<Long> all = sut.findAllKnownMembers();

            // then
            assertEquals(Set.of(20L, 21L), all);
            verify(repo, times(1)).findAll();
            verifyNoMoreInteractions(repo);
        }
    }
