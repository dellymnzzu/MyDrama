package com.MyDrama.service;

import com.MyDrama.dto.OrderDto;
import com.MyDrama.dto.OrderHistDto;
import com.MyDrama.dto.OrderItemDto;
import com.MyDrama.entity.*;
import com.MyDrama.repository.ItemImgRepository;
import com.MyDrama.repository.ItemRepository;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String userId) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByUserId(userId);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable); // 주문 리스트
        Long totalCount = orderRepository.countOrder(email); // 총 주문 수
        List<OrderHistDto> orderHistDtos = new ArrayList<>(); // 주문 히스토리 리스트
        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto
        for(Order order : orders){ // 주문들 -> 주문
            OrderHistDto orderHistDto = new OrderHistDto(order); // 주문 히스토리 객체생성
            List<OrderItem> orderItems = order.getOrderItems(); // 주문 -> 주문 아이템들
            for(OrderItem orderItem : orderItems){ // 주문 아이템들 - > 주문 아이템
                //주문 아이템 -> item id를 추출 해서 대표이미지를 받습니다. ItemImg
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(),
                        "Y");
                // 주문 아이템, 대표이미지 URL을 이용해서 OrderItemDto 객체를 생성
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                // 주문 히스토리 -> 주문 아이템 리스트에 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            //주문히스트리스트에 주문히스토리를 추가
            orderHistDtos.add(orderHistDto);
        }
        // pageImpl 주문히스토리 리스트, 페이지 세팅, 총 갯수
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return  true;
    }
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }


    public Long orders(List<OrderDto> orderDtoList, String email){
        //Member 엔티티 객체 추출
        Member member = memberRepository.findByEmail(email);
        //주문 Item 리스트 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        //주문 Dto List에 있는 객체만큼 반복
        for(OrderDto orderDto : orderDtoList){
            //주문 -> Item Entity 객체 추출
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            // 주문 Item 생성
            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            // 주문 Item List에 추가
            orderItemList.add(orderItem);
        }
        ////////// 주문 Item List가 완성/////////////////////
        // 주문Item리스트, Member를 매개변수로 넣고
        // 주문서 생성
        Order order = Order.createOrder(member, orderItemList);
        // 주문서 저장
        orderRepository.save(order);

        return order.getId();
    }
}