package com.MyDrama.dto;

import com.MyDrama.constant.Category;
import com.MyDrama.constant.MainCategory;
import com.MyDrama.constant.SkinConcern;
import com.MyDrama.entity.ItemLike;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.querydsl.core.annotations.QueryProjection;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String title;
    private String imgName;
    private String description;
    private Integer price;
    private MainCategory mainCategory;
    private Category category;
    private SkinConcern skinConcern;

    private Integer likeCount;  // 좋아요
    private Integer viewCount; // 방문자수

    private LocalDateTime regTime;  // 시간
    private LocalDateTime updateTime;

}
