package com.adhd.ad_hell.domain.inquiry.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class InquiryAnswerRequest {

    @NotBlank @Size(max = 5000)
    private String response;
}
