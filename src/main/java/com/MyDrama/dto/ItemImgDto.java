package com.MyDrama.dto;


import com.MyDrama.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {
    private Long id;
    private String imgName;  // 이미지 경로
    private String oriImgName;  // 이미지 저장 이름
    private String imgUrl;  // 이미지 본래 이름

    public static ModelMapper modelMapper = new ModelMapper();  // Object의 값들을 자동으로 매핑시켜주는 라이브러리이다.
    public static ItemImgDto of(ItemImg itemImg) {return modelMapper.map(itemImg, ItemImgDto.class);}  //ItemImg 객체를 ItemImgDto 객체로 변환하는 메서드
}
