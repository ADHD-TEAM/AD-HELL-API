package com.adhd.ad_hell.exception;

import com.adhd.ad_hell.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    ErrorCode errorCode = e.getErrorCode();
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return new ResponseEntity<>(response, errorCode.getHttpStatusCode());
  }
}
