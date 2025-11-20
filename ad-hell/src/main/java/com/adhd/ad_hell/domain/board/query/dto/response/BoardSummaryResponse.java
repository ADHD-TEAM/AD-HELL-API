package com.adhd.ad_hell.domain.board.query.dto.response;


import com.adhd.ad_hell.domain.advertise.query.dto.response.AdFileDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSummaryResponse {

    private Long id;
    private String title;
    private String status;
    private Long viewCount;
    private String categoryName;
    private String writerName;
    private LocalDateTime createdAt;

    @Setter(AccessLevel.NONE)
    private List<AdFileDto> files;

    public void setFiles(List<AdFileDto> files) {
        this.files = files;

    }
}
