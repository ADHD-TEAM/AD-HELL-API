package com.adhd.ad_hell.domain.announcement.command.domain.repository;

import com.adhd.ad_hell.domain.announcement.command.domain.aggregate.Announcement;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementRepository {

    // 도메인 계층에서는 JPA를 몰라야 하므로
    // JpaRepository 같은 구체 기술 의존을 제거한 순수 인터페이스 선언

    // 공지 저장
    Announcement save(Announcement announcement);

    // PK 기반 단건 조회
    Optional<Announcement> findById(Long id);

    // 공지 삭제
    void deleteById(Long id);

    // 존재 여부 확인
    boolean existsById(Long id);
}
