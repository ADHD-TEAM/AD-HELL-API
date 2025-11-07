package com.adhd.ad_hell.domain.notification.command.application.service;

import java.util.Set;

/** 푸시 설정 조회/변경 Port */
public interface PushPreferencePort {
    void setEnabled(Long memberId, boolean enabled);
    boolean isEnabled(Long memberId);
    Set<Long> findAllEnabled();
    Set<Long> findAllKnownMembers();
}
