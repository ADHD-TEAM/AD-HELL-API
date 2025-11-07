package com.adhd.ad_hell.domain.board_comment.command.domain.repository;

import com.adhd.ad_hell.domain.board_comment.command.domain.aggregate.BoardComment;

import java.util.Optional;

public interface BoardCommentRepository {

    BoardComment save(BoardComment boardComment);
    Optional<BoardComment> findById(Long id);
    void delete(BoardComment boardComment);
}
