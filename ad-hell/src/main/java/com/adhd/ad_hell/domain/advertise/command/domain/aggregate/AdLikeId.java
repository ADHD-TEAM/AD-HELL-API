package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode   // JPA 복합키에서는 반드시 equals/hashCode 구현 필요
public class AdLikeId implements Serializable {

    private Long adId;
    private Long userId;

    public AdLikeId(Long adId, Long userId) {
        this.adId = adId;
        this.userId = userId;
    }
}