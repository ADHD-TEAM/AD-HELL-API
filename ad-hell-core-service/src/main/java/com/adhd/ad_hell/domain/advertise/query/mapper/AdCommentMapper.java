package com.adhd.ad_hell.domain.advertise.query.mapper;

import com.adhd.ad_hell.domain.advertise.query.dto.request.AdCommentSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdCommentMapper {
    AdCommentDto selectCommentById(Long adCommentId);
    List<AdCommentDto> selectCommentsByAdId(AdCommentSearchRequest adCommentSearchRequest);
    long countComments(AdCommentSearchRequest adCommentSearchRequest);
    long countMyComments(AdCommentSearchRequest adCommentSearchRequest);
    List<AdCommentDto> selectMyCommentsByUserId(AdCommentSearchRequest adCommentSearchRequest);
    /* 댓글수 증가 */
    void incrementCommentCount(Long adId);
    /* 댓글수 감소 */
    void decrementCommentCount(Long adId);

}
