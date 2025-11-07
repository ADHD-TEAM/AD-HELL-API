package com.adhd.ad_hell.common;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@Getter
@RequiredArgsConstructor
public enum ApiEndpoint {

    // Auth 관련
    AUTH(HttpMethod.POST,"/api/auth/**" , null),

    // USER 관련
    USER_IS_AVAILABLE(HttpMethod.GET,"/api/users/isAvailable", null),
    USER_ME(HttpMethod.GET,"/api/users/**", Role.USER),
    USER_MODIFY(HttpMethod.PUT,"/api/users/**", Role.USER),
    USER_PATCH(HttpMethod.PATCH,"/api/users/**", Role.USER),

    // ADMIN 관련
    ADMIN_USER_LIST(HttpMethod.POST,"/api/admins/**", Role.ADMIN),
    ADMIN_USER_DETAIL(HttpMethod.GET,"/api/admins/**", Role.ADMIN),
    ADMIN_USER_MODIFY(HttpMethod.PUT,"/api/admins/**", Role.ADMIN),
    ADMIN_USER_PATCH(HttpMethod.PATCH,"/api/admins/**", Role.ADMIN),

    // CATEGORY
    CATEGORY_GET(HttpMethod.GET, "/api/categories", Role.ADMIN),
    CATEGORY_POST(HttpMethod.POST, "/api/categories", Role.ADMIN),
    CATEGORY_PUT(HttpMethod.PUT, "/api/categories", Role.ADMIN),
    CATEGORY_DELETE(HttpMethod.DELETE, "/api/categories", Role.ADMIN),

    // REPORT
    REPORT_POST(HttpMethod.POST,"/api/reports", Role.USER),
    MY_REPORT_LIST(HttpMethod.GET, "/api/reports/me", Role.USER),
    MY_REPORT_DETAIL(HttpMethod.GET, "/api/reports/me/**", Role.USER),
    REPORT_LIST(HttpMethod.GET, "/api/reports", Role.ADMIN),
    REPORT_DETAIL(HttpMethod.GET, "/api/reports/**", Role.ADMIN),

    // REWARD
    REWARD_POST(HttpMethod.POST, "/api/rewards", Role.ADMIN),
    REWARD_PUT(HttpMethod.PUT, "/api/rewards/**", Role.ADMIN),
    REWARD_PATCH(HttpMethod.PATCH, "/api/rewards/*/status", Role.ADMIN),
    REWARD_DELETE(HttpMethod.DELETE, "/api/rewards/**", Role.ADMIN),
    REWARD_STOCK_POST(HttpMethod.POST, "/api/rewards/*/stocks", Role.ADMIN),
    REWARD_STOCK_EXCHANGE(HttpMethod.POST, "/api/rewards/*/exchange", Role.USER),
    REWARD_STOCK_DETAIL(HttpMethod.GET, "/api/rewards/*/stock", Role.ADMIN),
    REWARD_LIST(HttpMethod.GET, "/api/rewards", Role.USER),
    REWARD_DETAIL(HttpMethod.GET, "/api/rewards/**", Role.USER),

    // POINT
    POINT_EARN(HttpMethod.POST, "/api/users/point", Role.USER),
    POINT_HISTORY(HttpMethod.GET, "/api/users/point", Role.USER),

    ALL_GET(HttpMethod.GET, "/api/**", null),
    ALL_POST(HttpMethod.POST, "/api/**", null),
    ALL_PUT(HttpMethod.PUT, "/api/**", null),
    ALL_PATCH(HttpMethod.PATCH, "/api/**", null),
    ALL_DELETE(HttpMethod.DELETE, "/api/**", null),

    ;
    private final HttpMethod endpointStatus;
    private final String path;
    private final Role role;


}
