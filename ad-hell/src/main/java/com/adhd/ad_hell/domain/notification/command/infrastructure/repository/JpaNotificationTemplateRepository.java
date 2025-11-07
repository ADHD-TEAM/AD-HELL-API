package com.adhd.ad_hell.domain.notification.command.infrastructure.repository;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationTemplate;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Page<NotificationTemplate> findByDeletedYn(YnType deletedYn, Pageable pageable);

    Page<NotificationTemplate> findByDeletedYnAndTemplateTitleContainingIgnoreCase(
            YnType deletedYn,
            String templateTitle,
            Pageable pageable
    );
}
