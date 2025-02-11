package com.MyDrama.dto;

import com.MyDrama.constant.Category;
import com.MyDrama.constant.MainCategory;
import com.MyDrama.constant.SkinConcern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemFormDto {
    private Long id;

    private String title;

    private String description;

    private MainCategory mainCategory;

    private Category category;

    private SkinConcern skinConcern;

    private ItemImgDto itemImgDto;

}
