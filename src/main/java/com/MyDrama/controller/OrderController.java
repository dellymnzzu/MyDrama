package com.MyDrama.controller;

import com.MyDrama.dto.OrderDto;
import com.MyDrama.dto.OrderHistDto;
import com.MyDrama.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 아임포트 API 키 정보
    // 주의: 아래 API 키는 아임포트 관리자 콘솔에서 확인한 실제 키로 교체해야 합니다
    private static final String IAMPORT_API_KEY = "RESTAPI"; // 실제 REST API 키
    private static final String IAMPORT_API_SECRET = "RESTAPISECRET"; // 실제 REST API Secret

    @PostMapping(value = "/order")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                         Principal principal){
        // String a = "abc" + "def"
        // StringBuilder a;
        // a.append("abc");
        // a.append("def");
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        // 로그인 정보 -> Spring Security
        // principal.getName() (현재 로그인된 정보)
        String userId = principal.getName();
        Long orderId;
        try {
            orderId = orderService.order(orderDto,userId);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage",5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        System.out.println("주문 취소 요청 - 주문 ID: " + orderId);
        
        if(principal == null){
            return new ResponseEntity<String>("로그인 후 이용해주세요", HttpStatus.UNAUTHORIZED);
        }
        
        try {
            // 1. 주문 유효성 검증
            if(!orderService.validateOrder(orderId, principal.getName())){
                System.err.println("주문 취소 권한 없음 - 주문 ID: " + orderId + ", 회원: " + principal.getName());
                return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
            
            // 2. 결제 정보 조회
            String impUid = orderService.getImpUid(orderId);
            
            boolean paymentCanceled = false;
            // 3. 결제 취소 처리 (결제 정보가 있는 경우에만)
            if(impUid != null && !impUid.isEmpty()) {
                try {
                    System.out.println("아임포트 결제 취소 시도 - 주문 ID: " + orderId + ", 결제번호: " + impUid);
                    paymentCanceled = cancelPayment(impUid, "고객 요청에 의한 취소");
                    
                    if(paymentCanceled) {
                        System.out.println("아임포트 결제 취소 성공 - 주문 ID: " + orderId);
                    } else {
                        System.err.println("아임포트 결제 취소 실패 - 주문 상태만 취소로 변경합니다. 주문 ID: " + orderId);
                    }
                } catch (Exception e) {
                    System.err.println("결제 취소 중 오류 발생 - 주문 상태만 취소로 변경합니다. 주문 ID: " + orderId);
                    e.printStackTrace();
                }
            } else {
                System.out.println("결제 정보 없음 - 주문 상태만 취소합니다. 주문 ID: " + orderId);
            }
            
            // 4. 주문 취소 처리 (결제 취소 성공 여부와 관계없이 주문은 취소)
            orderService.cancelOrder(orderId);
            System.out.println("주문 취소 완료 - 주문 ID: " + orderId + ", 결제 취소 " + (paymentCanceled ? "성공" : "실패 또는 미시도"));
            
            return new ResponseEntity<Long>(orderId, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("주문 취소 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<String>("주문 취소 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 아임포트 API 액세스 토큰 발급 메서드
    private String getIamportToken() throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.iamport.kr/users/getToken");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            System.out.println("아임포트 토큰 요청 시작");
            System.out.println("API 키 길이: " + IAMPORT_API_KEY.length() + ", 시크릿 키 길이: " + IAMPORT_API_SECRET.length());
            
            // API 키와 시크릿을 JSON으로 요청
            String jsonInputString = "{\"imp_key\":\"" + IAMPORT_API_KEY + "\",\"imp_secret\":\"" + IAMPORT_API_SECRET + "\"}";
            System.out.println("요청 데이터 (길이): " + jsonInputString.length());
            
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("토큰 요청 응답 코드: " + responseCode);
            
            StringBuilder response = new StringBuilder();
            
            if (responseCode == 200) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } else {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                System.err.println("토큰 발급 실패 응답: " + response.toString());
                return null;
            }
            
            String jsonResponse = response.toString();
            System.out.println("토큰 응답 (요약): " + (jsonResponse.length() > 100 ? jsonResponse.substring(0, 100) + "..." : jsonResponse));
            
            // 단순화된 토큰 추출 로직
            int accessTokenIdx = jsonResponse.indexOf("\"access_token\"");
            if (accessTokenIdx > 0) {
                int valueStart = jsonResponse.indexOf("\"", accessTokenIdx + 15) + 1;
                int valueEnd = jsonResponse.indexOf("\"", valueStart);
                
                if (valueStart > 0 && valueEnd > valueStart) {
                    String token = jsonResponse.substring(valueStart, valueEnd);
                    System.out.println("발급받은 토큰: " + (token.length() > 10 ? token.substring(0, 10) + "..." : token));
                    return "Bearer " + token;
                }
            }
            
            // 토큰 추출 실패시 상세 오류 출력
            System.err.println("토큰 추출 실패: " + jsonResponse);
            return null;
        } catch (Exception e) {
            System.err.println("토큰 발급 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    // 결제 취소 메서드
    private boolean cancelPayment(String impUid, String reason) throws Exception {
        HttpURLConnection conn = null;
        try {
            // 1. 액세스 토큰 획득
            String token = getIamportToken();
            if (token == null || token.isEmpty()) {
                System.err.println("토큰 발급 실패 - 결제 취소를 진행할 수 없습니다.");
                return false;
            }
            
            System.out.println("결제 취소 요청 시작 - impUid: " + impUid + ", 토큰: " + token);
            
            // 2. 결제 취소 API 호출
            URL url = new URL("https://api.iamport.kr/payments/cancel");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", token); // 토큰에 이미 Bearer가 포함됨
            conn.setDoOutput(true);
            
            // 필수 파라미터만 포함한 간결한 요청 바디
            String jsonInputString = "{\"imp_uid\":\"" + impUid + "\"}";
            
            System.out.println("결제 취소 요청 데이터: " + jsonInputString);
            
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("결제 취소 응답 코드: " + responseCode);
            
            // 응답 처리
            if (responseCode >= 200 && responseCode < 300) {
                // 성공 응답
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String jsonResponse = response.toString();
                    System.out.println("결제 취소 응답: " + jsonResponse);
                    
                    // 응답에서 성공 여부 확인 - 아임포트 API 응답 형식에 맞게 수정
                    if (jsonResponse.contains("\"code\":0")) {
                        System.out.println("결제 취소 성공!");
                        return true;
                    } else {
                        System.err.println("결제 취소 실패 - API는 성공했지만 응답에 오류 코드가 포함되어 있습니다.");
                        return false;
                    }
                }
            } else {
                // 에러 응답
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.err.println("결제 취소 API 오류 응답 (" + responseCode + "): " + response.toString());
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("결제 취소 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @GetMapping("/buy")
    public String buy(Model model){
        return "buy/buy";
    }
}