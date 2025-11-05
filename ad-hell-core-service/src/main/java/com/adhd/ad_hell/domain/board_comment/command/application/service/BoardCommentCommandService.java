package com.adhd.ad_hell.domain.board_comment.command.application.service;

import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentCreateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentUpdateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.response.BoardCommentCommandResponse;
import com.adhd.ad_hell.domain.board_comment.command.domain.aggregate.BoardComment;
import com.adhd.ad_hell.domain.board_comment.command.domain.repository.BoardCommentRepository;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BoardCommentCommandService {

    private final BoardCommentRepository boardCommentRepository;
    private final UserCommandRepository userRepository;
    private final BoardRepository boardRepository;

    /** 댓글 등록 */
    @Transactional
    public BoardCommentCommandResponse createBoardComment(BoardCommentCreateRequest req) {

        var user = userRepository.findById(req.getWriterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        var board = boardRepository.findById(req.getBoardId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        var comment = BoardComment.builder()
                .user(user)
                .board(board)
                .content(req.getContent())
                .build();

        var saved = boardCommentRepository.save(comment);

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

        var comment = boardCommentRepository.findById(commentId)
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

        var comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        comment.assertOwner(writerId);
        boardCommentRepository.delete(comment);
    }
}
