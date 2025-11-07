package com.adhd.ad_hell.domain.ranking.query.mapper;

import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdRankingMapper {

    List<AdRank> selectTop20ByScore();
}
