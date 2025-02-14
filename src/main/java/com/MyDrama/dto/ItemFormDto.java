package com.MyDrama.dto;

import com.MyDrama.constant.*;
import com.MyDrama.entity.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemFormDto {

    private Long id;

    @NotNull(message = "상품명은 필수 입력 값입니다.")
    private String title;

    @NotNull(message = "내용은 필수 입력 값입니다.")
    private String description;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotNull(message = "수량은 필수 입력 값입니다.")
    @Max(value = 999,message = "수량은 50개 이하여야합니다.")
    private int stockNumber;

    private MainCategory mainCategory;

    private Category category;

    private SkinConcern skinConcern;

    private LINE line;

    private ItemSellStatus itemSellStatus;


    // modelMapper ()
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();  // 상품 이미지 정보

    private List<Long> itemImgIds = new ArrayList<>();  // 상품 이미지 아이디

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        //ItemFormDto -> Item 연결
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item){
        // Item -> ITemFormDto 연결
        return modelMapper.map(item,ItemFormDto.class);
    }
}
