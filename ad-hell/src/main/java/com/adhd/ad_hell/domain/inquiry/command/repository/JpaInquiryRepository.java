package com.adhd.ad_hell.domain.inquiry.command.repository;

import com.adhd.ad_hell.domain.inquiry.command.domain.aggregate.Inquiry;
import com.adhd.ad_hell.domain.inquiry.command.domain.repository.InquiryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInquiryRepository extends InquiryRepository, JpaRepository<Inquiry, Long> {
}
