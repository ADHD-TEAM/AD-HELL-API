package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdCommentRepository extends AdCommentRepository , JpaRepository<AdComment,Long> {
}
