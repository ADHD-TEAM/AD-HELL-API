package com.adhd.ad_hell.domain.notification.command.infrastructure.repository;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.MemberPushPreference;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaMemberPushPreferenceRepository extends JpaRepository<MemberPushPreference, Long> {

    List<MemberPushPreference> findByPushEnabled(YnType pushEnabled);
}
