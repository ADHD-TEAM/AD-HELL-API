package com.adhd.ad_hell.domain.advertise.query.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdFileDto {
  private Long fileId;
  private String originFileName;
  private String storedName;
  private String fileUrl;   // Controller에서 조합
  private String fileType;
}
