package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto {
    private Long itemId;
    private Integer count;
    private String itemName;
    private String impUid;
    private String merchantUid;
    private Integer price;
    private String status;
    private String buyerName;
    private String buyerId;
    private String buyerTel;
} 