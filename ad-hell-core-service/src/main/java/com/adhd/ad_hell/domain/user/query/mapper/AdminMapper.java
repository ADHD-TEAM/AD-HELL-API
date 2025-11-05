package com.adhd.ad_hell.domain.user.query.mapper;

import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {


    List<UserDTO> findAllByUsers(AdminSearchRequest adminSearchRequest);
}
