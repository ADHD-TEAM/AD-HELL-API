package com.adhd.ad_hell.domain.inquiry.command.domain.repository;

import com.adhd.ad_hell.domain.inquiry.command.domain.aggregate.Inquiry;

import java.util.Optional;

public interface InquiryRepository {

    // 문의 등록
    Inquiry save(Inquiry inquiry);
    // PK 기반 단건 조회
    Optional<Inquiry> findById(Long id);
}
