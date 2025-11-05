package com.adhd.ad_hell.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
  // ex. 에러코드
  EXAMPLE_NOT_FOUND("10001", "예제 메세지입니다.", HttpStatus.NOT_FOUND),
  // 회원정보 관련 에러코드
  NICKNAME_ALREADY_EXISTS("10002","이미 사용중인 닉네임입니다." , HttpStatus.ALREADY_REPORTED ),
  INVALID_USERNAME_OR_PASSWORD("10003", "올바르지 않은 아이디 혹은 비밀번호입니다.", HttpStatus.UNAUTHORIZED ),
  USER_NOT_FOUND("10004", "사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
  LOGIN_ID_ALREADY_EXISTS("10005","이미 사용중인 아이디입니다." ,  HttpStatus.ALREADY_REPORTED),
      VERIFI_CODE_DIFFERENT("10006","인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST ),
  INVALID_VERIFICATION_CODE("10007", "유효하지 않는 인증번호입니다.", HttpStatus.BAD_REQUEST ),
  INVALID_VERIFICATION_TOKEN("10008","유효하지 않는 토큰입니다..", HttpStatus.BAD_REQUEST ),


    // 카테고리 관련 에러코드
  CATEGORY_NOT_FOUND("30001", "카테고리를 찾지 못했습니다.", HttpStatus.NOT_FOUND),

  // 경품 관련 에러코드
  REWARD_NOT_FOUND("40001", "경품을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
  REWARD_STOCK_INVALID_STATUS("40011", "이미 사용되었거나 만료된 경품입니다.", HttpStatus.BAD_REQUEST),

  // 게시판 관련 에러코드
  BOARD_NOT_FOUND("60001", "게시판을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    // 게시글 관련 에러코드
    COMMENT_NOT_FOUND("70001", "게시글을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    // 공지사항 관련 에러코드
    ANNOUNCEMENT_NOT_FOUND("80001", "공지사항을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    // 문의 관련 에러코드
    INQUIRY_NOT_FOUND("90001", "문의를 찾지 못했습니다.", HttpStatus.NOT_FOUND),

    // 즐겨찾기 관련 에러코드
    AD_FAVORITE_ALREADY_EXISTS("100002", "이미 즐겨찾기한 광고입니다.", HttpStatus.CONFLICT),
    AD_FAVORITE_NOT_FOUND("100003", "즐겨찾기 내역이 존재하지 않습니다.", HttpStatus.NOT_FOUND),


    //광고 관련 에러코드
    AD_NOT_FOUND("60001", "광고를 찾지 못했습니다.", HttpStatus.NOT_FOUND),

    //파일 관련 에러코드
  FILE_EMPTY("50001", "업로드된 파일이 비어 있습니다.", HttpStatus.NOT_FOUND),
    FILE_STORE_FAILED("50002", "파일 저장에 실패했습니다.", HttpStatus.NOT_FOUND),

    FILE_DIR_CREATE_FAILED("50003","파일 저장소를 만드는데 실패했습니다." ,HttpStatus.BAD_REQUEST ),
    FILE_NAME_NOT_PRESENT("50004","파일 이름이 입력되지 않았습니다." ,HttpStatus.BAD_REQUEST ),
    FILE_EXTENSION_NOT_ALLOWED("50005","해당 파일 확장자는 지원하지 않습니다." ,HttpStatus.UNAUTHORIZED ),
    FILE_SAVE_IO_ERROR("50006","파일 저장에 실패했습니다." ,HttpStatus.INTERNAL_SERVER_ERROR ),
    FILE_DELETE_IO_ERROR("50007","파일 삭제에 실패했습니다." ,HttpStatus.INTERNAL_SERVER_ERROR ),
    FILE_PATH_TRAVERSAL_DETECTED("50008","파일 경로에 이상이 있습니다." ,HttpStatus.NOT_FOUND ),


    // 알림 관련 에러코드
    NOTI_NOT_FOUND("100001","알림이 존재하지 않습니다." ,HttpStatus.NOT_FOUND ),
    NOTI_UNAUTHORIZED("100002","본인 알림만 읽음 처리할 수 있습니다." ,HttpStatus.BAD_REQUEST ),
    NOTI_TEMPLATE_NOT_FOUND("100003","템플릿이 존재하지 않습니다." ,HttpStatus.NOT_FOUND),
    NOTI_TIME_NOT_ALLOWED("100004","예약 발송 시각은 현재 이후여야 합니다." ,HttpStatus.UNAUTHORIZED ),
    NOTI_SENDTYPE_PRESENT("100005", "발송 대상 타입은 필수입니다.", HttpStatus.BAD_REQUEST ),
    NOTI_CUSTOM_PRESENT("100006", "CUSTOM 발송은 대상 회원 목록이 필요합니다.", HttpStatus.BAD_REQUEST ),
    NOTI_SENDTYPE_NOT_ALLOWED("100007","지원하지 않는 발송 타입입니다: " ,HttpStatus.UNAUTHORIZED ),

    // 포인트 관련 에러코드
    POINT_HISTORY_NOT_FOUND("120001", "포인트 이력이 존재하지 않습니다.", HttpStatus.NOT_FOUND ),
    POINT_NOT_ENOUGH("120002", "포인트가 충분하지 않습니다.", HttpStatus.BAD_REQUEST ),
    POINT_DECREASE_FAILED("120003", "포인트 감소 실패", HttpStatus.NOT_MODIFIED ),

    // 신고 관련 에러코드
    REPORT_NOT_FOUND("130001", "신고 목록이 존재하지 않습니다.", HttpStatus.NOT_FOUND ),

    ACCESS_DENIED("990001", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN ),
    // 댓글 관련 에러 코드
    AD_COMMENT_NOT_FOUND("140000","댓글이 존재 하지않습니다", HttpStatus.NOT_FOUND);
  private final String code;
  private final String message;
  private final HttpStatusCode httpStatusCode;
}
