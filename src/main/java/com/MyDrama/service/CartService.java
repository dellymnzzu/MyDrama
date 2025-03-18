package com.MyDrama.service;

import com.MyDrama.dto.CartDetailDto;
import com.MyDrama.dto.CartItemDto;
import com.MyDrama.dto.CartOrderDto;
import com.MyDrama.dto.OrderDto;
import com.MyDrama.entity.Cart;
import com.MyDrama.entity.CartItem;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.CartItemRepository;
import com.MyDrama.repository.CartRepository;
import com.MyDrama.repository.ItemRepository;
import com.MyDrama.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    
    @Autowired
    private OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String userId) {
        try {
            // Item 객체 DB에서 조회
            Item item = itemRepository.findById(cartItemDto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
            System.out.println("장바구니 담기 - 상품 정보: " + item.getId() + ", " + item.getTitle());
            
            // Member 객체 DB에서 조회
            Member member = memberRepository.findByUserId(userId);
            if (member == null) {
                throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
            }
            System.out.println("장바구니 담기 - 회원 정보: " + member.getId() + ", " + member.getUserId());

            // Member Id를 통해서 Cart 객체 조회
            Cart cart = cartRepository.findByMemberId(member.getId());
            System.out.println("장바구니 담기 - 장바구니 정보: " + (cart != null ? cart.getId() : "없음"));
            
            // Cart 객체가 null이면 Cart 객체 생성
            if (cart == null) {
                cart = Cart.createCart(member);
                cartRepository.save(cart);
                System.out.println("장바구니 담기 - 새 장바구니 생성: " + cart.getId());
            }
            
            // Cart ID와 Item ID로 CartItem 조회
            CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
            
            // 이미 존재하는 CartItem이 있으면 수량 증가
            if (savedCartItem != null) {
                savedCartItem.addCount(cartItemDto.getCount());
                System.out.println("장바구니 담기 - 기존 상품 수량 증가: " + savedCartItem.getId() + ", 수량: " + savedCartItem.getCount());
                return savedCartItem.getId();
            }
            // 없으면 새로운 CartItem 생성
            else {
                CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
                cartItemRepository.save(cartItem);
                System.out.println("장바구니 담기 - 새 상품 추가: " + cartItem.getId() + ", 수량: " + cartItem.getCount());
                return cartItem.getId();
            }
        } catch (Exception e) {
            System.err.println("장바구니 담기 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String userId) {
        try {
            System.out.println("장바구니 목록 조회 시작 - 사용자: " + userId);
            
            List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

            // 회원 정보 조회
            Member member = memberRepository.findByUserId(userId);
            if (member == null) {
                System.err.println("장바구니 목록 조회 - 회원 정보 없음: " + userId);
                return cartDetailDtoList;
            }
            System.out.println("장바구니 목록 조회 - 회원 정보: " + member.getId());

            // 장바구니 조회
            Cart cart = cartRepository.findByMemberId(member.getId());
            if (cart == null) {
                System.out.println("장바구니 목록 조회 - 장바구니 없음");
                return cartDetailDtoList;
            }
            System.out.println("장바구니 목록 조회 - 장바구니 ID: " + cart.getId());

            // 장바구니 상품 목록 조회
            cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
            System.out.println("장바구니 목록 조회 완료 - 상품 수: " + cartDetailDtoList.size());
            
            return cartDetailDtoList;
        } catch (Exception e) {
            System.err.println("장바구니 목록 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String userId) {
        // email을 이용해서 Member 엔티티 객체 추출
        Member curMember = memberRepository.findByUserId(userId);
        // cartItemId를 이용해서 CartItem 엔티티 객체 추출
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        // Cart -> Memeber 엔티티 객체를 추출
        Member savedMember = cartItem.getCart().getMember();
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같지 않으면 true return false
        if (!StringUtils.equals(curMember.getUserId(), savedMember.getUserId())) {
            return false;
        }
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같으면 return true
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String userId) {
        // 주문DTO List 객체 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        // 카트 주문 List에 있는 목록 -> 카트 아이템 객체로 추출
        // 주문 Dto에 CartItem 정보를 담고
        // 위에 선언된 주문 Dto List에 추가
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        // 주문DTO리스트 현재 로그인된 이메일 매개변수 넣고
        // 주문 서비스 실행 -> 주문
        Long orderId = orderService.orders(orderDtoList, userId);

        //Cart에서 있던 Item 주문이 되니까 CartItem 모두 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }
}