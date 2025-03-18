package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId; // 장바구니 상품 아이디
    private Long itemId;     // 상품 아이디
    private String title;    // 상품명
    private int price;       // 가격
    private int count;       // 수량
    private String imgName;  // 이미지 파일명

    public CartDetailDto(Long cartItemId, Long itemId, String title, int price, int count, String imgName) {
        this.cartItemId = cartItemId;
        this.itemId = itemId;
        this.title = title;
        this.price = price;
        this.count = count;
        this.imgName = imgName;
    }
}