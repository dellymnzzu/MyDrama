package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member", nullable = false)

    private Member member;

    @ManyToOne
    @JoinColumn(name = "orders", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @Column(name = "price",nullable = false)
    private int price;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice; // 결제한 총 가격

    @Column(name = "status")
    private Boolean status = true; // 상태

    @Column(name = "merchant_uid", unique = true, nullable = false)
    private String merchantUid; // 주문 번호


    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now(); // 결제 날짜



}
