package com.adhd.ad_hell.domain.board_comment.command.application.service;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentCreateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentUpdateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.response.BoardCommentCommandResponse;
import com.adhd.ad_hell.domain.board_comment.command.domain.aggregate.BoardComment;
import com.adhd.ad_hell.domain.board_comment.command.domain.repository.BoardCommentRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardCommentCommandServiceTest {

    @InjectMocks
    private BoardCommentCommandService boardCommentCommandService;


    @Mock
    private UserProvider userProvider;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardCommentRepository boardCommentRepository;

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createBoardComment_success() {
        // given
        long userId = 1L;
        long boardId = 10L;
        String content = "이것은 댓글 내용입니다.";

        BoardCommentCreateRequest request = BoardCommentCreateRequest.builder()
                .boardId(boardId)
                .content(content)
                .build();

        LoginUserInfo mockLoginUser = mock(LoginUserInfo.class);
        User mockUser = User.builder().userId(userId).build();
        Board mockBoard = Board.builder().id(boardId).build();
        BoardComment savedComment = BoardComment.builder()
                .id(100L)
                .user(mockUser)
                .board(mockBoard)
                .content(content)
                .build();

        // SecurityUtil의 static 메소드를 Mocking 하기 위해 try-with-resources 구문 사용
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {

            // Mock 동작 정의
            mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(mockLoginUser); // static 호출을 mocking
            when(mockLoginUser.getUserId()).thenReturn(userId);
            when(userProvider.getUserById(userId)).thenReturn(mockUser);
            when(boardRepository.findById(boardId)).thenReturn(Optional.of(mockBoard));
            when(boardCommentRepository.save(any(BoardComment.class))).thenReturn(savedComment);

            // when
            BoardCommentCommandResponse response = boardCommentCommandService.createBoardComment(request);

            // then
            assertNotNull(response);
            assertEquals(savedComment.getId(), response.getId());
            assertEquals(userId, response.getWriterId());
            assertEquals(boardId, response.getBoardId());
            assertEquals(content, response.getContent());

            // ArgumentCaptor를 사용한 검증 (이전과 동일)
            ArgumentCaptor<BoardComment> commentCaptor = ArgumentCaptor.forClass(BoardComment.class);
            verify(boardCommentRepository, times(1)).save(commentCaptor.capture());
            BoardComment capturedComment = commentCaptor.getValue();
            assertEquals(mockUser, capturedComment.getUser());
            assertEquals(mockBoard, capturedComment.getBoard());
            assertEquals(content, capturedComment.getContent());
        }
    }



    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateBoardComment_success() {
        // given
        long commentId = 1L;
        long writerId = 10L;
        String newContent = "수정된 댓글 내용입니다.";

        BoardCommentUpdateRequest request = BoardCommentUpdateRequest.builder()
                .writerId(writerId)
                .content(newContent)
                .build();

        // 원본 댓글의 User와 Board 객체 생성
        User originalWriter = User.builder().userId(writerId).build();
        Board board = Board.builder().id(100L).build();

        // findById로 찾아올 원본 댓글 객체 생성
        // spy를 사용하여 실제 updateContent 메소드가 호출되는지 추적
        BoardComment originalComment = spy(BoardComment.builder()
                .id(commentId)
                .user(originalWriter)
                .board(board)
                .content("원본 댓글 내용")
                .build());

        // boardCommentRepository가 originalComment를 반환하도록 설정
        when(boardCommentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));

        // when
        BoardCommentCommandResponse response = boardCommentCommandService.updateBoardComment(commentId, request);

        // then
        // 1. originalComment의 updateContent 메소드가 올바른 인자들로 호출되었는지 검증
        verify(originalComment, times(1)).updateContent(newContent, writerId);

        // 2. 반환된 응답 객체의 내용이 수정된 내용과 일치하는지 검증
        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals(writerId, response.getWriterId());
        assertEquals(newContent, response.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteBoardComment_success() {
        // given
        long commentId = 1L;
        long writerId = 10L;

        // 댓글 작성자 User 객체 생성
        User writer = User.builder().userId(writerId).build();

        // findById로 찾아올 댓글 객체 생성
        // spy를 사용하여 실제 assertOwner 메소드가 호출되는지 추적
        BoardComment mockComment = spy(BoardComment.builder()
                .id(commentId)
                .user(writer)
                .build());

        // boardCommentRepository가 mockComment를 반환하도록 설정
        when(boardCommentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // when
        boardCommentCommandService.deleteBoardComment(commentId, writerId);

        // then
        // 1. mockComment의 assertOwner 메소드가 올바른 인자로 호출되었는지 검증
        verify(mockComment, times(1)).assertOwner(writerId);

        // 2. boardCommentRepository.delete 메소드가 mockComment 객체로 호출되었는지 검증
        verify(boardCommentRepository, times(1)).delete(mockComment);
    }

}