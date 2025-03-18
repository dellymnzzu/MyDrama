package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartPaymentDto {
    // 결제 정보
    private String impUid;         // 아임포트 결제 고유 번호
    private String merchantUid;     // 상점 거래 고유 번호
    private Integer totalPrice;     // 총 결제 금액
    private String status;          // 결제 상태
    
    // 구매자 정보
    private String buyerId;         // 구매자 ID
    private String buyerName;       // 구매자 이름
    private String buyerTel;        // 구매자 전화번호
    
    // 주문 상품 목록
    private List<CartOrderItemDto> orderItems;
    
    @Getter
    @Setter
    public static class CartOrderItemDto {
        private Long cartItemId;    // 장바구니 아이템 ID
        private Long itemId;        // 상품 ID
        private String itemName;    // 상품명
        private Integer count;      // 수량
        private Integer price;      // 가격
    }
} 