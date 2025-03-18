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
        Member member = memberRepository.findByUserId(userId);
        
        // 블랙리스트 체크
        if (member.isBlacklisted()) {
            throw new IllegalStateException("신고 누적으로 인해 주문이 제한된 사용자입니다.");
        }

        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String userId, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(userId, pageable);
        Long totalCount = orderRepository.countOrder(userId);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders){
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems){
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(
                    orderItem.getItem().getId(), "Y");
                // 이미지 경로를 전체 URL로 구성
                String imgUrl = "/itemImg/" + itemImg.getImgName(); // 또는 getOriImgName()
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, imgUrl);
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            //주문히스트리스트에 주문히스토리를 추가
            orderHistDtos.add(orderHistDto);
        }
        // pageImpl 주문히스토리 리스트, 페이지 세팅, 총 갯수
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String userId){
        Member curMember = memberRepository.findByUserId(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getUserId(), savedMember.getUserId())){
            return false;
        }
        return  true;
    }
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }


    public Long orders(List<OrderDto> orderDtoList, String userId) {
        Member member = memberRepository.findByUserId(userId);
        
        // 블랙리스트 체크
        if (member.isBlacklisted()) {
            throw new IllegalStateException("신고 누적으로 인해 주문이 제한된 사용자입니다.");
        }

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

    // 주문의 아임포트 결제 고유 번호(imp_uid) 조회
    @Transactional(readOnly = true)
    public String getImpUid(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));
            
            // impUid 필드가 있는지 확인
            String impUid = order.getImpUid();
            
            if (impUid == null || impUid.isEmpty()) {
                // 결제 정보가 없는 경우
                System.out.println("주문 ID: " + orderId + "의 결제 정보(impUid)가 없습니다.");
                return null;
            }
            
            System.out.println("주문 ID: " + orderId + "의 결제번호(impUid): " + impUid);
            return impUid;
        } catch (Exception e) {
            System.err.println("결제 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}