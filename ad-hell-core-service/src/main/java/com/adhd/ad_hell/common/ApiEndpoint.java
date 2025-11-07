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
    USER_DELETE(HttpMethod.DELETE,"/api/users/**", Role.USER),

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
    REWARD_LIST(HttpMethod.GET, "/api/rewards", null),
    REWARD_DETAIL(HttpMethod.GET, "/api/rewards/**", Role.USER),

    // POINT
    POINT_EARN(HttpMethod.POST, "/api/users/point", Role.USER),
    POINT_HISTORY(HttpMethod.GET, "/api/users/point", Role.USER),

    ALL_GET(HttpMethod.GET, "/api/**", null),
    ALL_POST(HttpMethod.POST, "/api/**", null),
    ALL_PUT(HttpMethod.PUT, "/api/**", null),
    ALL_PATCH(HttpMethod.PATCH, "/api/**", null),
    ALL_DELETE(HttpMethod.DELETE, "/api/**", null),

    // AD_FAVORITE
    AD_FAVORITE_POST(HttpMethod.POST,   "/api/ad_favorites",         Role.USER),
    AD_FAVORITE_DELETE(HttpMethod.DELETE,"/api/ad_favorites",        Role.USER),
    AD_FAVORITE_MY_LIST(HttpMethod.GET, "/api/ad-favorites/my",      Role.USER),
    AD_FAVORITE_DETAIL(HttpMethod.GET,  "/api/ad-favorites/*",       Role.USER),
    AD_FAVORITE_EXISTS(HttpMethod.GET,  "/api/ad-favorites/exists",  Role.USER),

    // ANNOUNCEMENT
    ANNOUNCEMENT_CREATE(HttpMethod.POST,   "/api/announcements",   Role.ADMIN),
    ANNOUNCEMENT_UPDATE(HttpMethod.PUT,    "/api/announcements/*", Role.ADMIN),
    ANNOUNCEMENT_DELETE(HttpMethod.DELETE, "/api/announcements/*", Role.ADMIN),
    ANNOUNCEMENT_LIST(HttpMethod.GET,      "/api/announcements",   null),
    ANNOUNCEMENT_DETAIL(HttpMethod.GET,    "/api/announcements/*", null),


    // BOARD
    BOARD_CREATE(HttpMethod.POST,   "/api/boards",        Role.USER),
    BOARD_UPDATE(HttpMethod.PUT,    "/api/boards/*",      Role.USER),
    BOARD_DELETE(HttpMethod.DELETE, "/api/boards/*",      Role.USER),
    BOARD_LIST(HttpMethod.GET,      "/api/boards",             null), // 게시글 목록 조회 (검색, 페이징)
    BOARD_DETAIL(HttpMethod.GET,    "/api/boards/*",           null), // 게시글 상세 조회 (조회수 증가 O)
    BOARD_DETAIL_PLAIN(HttpMethod.GET, "/api/boards/*/plain",  null), // 게시글 상세 조회 (조회수 증가 X)

    // BOARD_COMMENT
    BOARD_COMMENT_CREATE(HttpMethod.POST,   "/api/board_comments",    Role.USER),
    BOARD_COMMENT_UPDATE(HttpMethod.PUT,    "/api/board_comments/*",  Role.USER),
    BOARD_COMMENT_DELETE(HttpMethod.DELETE, "/api/board_comments/*",  Role.USER),
    BOARD_COMMENT_LIST(HttpMethod.GET,      "/api/board_comments",    Role.USER),
    BOARD_COMMENT_MY_LIST(HttpMethod.GET,   "/api/board_comments/my", Role.USER),
    BOARD_COMMENT_DETAIL(HttpMethod.GET,    "/api/board_comments/*",  Role.USER),

    // INQUIRY
    INQUIRY_CREATE(HttpMethod.POST,  "/api/inquiries",               Role.USER),
    INQUIRY_ANSWER(HttpMethod.PATCH, "/api/inquiries/admin/*/answer", Role.ADMIN),
    INQUIRY_MY_LIST(HttpMethod.GET,     "/api/inquiries/my",        Role.USER),
    INQUIRY_MY_DETAIL(HttpMethod.GET,   "/api/inquiries/my/*",      Role.USER),
    INQUIRY_ADMIN_LIST(HttpMethod.GET,  "/api/inquiries/admin",     Role.ADMIN),
    INQUIRY_ADMIN_DETAIL(HttpMethod.GET,"/api/inquiries/admin/*",   Role.ADMIN),


    ;
    private final HttpMethod endpointStatus;
    private final String path;
    private final Role role;


}
