package com.adhd.ad_hell.domain.board_comment.command.application.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentCreateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentUpdateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.response.BoardCommentCommandResponse;
import com.adhd.ad_hell.domain.board_comment.command.domain.aggregate.BoardComment;
import com.adhd.ad_hell.domain.board_comment.command.domain.repository.BoardCommentRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardCommentCommandService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final SecurityUtil securityUtil;
    private final UserProvider userProvider;

    /** 댓글 등록 */
    @Transactional
    public BoardCommentCommandResponse createBoardComment(BoardCommentCreateRequest req) {

        Long userId = securityUtil.getLoginUserInfo().getUserId();
        User user = userProvider.getUserById(userId);

        // 게시글 조회
        com.adhd.ad_hell.domain.board.command.domain.aggregate.Board board =
                boardRepository.findById(req.getBoardId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 댓글 생성
        BoardComment comment = BoardComment.builder()
                .user(user)
                .board(board)
                .content(req.getContent())
                .build();

        BoardComment saved = boardCommentRepository.save(comment);

        return BoardCommentCommandResponse.builder()
                .id(saved.getId())
                .writerId(saved.getUser().getUserId())
                .boardId(saved.getBoard().getId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    /** 댓글 수정 */
    @Transactional
    public BoardCommentCommandResponse updateBoardComment(Long commentId, BoardCommentUpdateRequest req) {

        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        comment.updateContent(req.getContent(), req.getWriterId());

        return BoardCommentCommandResponse.builder()
                .id(comment.getId())
                .writerId(comment.getUser().getUserId())
                .boardId(comment.getBoard().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /** 댓글 삭제 */
    @Transactional
    public void deleteBoardComment(Long commentId, Long writerId) {

        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        comment.assertOwner(writerId);
        boardCommentRepository.delete(comment);
    }
}
