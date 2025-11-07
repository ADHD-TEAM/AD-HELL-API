package com.adhd.ad_hell.domain.advertise.query.dto.response;

import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdDto {

    private Long adId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private Long viewCount;
    private Long likeCount;
    private Long bookmarkCount;
    private Long commentCount;
    private String createdAt;
    private String updatedAt;

    private List<AdFileDto> files;
}
