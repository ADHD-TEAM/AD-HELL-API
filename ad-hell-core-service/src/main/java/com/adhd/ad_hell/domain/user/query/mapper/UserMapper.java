package com.adhd.ad_hell.domain.user.query.mapper;

import com.adhd.ad_hell.domain.auth.command.dto.response.FindUserInfoResponse;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<UserDTO> getDeleteStatusUsers();

    UserDTO findByEmailAndLoginId(String email, String loginId);
}
