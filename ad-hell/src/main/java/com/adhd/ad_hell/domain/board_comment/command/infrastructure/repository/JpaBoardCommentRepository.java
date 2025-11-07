package com.adhd.ad_hell.domain.board_comment.command.infrastructure.repository;

import com.adhd.ad_hell.domain.board_comment.command.domain.aggregate.BoardComment;
import com.adhd.ad_hell.domain.board_comment.command.domain.repository.BoardCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBoardCommentRepository extends BoardCommentRepository, JpaRepository<BoardComment, Long> {
}
