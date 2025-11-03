package com.adhd.ad_hell.domain.board.command.infrastructure.repository;

import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBoardRepository extends BoardRepository, JpaRepository<Board, Long> {
}
