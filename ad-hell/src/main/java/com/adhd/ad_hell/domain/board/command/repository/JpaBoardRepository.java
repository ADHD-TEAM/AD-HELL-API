package com.adhd.ad_hell.domain.board.command.repository;

import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JpaBoardRepository extends BoardRepository, JpaRepository<Board, Long> {
}
