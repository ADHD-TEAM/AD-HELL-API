package com.adhd.ad_hell.domain.user.query.mapper;

import com.adhd.ad_hell.domain.user.command.entity.PointStatus;
import com.adhd.ad_hell.domain.user.command.entity.PointType;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import com.adhd.ad_hell.domain.user.query.dto.response.UserPointHistoryResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointHistoryMapper {
    List<UserPointHistoryResponse> findMyPointHistory(Long userId, PointStatus pointStatus);
}
