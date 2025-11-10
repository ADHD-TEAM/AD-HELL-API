package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_like")
@Getter
@NoArgsConstructor
public class AdLike {

    @EmbeddedId
    private AdLikeId id;   // 복합키 내장

    /* 외래키 매핑 */
    @MapsId("adId")  // AdLikeId.adId를 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @MapsId("userId") // AdLikeId.userId를 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public AdLike(Ad ad, User user) {
        this.id = new AdLikeId(ad.getAdId(), user.getUserId());
        this.ad = ad;
        this.user = user;
    }
}