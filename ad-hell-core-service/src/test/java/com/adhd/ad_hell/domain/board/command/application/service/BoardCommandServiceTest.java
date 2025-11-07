package com.adhd.ad_hell.domain.board.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardCreateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardUpdateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.response.BoardCommandResponse;
import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BoardCommandServiceTest {

    @Mock private BoardRepository boardRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserProvider userProvider;
    @Mock private SecurityUtil securityUtil; // ✅ 인스턴스 빈(정적 아님)

    @InjectMocks
    private BoardCommandService service;

    private void mockLogin(long userId) {
        LoginUserInfo info = mock(LoginUserInfo.class);
        when(info.getUserId()).thenReturn(userId);
        when(securityUtil.getLoginUserInfo()).thenReturn(info);
    }

    @Nested
    @DisplayName("createBoard()")
    class CreateTests {

        @Test
        @DisplayName("성공 - 파일 없음")
        void create_success() {
            BoardCreateRequest req = BoardCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .writerId(10L)      // 서비스는 실제로 securityUtil에서 id를 읽음
                    .categoryId(100L)
                    .status("Y")
                    .build();

            mockLogin(10L);

            User writer = mock(User.class);
            Category category = mock(Category.class);
            when(writer.getUserId()).thenReturn(10L);
            when(category.getId()).thenReturn(100L);

            given(userProvider.getUserById(10L)).willReturn(writer);
            given(categoryRepository.findById(100L)).willReturn(Optional.of(category));
            // 저장 시, 서비스가 만든 엔티티를 그대로 반환시키면 응답 변환이 안전
            given(boardRepository.save(any(Board.class))).willAnswer(invocation -> invocation.getArgument(0));

            TransactionSynchronizationManager.initSynchronization();
            try {
                BoardCommandResponse res = service.createBoard(req, Collections.<MultipartFile>emptyList());

                assertThat(res.getTitle()).isEqualTo("제목");
                assertThat(res.getWriterId()).isEqualTo(10L);
                assertThat(res.getCategoryId()).isEqualTo(100L);

                verify(securityUtil).getLoginUserInfo();
                verify(userProvider).getUserById(10L);
                verify(categoryRepository).findById(100L);
                verify(boardRepository).save(any(Board.class));
            } finally {
                TransactionSynchronizationManager.clearSynchronization();
            }
        }

        @Test
        @DisplayName("실패 - 카테고리 없음 → CATEGORY_NOT_FOUND")
        void create_categoryNotFound() {
            BoardCreateRequest req = BoardCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .writerId(10L)
                    .categoryId(999L)
                    .status("Y")
                    .build();

            mockLogin(10L);
            given(userProvider.getUserById(10L)).willReturn(mock(User.class));
            given(categoryRepository.findById(999L)).willReturn(Optional.empty());

            TransactionSynchronizationManager.initSynchronization();
            try {
                assertThatThrownBy(() -> service.createBoard(req, Collections.emptyList()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
            } finally {
                TransactionSynchronizationManager.clearSynchronization();
            }

            verify(userProvider).getUserById(10L);
            verify(categoryRepository).findById(999L);
            verify(boardRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 작성자 없음 → USER_NOT_FOUND")
        void create_userNotFound() {
            BoardCreateRequest req = BoardCreateRequest.builder()
                    .title("제목")
                    .content("내용")
                    .writerId(404L)
                    .categoryId(100L) // 존재하도록
                    .status("Y")
                    .build();

            mockLogin(404L);
            given(categoryRepository.findById(100L)).willReturn(Optional.of(mock(Category.class)));
            given(userProvider.getUserById(404L)).willReturn(null);

            TransactionSynchronizationManager.initSynchronization();
            try {
                assertThatThrownBy(() -> service.createBoard(req, Collections.emptyList()))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
            } finally {
                TransactionSynchronizationManager.clearSynchronization();
            }

            verify(categoryRepository).findById(100L);
            verify(userProvider).getUserById(404L);
            verify(boardRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateBoard()")
    class UpdateTests {

        @Test
        @DisplayName("실패 - 게시글 없음 → BOARD_NOT_FOUND")
        void update_notFound() {
            Long id = 999L;
            given(boardRepository.findById(id)).willReturn(Optional.empty());

            BoardUpdateRequest req = BoardUpdateRequest.builder()
                    .title("수정제목")
                    .content("수정내용")
                    .status("N")
                    .categoryId(100L)
                    .build();

            assertThatThrownBy(() -> service.updateBoard(id, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_NOT_FOUND.getMessage());

            verify(boardRepository).findById(id);
        }

        @Test
        @DisplayName("성공 - 부분수정(null은 유지)")
        void update_success_partial() {
            Long id = 1L;
            Board board = Board.builder()
                    .id(id)
                    .title("원제목")
                    .content("원내용")
                    .status("Y")
                    .viewCount(0L)
                    .build();

            given(boardRepository.findById(id)).willReturn(Optional.of(board));

            BoardUpdateRequest req = BoardUpdateRequest.builder()
                    .title("수정제목")
                    .content(null)
                    .status(null)
                    .categoryId(null)
                    .build();

            service.updateBoard(id, req);

            assertThat(board.getTitle()).isEqualTo("수정제목");
            assertThat(board.getContent()).isEqualTo("원내용");
            assertThat(board.getStatus()).isEqualTo("Y");
            verify(boardRepository).findById(id);
        }

        @Test
        @DisplayName("실패 - 카테고리 변경 요청 but 없음 → CATEGORY_NOT_FOUND")
        void update_categoryNotFound() {
            Long id = 1L;
            Board board = Board.builder()
                    .id(id)
                    .title("원제목")
                    .content("원내용")
                    .status("Y")
                    .build();

            given(boardRepository.findById(id)).willReturn(Optional.of(board));
            given(categoryRepository.findById(777L)).willReturn(Optional.empty());

            BoardUpdateRequest req = BoardUpdateRequest.builder()
                    .categoryId(777L)
                    .build();

            assertThatThrownBy(() -> service.updateBoard(id, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());

            verify(boardRepository).findById(id);
            verify(categoryRepository).findById(777L);
        }
    }

    @Nested
    @DisplayName("deleteBoard()")
    class DeleteTests {

        @Test
        @DisplayName("실패 - 게시글 없음 → BOARD_NOT_FOUND")
        void delete_notFound() {
            Long id = 404L;
            given(boardRepository.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteBoard(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_NOT_FOUND.getMessage());

            verify(boardRepository).findById(id);
            verify(boardRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("성공 - 파일 목록이 null이어도 NPE 없이 삭제")
        void delete_success() {
            Long id = 1L;

            // Board를 목킹하여 getFiles()가 null이 되도록(서비스가 null-safe 처리했는지 확인)
            Board board = mock(Board.class);
            when(board.getFiles()).thenReturn(null);

            given(boardRepository.findById(id)).willReturn(Optional.of(board));
            willDoNothing().given(boardRepository).deleteById(id);

            service.deleteBoard(id);

            verify(boardRepository).findById(id);
            verify(boardRepository).deleteById(id);
        }
    }
}
