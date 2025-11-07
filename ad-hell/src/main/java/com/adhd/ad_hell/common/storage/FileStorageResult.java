package com.adhd.ad_hell.common.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileStorageResult {
  private final String storedName; // 서버에 저장된 실제 파일명(UUID)
  private final String url;        // 접근 가능한 URL
}
