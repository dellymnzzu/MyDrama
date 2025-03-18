package com.MyDrama.dto;

import com.MyDrama.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private String itemNm;
    private int count;
    private int orderPrice;
    private String oriImgName;

    public OrderItemDto(OrderItem orderItem, String oriImgName) {
        this.itemNm = orderItem.getItem().getTitle();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.oriImgName = oriImgName;
    }
}
