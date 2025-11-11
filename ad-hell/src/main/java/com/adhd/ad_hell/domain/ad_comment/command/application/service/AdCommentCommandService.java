package com.adhd.ad_hell.domain.ad_comment.command.application.service;

import com.adhd.ad_hell.domain.ad_comment.command.application.dto.request.AdCommentCreateRequest;
import com.adhd.ad_hell.domain.ad_comment.command.application.dto.request.AdCommentUpdateRequest;
import com.adhd.ad_hell.domain.ad_comment.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.ad_comment.command.domain.repository.AdCommentRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdCommentCommandService {

    private final AdCommentRepository adCommentRepository;
    private final AdRepository adRepository;

    /* 광고 댓글 등록 */
    @Transactional
    public void createAdComment(AdCommentCreateRequest req) {
        AdComment newComment = AdComment.builder()
                .userId(req.getUserId())
                .adId(req.getAdId())
                .content(req.getContent())
                .build();

        Ad ad = adRepository.findById(req.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        ad.increaseCommentCount();

        adCommentRepository.save(newComment);
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
        AdComment comment = adCommentRepository.findById(adCommentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        Ad ad = adRepository.findById(comment.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        ad.decreaseCommentCount();

        adCommentRepository.deleteById(adCommentId);
    }
}
