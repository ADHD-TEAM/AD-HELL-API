package com.adhd.ad_hell.domain.inquiry.command.application.service;


import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryAnswerRequest;

import com.adhd.ad_hell.domain.inquiry.command.domain.repository.InquiryRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryCreateRequest;
import com.adhd.ad_hell.domain.inquiry.command.domain.aggregate.Inquiry;


import com.adhd.ad_hell.external.notification.client.NotificationInternalClient;
import com.adhd.ad_hell.external.notification.dto.NotificationSendRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InquiryCommandService {

    private final InquiryRepository inquiryRepository;
    private final CategoryRepository categoryRepository;
    private final UserCommandRepository userCommandRepository;
    private final NotificationInternalClient notificationClient;

    /** 문의 등록 (Builder 사용) */
    @Transactional
    public Long createInquiry(InquiryCreateRequest req) {
        User user = userCommandRepository.findById(req.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Inquiry inquiry = Inquiry.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .build();

        inquiry.linkUser(user);
        inquiry.linkCategory(category);

        return inquiryRepository.save(inquiry).getId();
    }

    /** 관리자: 문의 답변 등록/수정 */
    @Transactional
    public void answerInquiry(Long inquiryId, InquiryAnswerRequest req) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        inquiry.answer(req.getResponse());
        // 변경감지로 자동 UPDATE

        // 2) 알림 템플릿 ID (예: "문의 답변 완료" 템플릿)
        Long templateId = 1L; // 실제로는 설정/DB/enum 등으로 관리 추천

        // 3) 알림 발송 요청 DTO 작성
        NotificationSendRequestDto request = NotificationSendRequestDto.builder()
                .targetType(NotificationSendRequestDto.TargetType.CUSTOM)
                .targetMemberIds(List.of(inquiry.getUser().getUserId()))
                .variables(Map.of(
                        "userName", inquiry.getUser().getNickname(),
                        "inquiryTitle", inquiry.getTitle()
                ))
                .build();

        // 4) notification-service 에 발송 요청
        notificationClient.sendByTemplate(templateId, request);

    }
}
