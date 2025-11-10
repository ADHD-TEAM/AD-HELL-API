package com.adhd.ad_hell.domain.advertise.command.domain.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdLike;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdLikeId;

import java.util.List;
import java.util.Optional;

public interface AdLikeRepository {
    AdLike save(AdLike adLike);
    Optional<AdLike> findById(AdLikeId id);
    void deleteById(AdLikeId id);
}