package com.adhd.ad_hell.domain.inquiry.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryAnswerRequest;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryCreateRequest;
import com.adhd.ad_hell.domain.inquiry.command.domain.aggregate.Inquiry;
import com.adhd.ad_hell.domain.inquiry.command.domain.repository.InquiryRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InquiryCommandServiceTest {

    @Mock private InquiryRepository inquiryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserProvider userProvider;        // ✅ 반드시 mock

    @InjectMocks
    private InquiryCommandService inquiryCommandService;

    // --- 편의용: 생성자/세터 없는 DTO 채우기 ---
    private static InquiryCreateRequest req(String title, String content, Long categoryId) {
        try {
            InquiryCreateRequest r = InquiryCreateRequest.class.getDeclaredConstructor().newInstance();
            set(r, "title", title);
            set(r, "content", content);
            set(r, "categoryId", categoryId);
            return r;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static InquiryAnswerRequest answerReq(String response) {
        try {
            InquiryAnswerRequest r = InquiryAnswerRequest.class.getDeclaredConstructor().newInstance();
            set(r, "response", response);
            return r;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void set(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Nested
    @DisplayName("createInquiry()")
    class CreateInquiryTests {

        @Test
        @DisplayName("성공 - 로그인 유저/카테고리 OK")
        void create_success() {
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                // given
                InquiryCreateRequest req = req("제목", "내용", 100L);

                LoginUserInfo login = mock(LoginUserInfo.class);
                given(login.getUserId()).willReturn(10L);
                mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(login);

                User user = mock(User.class);
                given(userProvider.getUserById(10L)).willReturn(user);

                Category category = Category.builder().name("카테고리").description("desc").build();
                given(categoryRepository.findById(100L)).willReturn(Optional.of(category));

                Inquiry saved = mock(Inquiry.class);
                given(saved.getId()).willReturn(1L);
                given(inquiryRepository.save(any(Inquiry.class))).willReturn(saved);

                // when
                Long id = inquiryCommandService.createInquiry(req);

                // then
                assertThat(id).isEqualTo(1L);
                verify(inquiryRepository).save(any(Inquiry.class));
            }
        }

        @Test
        @DisplayName("실패 - 로그인 유저 조회 실패 → USER_NOT_FOUND")
        void create_userNotFound() {
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                // given
                InquiryCreateRequest req = req("제목", "내용", 100L);

                LoginUserInfo login = mock(LoginUserInfo.class);
                given(login.getUserId()).willReturn(10L);
                mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(login);

                given(userProvider.getUserById(10L)).willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

                // when / then
                assertThatThrownBy(() -> inquiryCommandService.createInquiry(req))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
            }
        }

        @Test
        @DisplayName("실패 - 카테고리 없음 → CATEGORY_NOT_FOUND")
        void create_categoryNotFound() {
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                // given
                InquiryCreateRequest req = req("제목", "내용", 999L);

                LoginUserInfo login = mock(LoginUserInfo.class);
                given(login.getUserId()).willReturn(10L);
                mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(login);

                User user = mock(User.class);
                given(userProvider.getUserById(10L)).willReturn(user);

                given(categoryRepository.findById(999L)).willReturn(Optional.empty());

                // when / then
                assertThatThrownBy(() -> inquiryCommandService.createInquiry(req))
                        .isInstanceOf(BusinessException.class)
                        .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("answerInquiry()")
    class AnswerInquiryTests {

        @Test
        @DisplayName("성공 - 답변 등록/수정")
        void answer_success() {
            // given
            Long inquiryId = 1L;
            InquiryAnswerRequest req = answerReq("답변 내용");

            Inquiry inquiry = mock(Inquiry.class);
            given(inquiryRepository.findById(inquiryId)).willReturn(Optional.of(inquiry));

            // when
            inquiryCommandService.answerInquiry(inquiryId, req);

            // then
            verify(inquiryRepository).findById(inquiryId);
            verify(inquiry).answer("답변 내용");
        }

        @Test
        @DisplayName("실패 - 문의 없음 → INQUIRY_NOT_FOUND")
        void answer_notFound() {
            // given
            Long inquiryId = 404L;
            InquiryAnswerRequest req = answerReq("답변 내용");
            given(inquiryRepository.findById(inquiryId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> inquiryCommandService.answerInquiry(inquiryId, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
        }
    }
}
