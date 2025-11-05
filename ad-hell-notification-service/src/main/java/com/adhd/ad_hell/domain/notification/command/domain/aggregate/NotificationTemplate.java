package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification_template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTemplate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_kind", nullable = false, length = 10)
    private NotificationTemplateKind templateKind;

    @Column(name = "template_title", nullable = false, length = 50)
    private String templateTitle;

    @Column(name = "template_body", nullable = false, columnDefinition = "TEXT")
    private String templateBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false, length = 1)
    private YnType deletedYn;

    @Builder
    private NotificationTemplate(
            NotificationTemplateKind templateKind,
            String templateTitle,
            String templateBody,
            YnType deletedYn
    ) {
        this.templateKind = templateKind;
        this.templateTitle = templateTitle;
        this.templateBody = templateBody;
        this.deletedYn = deletedYn == null ? YnType.no() : deletedYn;
    }

    public void update(NotificationTemplateKind templateKind, String templateTitle, String templateBody) {
        if (templateKind != null) {
            this.templateKind = templateKind;
        }
        if (templateTitle != null && !templateTitle.isBlank()) {
            this.templateTitle = templateTitle;
        }
        if (templateBody != null && !templateBody.isBlank()) {
            this.templateBody = templateBody;
        }
    }

    public void delete() {
        this.deletedYn = YnType.yes();
    }

    public void restore() {
        this.deletedYn = YnType.no();
    }
}