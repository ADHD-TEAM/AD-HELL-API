package com.adhd.ad_hell.domain.user.command.repository;

import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCommandRepository extends JpaRepository<User, Long> {
    // 사용가능한 닉네임인지 확인
    Boolean existsByloginId(String loginId);
    Boolean existsByNickname(String nickname);

    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.userId = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);
}
