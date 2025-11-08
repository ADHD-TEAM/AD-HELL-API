package com.adhd.ad_hell.domain.announcement.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementUpdateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.response.AnnouncementCommandResponse;
import com.adhd.ad_hell.domain.announcement.command.domain.aggregate.Announcement;
import com.adhd.ad_hell.domain.announcement.command.domain.repository.AnnouncementRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnnouncementCommandServiceTest {

    @Mock private AnnouncementRepository announcementRepository;
    // 현재 서비스 생성자에 포함돼 있다면 유지, 아니라면 제거 OK
    @Mock private UserCommandRepository userRepository;
    @Mock private UserProvider userProvider;

    @InjectMocks
    private AnnouncementCommandService service;

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("status가 null이면 기본값 'Y'로 저장되고 응답에도 반영된다")
        void create_defaultStatusY() {
            // given
            AnnouncementCreateRequest req = AnnouncementCreateRequest.builder()
                    .title("공지 제목")
                    .content("내용")
                    .status(null) // 기본값 로직 검증
                    .build();

            try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
                LoginUserInfo login = mock(LoginUserInfo.class);
                when(login.getUserId()).thenReturn(10L);
                mocked.when(SecurityUtil::getLoginUserInfo).thenReturn(login);

                User writer = mock(User.class);
                given(userProvider.getUserById(10L)).willReturn(writer);

                ArgumentCaptor<Announcement> entityCaptor = ArgumentCaptor.forClass(Announcement.class);

                Announcement saved = Announcement.builder()
                        .writer(writer)
                        .title("공지 제목")
                        .content("내용")
                        .status("Y")
                        .build();
                given(announcementRepository.save(any(Announcement.class))).willReturn(saved);

                // when
                AnnouncementCommandResponse res = service.create(req);

                // then
                verify(announcementRepository).save(entityCaptor.capture());
                Announcement toSave = entityCaptor.getValue();
                assertThat(toSave.getStatus()).isEqualTo("Y");
                assertThat(toSave.getTitle()).isEqualTo("공지 제목");
                assertThat(toSave.getContent()).isEqualTo("내용");
                assertThat(toSave.getWriter()).isSameAs(writer);

                assertThat(res.getStatus()).isEqualTo("Y");
                mocked.verify(SecurityUtil::getLoginUserInfo, times(1));
            }
        }

        @Test
        @DisplayName("status가 'N'으로 들어오면 그대로 저장/반영된다")
        void create_statusN() {
            // given
            AnnouncementCreateRequest req = AnnouncementCreateRequest.builder()
                    .title("점검 안내")
                    .content("점검 예정입니다")
                    .status("N")
                    .build();

            try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
                LoginUserInfo login = mock(LoginUserInfo.class);
                when(login.getUserId()).thenReturn(20L);
                mocked.when(SecurityUtil::getLoginUserInfo).thenReturn(login);

                User writer = mock(User.class);
                given(userProvider.getUserById(20L)).willReturn(writer);

                Announcement saved = Announcement.builder()
                        .writer(writer)
                        .title("점검 안내")
                        .content("점검 예정입니다")
                        .status("N")
                        .build();
                given(announcementRepository.save(any(Announcement.class))).willReturn(saved);

                // when
                AnnouncementCommandResponse res = service.create(req);

                // then
                assertThat(res.getStatus()).isEqualTo("N");
                verify(announcementRepository).save(any(Announcement.class));
                mocked.verify(SecurityUtil::getLoginUserInfo, times(1));
            }
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("수정 대상이 없으면 ANNOUNCEMENT_NOT_FOUND")
        void update_notFound() {
            // given
            Long id = 999L;
            given(announcementRepository.findById(id)).willReturn(Optional.empty());

            AnnouncementUpdateRequest req = AnnouncementUpdateRequest.builder()
                    .title("수정 제목")
                    .content("수정 내용")
                    .status("Y")
                    .build();

            // when & then
            assertThatThrownBy(() -> service.update(id, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ANNOUNCEMENT_NOT_FOUND.getMessage());

            verify(announcementRepository).findById(id);
            verifyNoMoreInteractions(announcementRepository);
        }

        @Test
        @DisplayName("일부 필드만 수정 요청 시, null이 아닌 필드만 반영된다")
        void update_partial() {
            // given
            Long id = 1L;

            User writer = mock(User.class);
            Announcement ann = Announcement.builder()
                    .writer(writer)
                    .title("원래 제목")
                    .content("원래 내용")
                    .status("Y")
                    .build();

            given(announcementRepository.findById(id)).willReturn(Optional.of(ann));

            AnnouncementUpdateRequest req = AnnouncementUpdateRequest.builder()
                    .title("새 제목")   // 변경
                    .content(null)     // 유지
                    .status(null)      // 유지
                    .build();

            // when
            AnnouncementCommandResponse res = service.update(id, req);

            // then
            assertThat(ann.getTitle()).isEqualTo("새 제목");
            assertThat(ann.getContent()).isEqualTo("원래 내용");
            assertThat(ann.getStatus()).isEqualTo("Y");

            assertThat(res.getTitle()).isEqualTo("새 제목");
            assertThat(res.getContent()).isEqualTo("원래 내용");
            assertThat(res.getStatus()).isEqualTo("Y");

            verify(announcementRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("존재하지 않으면 ANNOUNCEMENT_NOT_FOUND")
        void delete_notFound() {
            // given
            Long id = 404L;
            given(announcementRepository.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ANNOUNCEMENT_NOT_FOUND.getMessage());

            verify(announcementRepository).existsById(id);
            verify(announcementRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("정상 삭제")
        void delete_success() {
            // given
            Long id = 7L;
            given(announcementRepository.existsById(id)).willReturn(true);
            willDoNothing().given(announcementRepository).deleteById(id);

            // when
            service.delete(id);

            // then
            verify(announcementRepository).existsById(id);
            verify(announcementRepository).deleteById(id);
        }
    }
}
