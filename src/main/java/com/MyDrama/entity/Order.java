package com.MyDrama.entity;

import com.MyDrama.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "recipient_name")
    private String recipientName; // 수령인 이름

    @Column(name = "recipient_phone")
    private String recipientPhone; // 수령인 전화번호

    @Column(name = "zip_code")
    private String zipCode; // 우편번호

    @Column(name = "address")
    private String address; // 기본주소

    @Column(name = "detail_address")
    private String detailAddress; // 상세주소

    @Column(name = "delivery_message")
    private String deliveryMessage; // 배송 메시지
    
    @Column(name = "imp_uid")
    private String impUid; // 아임포트 결제 고유 번호
    
    @Column(name = "merchant_uid")
    private String merchantUid; // 상점 거래 고유 번호
    
    @Column(name = "total_price")
    private Integer totalPrice; // 결제 총 금액

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;



    //주문서 주문아이템 리스트에 주문 아이템 추가
    //주문 아이템에 주문서 추가
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    // 주문서 생성
    // 현재 로그인된 멤버 주문서에 추가
    // 주문아이템 리스트를 반복문을 통해서 주문서에 추가
    // 상태는 주문으로 세팅
    // 주문 시간은 현재시간으로 세팅
    // 주문서 리턴
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    // 주문서에 있는 주문 아이템 리스트를 반복
    // 주문 아이템마다 총 가격을 tatalPrice에 추가
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

}
