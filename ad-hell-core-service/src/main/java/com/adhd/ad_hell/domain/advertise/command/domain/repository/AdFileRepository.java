package com.adhd.ad_hell.domain.advertise.command.domain.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;

import java.util.List;
import java.util.Optional;

public interface AdFileRepository {
    AdFile save(AdFile ad);
    List<AdFile> findByAd_AdId(Long adId);
    Optional<AdFile> findById(Long fileId);
    void deleteById(Long fileId);


    // 게시판 연동용
    List<AdFile> findByBoardId(Long boardId);
    void deleteByBoardId(Long boardId);
}