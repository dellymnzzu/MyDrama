package com.MyDrama.controller;

import com.MyDrama.dto.CartPaymentDto;
import com.MyDrama.dto.PaymentDto;
import com.MyDrama.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/complete")
    @ResponseBody
    public ResponseEntity<?> completePayment(@RequestBody PaymentDto paymentDto) {
        try {
            paymentService.savePayment(paymentDto);
            return ResponseEntity.ok().body("결제가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/payment/cart/complete")
    @ResponseBody
    public ResponseEntity<?> completeCartPayment(@RequestBody CartPaymentDto cartPaymentDto) {
        try {
            paymentService.saveCartPayment(cartPaymentDto);
            return ResponseEntity.ok().body("장바구니 결제가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/payment/success")
    public String paymentSuccess() {
        return "payment/success";
    }

    @GetMapping("/payment/failure")
    public String paymentFailure() {
        return "payment/failure";
    }
} 