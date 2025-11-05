package com.adhd.ad_hell.domain.board.command.domain.repository;

import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository {


    // 도메인 계층에서는 JPA를 몰라야 해서 extends
    // JpaRepository 같은 구체 기술 의존을 없앤 순수 인터페이스로 선언

    // 게시글 저장
    Board save(Board board);
    // PK 기반 단건 조회
    Optional<Board> findById(Long id);
    // 게시굴 삭제
    void deleteById(Long id);
}
