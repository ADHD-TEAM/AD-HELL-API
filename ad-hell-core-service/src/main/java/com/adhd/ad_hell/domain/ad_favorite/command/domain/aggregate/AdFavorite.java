package com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ad_favorite", uniqueConstraints = {@UniqueConstraint(name =
                "uk_ad_favorite_user_ad", columnNames = {"user_id", "ad_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AdFavorite extends BaseTimeEntity {

    /** 즐겨찾기 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_id")
    private Long id;

    /** 광고 (FK: ad_id) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    /** 회원정보 (FK: user_id) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 정적 팩토리 메서드 */
    public static AdFavorite create(Ad ad, User user) {
        return AdFavorite.builder()
                .ad(ad)
                .user(user)
                .build();
    }
}
