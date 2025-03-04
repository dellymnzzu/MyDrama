package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId; // 장바구니 상품 아이디
    private String title; // 상품명
    private int price; // 가격
    private int count; // 수량
    private String imgName; // 이 필드가 실제 파일명만 포함하도록

    public CartDetailDto(Long cartItemId, String title, int price, int count, String imgName) {
        this.cartItemId = cartItemId;
        this.title = title;
        this.price = price;
        this.count = count;
        this.imgName = imgName;
    }
}