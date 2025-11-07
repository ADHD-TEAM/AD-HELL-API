package com.adhd.ad_hell.domain.advertise.command.domain.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;

import java.util.List;
import java.util.Optional;

public interface AdRepository {
    Ad save(Ad ad);
    Optional<Ad> findById(Long adId);
    void deleteById(Long adId);
}