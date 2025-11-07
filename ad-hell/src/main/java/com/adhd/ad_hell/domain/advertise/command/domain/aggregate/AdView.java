package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Embeddable
@Getter
public class AdView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adViewId;
    private Long userId;
    private Long adId;
}
