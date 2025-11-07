package com.adhd.ad_hell.domain.user.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import com.adhd.ad_hell.domain.user.query.service.AdminQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminQueryController {

    private final AdminQueryService adminQueryService;

    /**
     * 관리자 - 회원 목록 가져오기
     * @param adminSearchRequest
     * @return
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers(
            @RequestBody AdminSearchRequest adminSearchRequest) {
        log.info("[AdminQueryController/getUsers] 관리자 - 회원 목록 가져오기");
        List<UserDTO> response = adminQueryService.findAllByUsers(adminSearchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 관리자 - 회원 상세 조회
     * @param user_id
     * @return
     */
    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @PathVariable Long user_id) {
        log.info("[AdminQueryController/getUser] 관리자 - 회원 상세 가져오기");
        UserDTO response = adminQueryService.findByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
