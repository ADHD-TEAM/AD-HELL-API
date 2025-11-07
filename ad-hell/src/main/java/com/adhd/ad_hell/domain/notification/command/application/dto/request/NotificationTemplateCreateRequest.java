package com.adhd.ad_hell.domain.notification.command.application.dto.request;


import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationTemplateCreateRequest {

    @NotNull(message = "템플릿 종류는 필수 값입니다.")
    private NotificationTemplateKind templateKind;

    @NotBlank(message = "템플릿 제목은 필수 값입니다.")
    @Size(max = 50, message = "템플릿 제목은 50자를 넘을 수 없습니다.")
    private String templateTitle;

    @NotBlank(message = "템플릿 본문은 필수 값입니다.")
    private String templateBody;

    @Builder
    private NotificationTemplateCreateRequest(
            NotificationTemplateKind templateKind,
            String templateTitle,
            String templateBody
    ) {
        this.templateKind = templateKind;
        this.templateTitle = templateTitle;
        this.templateBody = templateBody;
    }
}