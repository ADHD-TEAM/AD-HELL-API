package com.adhd.ad_hell.domain.advertise.command.application.service;

import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCommentCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCommentUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdCommentCommandService {

    private final AdCommentRepository adCommentRepository;

    /* 광고 댓글 등록 */
    @Transactional
    public Long createAdComment(AdCommentCreateRequest req) {
        AdComment newComment = AdComment.builder()
                .userId(req.getUserId())
                .adId(req.getAdId())
                .content(req.getContent())
                .build();

        AdComment saved = adCommentRepository.save(newComment);
        return saved.getAdCommentId();
    }


    /* 광고 댓글 수정 */
    @Transactional
    public void updateAdComment(Long adCommentId, AdCommentUpdateRequest req) {
        AdComment comment = adCommentRepository.findById(adCommentId)
                .orElseThrow(() -> new EntityNotFoundException("AdComment not found: " + adCommentId));

        comment.update(req.getContent());
    }

    /* 광고 댓글 삭제 */
    @Transactional
    public void deleteAdComment(Long adCommentId) {
        if (!adCommentRepository.existsById(adCommentId)) {
            throw new EntityNotFoundException("AdComment not found: " + adCommentId);
        }
        adCommentRepository.deleteById(adCommentId);
    }
}
