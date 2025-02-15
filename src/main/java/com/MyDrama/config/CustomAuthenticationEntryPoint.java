package com.MyDrama.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

//인증되지 않은 사용자가 리소스 요청하면 차단 하는 클래스
// 관리자가 아닌데 관리자 페이지로 들어가는 경우 차단하게 하는 클래스이다.
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException)
         throws IOException, ServletException {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
   }
}
