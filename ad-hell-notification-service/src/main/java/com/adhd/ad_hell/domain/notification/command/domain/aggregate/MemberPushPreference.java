package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_push_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberPushPreference extends BaseTimeEntity {

    /** 회원 고유 식별자 = PK (1:1 매핑 가정) */
    @Id
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /** 푸시 사용 여부 (Y/N) — 프로젝트 컨벤션(YnType) 준수 */
    @Enumerated(EnumType.STRING)
    @Column(name = "push_enabled", nullable = false, length = 1)
    private YnType pushEnabled;

    /** 기본값/수정 편의 메서드 */
    public static MemberPushPreference create(Long memberId, boolean enabled) {
        return MemberPushPreference.builder()
                .memberId(memberId)
                .pushEnabled(enabled ? YnType.Y : YnType.N)
                .build();
    }

    public void change(boolean enabled) {
        this.pushEnabled = enabled ? YnType.Y : YnType.N;
    }

    public boolean isEnabled() {
        return this.pushEnabled == YnType.Y;
    }
}
