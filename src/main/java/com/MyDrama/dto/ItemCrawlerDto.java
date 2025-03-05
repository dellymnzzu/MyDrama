package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemCrawlerDto {
    private Long id;
    private String name;
    private String price;
    private String imgUrl;
}
