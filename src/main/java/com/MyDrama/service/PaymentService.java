package com.MyDrama.service;

import com.MyDrama.dto.CartPaymentDto;
import com.MyDrama.dto.PaymentDto;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.Member;
import com.MyDrama.entity.Order;
import com.MyDrama.entity.OrderItem;
import com.MyDrama.repository.CartItemRepository;
import com.MyDrama.repository.ItemRepository;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    public void savePayment(PaymentDto paymentDto) {
        // 회원 정보 조회
        Member member = memberRepository.findByUserId(paymentDto.getBuyerId());

        // 상품 정보 조회
        Item item = itemRepository.findById(paymentDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, paymentDto.getCount());

        // 주문 생성
        Order order = Order.createOrder(member, List.of(orderItem));
        order.setImpUid(paymentDto.getImpUid());
        order.setMerchantUid(paymentDto.getMerchantUid());
        order.setTotalPrice(paymentDto.getPrice());

        // 주문 저장
        orderRepository.save(order);
    }
    
    public void saveCartPayment(CartPaymentDto cartPaymentDto) {
        try {
            System.out.println("장바구니 결제 처리 시작 - 사용자: " + cartPaymentDto.getBuyerId());
            System.out.println("주문 상품 개수: " + cartPaymentDto.getOrderItems().size());
            
            // 회원 정보 조회
            Member member = memberRepository.findByUserId(cartPaymentDto.getBuyerId());
            System.out.println("회원 정보: " + member.getId() + ", " + member.getUserId());
            
            // 주문 상품 목록 생성
            List<OrderItem> orderItems = new ArrayList<>();
            
            // 각 장바구니 상품에 대한 주문 상품 생성
            for (CartPaymentDto.CartOrderItemDto itemDto : cartPaymentDto.getOrderItems()) {
                System.out.println("장바구니 상품 처리: cartItemId=" + itemDto.getCartItemId() + ", itemId=" + itemDto.getItemId());
                
                // 상품 조회
                try {
                    Item item = itemRepository.findById(itemDto.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. (itemId: " + itemDto.getItemId() + ")"));
                    
                    System.out.println("상품 조회 성공: " + item.getId() + ", " + item.getTitle());
                    
                    // 주문 상품 생성 및 추가
                    OrderItem orderItem = OrderItem.createOrderItem(item, itemDto.getCount());
                    orderItems.add(orderItem);
                    
                    // 결제 완료된 장바구니 상품 삭제
                    cartItemRepository.deleteById(itemDto.getCartItemId());
                    System.out.println("장바구니 상품 삭제 완료: " + itemDto.getCartItemId());
                } catch (Exception e) {
                    System.err.println("상품 처리 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            // 주문 생성
            Order order = Order.createOrder(member, orderItems);
            order.setImpUid(cartPaymentDto.getImpUid());
            order.setMerchantUid(cartPaymentDto.getMerchantUid());
            order.setTotalPrice(cartPaymentDto.getTotalPrice());
            
            // 주문 저장
            orderRepository.save(order);
            System.out.println("주문 생성 완료: " + order.getId() + ", 총 상품 수: " + orderItems.size());
        } catch (Exception e) {
            System.err.println("장바구니 결제 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 