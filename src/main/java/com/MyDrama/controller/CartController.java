package com.MyDrama.controller;

import com.MyDrama.dto.CartDetailDto;
import com.MyDrama.dto.CartItemDto;
import com.MyDrama.dto.CartOrderDto;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody CartItemDto cartItemDto, Principal principal) {
        try {
            System.out.println("장바구니 추가 요청: itemId=" + cartItemDto.getItemId() + ", count=" + cartItemDto.getCount());
            
            if (principal == null) {
                System.err.println("장바구니 추가 실패: 로그인 필요");
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            // 요청 데이터 검증
            if (cartItemDto.getItemId() == null) {
                System.err.println("장바구니 추가 실패: 상품 ID 누락");
                return new ResponseEntity<>("상품 ID가 필요합니다.", HttpStatus.BAD_REQUEST);
            }
            if (cartItemDto.getCount() <= 0) {
                System.err.println("장바구니 추가 실패: 수량이 0 이하");
                return new ResponseEntity<>("수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
            }
            
            Long cartItemId = cartService.addCart(cartItemDto, principal.getName());
            System.out.println("장바구니 추가 성공: cartItemId=" + cartItemId);
            return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("장바구니 추가 실패: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart")
    public String cartList(Principal principal, Model model) {
        // 로그인 여부 체크
        if(principal == null) {
            return "redirect:/member/signin";  // 로그인 페이지로 리다이렉트
        }

        try {
            System.out.println("장바구니 조회 시작: " + principal.getName());
            
            // 사용자 정보 추가
            Member member = memberRepository.findByUserId(principal.getName());
            System.out.println("사용자 정보: " + member);
            
            model.addAttribute("userId", member.getUserId());
            model.addAttribute("userName", member.getName());
            model.addAttribute("userTel", member.getTel());
            
            // 로그인한 회원의 장바구니 조회
            List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
            System.out.println("장바구니 항목 수: " + (cartDetailList != null ? cartDetailList.size() : "null"));
            
            model.addAttribute("cartItems", cartDetailList);
            return "cart/cartList";
        } catch (Exception e) {
            System.err.println("장바구니 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "장바구니 조회 중 에러가 발생했습니다: " + e.getMessage());
            return "cart/cartList";
        }
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal) {
        System.out.println(cartItemId);
        if (count <= 0) {
            return new ResponseEntity<String>("최소 1개이상 담아주세요.", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal){
        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/" +
            "cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){
        System.out.println(cartOrderDto.getCartItemId());
        //CartOrderDtoList List <- getCartOrderDtoList 통해서 Views 내랴운 리스트
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        // null, size가 0이면 실행
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.",HttpStatus.FORBIDDEN);
        }
        // 전체 유효성검사
        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }
        Long orderId;
        try {
            // cart -> order
            // cartService -> orderService
            // cartOrderDtoList(CartOrderDtoList)
            // cartOrderDtoList -> OrderDtoList -> OrderItem
            // Order <- OrderItem
            // Order save Order 저장 OrderItemList 저장
            orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        }
        catch (Exception e){ // 실패하면
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        //성공
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}