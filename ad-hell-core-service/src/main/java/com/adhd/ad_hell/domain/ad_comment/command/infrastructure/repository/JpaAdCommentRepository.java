package com.adhd.ad_hell.domain.ad_comment.command.infrastructure.repository;

import com.adhd.ad_hell.domain.ad_comment.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.ad_comment.command.domain.repository.AdCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdCommentRepository extends AdCommentRepository , JpaRepository<AdComment,Long> {
}
