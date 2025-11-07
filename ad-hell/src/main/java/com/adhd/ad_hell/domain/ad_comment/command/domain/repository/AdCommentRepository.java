package com.adhd.ad_hell.domain.ad_comment.command.domain.repository;

import com.adhd.ad_hell.domain.ad_comment.command.domain.aggregate.AdComment;

import java.util.Optional;

public interface AdCommentRepository {
    AdComment save(AdComment adComment);
    Optional<AdComment> findById(Long adCommentId);
    void deleteById(Long adCommentId);
    boolean existsById(Long adCommentIs);
}
