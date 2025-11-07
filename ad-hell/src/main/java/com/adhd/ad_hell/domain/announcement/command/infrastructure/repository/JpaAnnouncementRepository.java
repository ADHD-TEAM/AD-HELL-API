package com.adhd.ad_hell.domain.announcement.command.infrastructure.repository;

import com.adhd.ad_hell.domain.announcement.command.domain.aggregate.Announcement;
import com.adhd.ad_hell.domain.announcement.command.domain.repository.AnnouncementRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAnnouncementRepository extends AnnouncementRepository, JpaRepository<Announcement, Long> {
}
