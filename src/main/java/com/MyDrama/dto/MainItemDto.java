package com.MyDrama.dto;

import com.MyDrama.constant.ItemSellStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String title;
    private String description;
    private String imgName;
    private Integer price;
    @QueryProjection //Querydsl 결과 조회 시 MainItemDto 객체로 바로 오도록  활용
    public MainItemDto(Long id, String title, String description, String imgName, Integer price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgName = imgName;
        this.price = price;
    }
}