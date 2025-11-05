package com.adhd.ad_hell.domain.notification.command.application.service;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.MemberPushPreference;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaMemberPushPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA 기반 푸시환경설정 Port 구현체.
 */
@Component
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPushPreferenceAdapter implements PushPreferencePort {

    private final JpaMemberPushPreferenceRepository repo;

    @Override
    @Transactional
    public void setEnabled(Long memberId, boolean enabled) {
        var pref = repo.findById(memberId)
                .orElseGet(() -> MemberPushPreference.create(memberId, enabled));
        // 신규면 create 값으로 저장, 기존이면 변경
        if (repo.existsById(memberId)) {
            pref.change(enabled);
        }
        repo.save(pref);
    }

    @Override
    public boolean isEnabled(Long memberId) {
        // 레코드 없으면 on default
        return repo.findById(memberId)
                .map(MemberPushPreference::isEnabled)
                .orElse(true);
    }

    @Override
    public Set<Long> findAllEnabled() {
        return repo.findByPushEnabled(YnType.Y).stream()
                .map(MemberPushPreference::getMemberId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> findAllKnownMembers() {
        return repo.findAll().stream()
                .map(MemberPushPreference::getMemberId)
                .collect(Collectors.toSet());
    }
}
