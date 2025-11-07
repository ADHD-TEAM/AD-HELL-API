package com.adhd.ad_hell.domain.user.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import com.adhd.ad_hell.domain.user.query.service.AdminQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
@Tag(name = "Admin Query", description = "관리자 API")
public class AdminQueryController {

    private final AdminQueryService adminQueryService;

    /**
     * 관리자 - 회원 목록 가져오기
     * @param adminSearchRequest
     * @return
     */
    @Operation(
            summary = "관리자 - 회원 목록 가져오기",
            description = "관리자는 회원 목록을 조회할 수 있다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
    })
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
    @Operation(
            summary = "관리자 - 회원 상세 조회",
            description = "관리자는 특정 회원을 상세 조회할 수 있다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
    })
    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @PathVariable Long user_id) {
        log.info("[AdminQueryController/getUser] 관리자 - 회원 상세 가져오기");
        UserDTO response = adminQueryService.findByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
