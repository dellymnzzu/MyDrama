package com.MyDrama.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// 메시지 브로커 설정
// 클라이언트와 서버 간 메시지를 전달하기 위한 브로커 설정을 정의합니다.
// websocket과 stomp 프로토콜을 설정하기 위한 클래스
@Configuration
@EnableWebSocketMessageBroker
@Controller
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic으로 시작하는 메시지를 처리하기 위해 내장된 간단한 브로커를 활성화한다.
        config.enableSimpleBroker("/topic");
        //클라이언트에서 메시지를 보낼 때 사용하는 prefix를 "/app"으로 설정합니다.
        config.setApplicationDestinationPrefixes("/app");
        //이 설정은 클라이언트가 /app 경로로 메시지를 보내고, /topic 경로에서 메시지를 구독(subscribe)할 수 있도록 지원합니다.
    }

    // stomp 엔드 포인트를 등록합니다.
    //클라이언트가 websocket에 연결 할 수 있도록 엔드포인트를 설정합니다.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 "/app/gs-guide-websocket"엔드 포인트를 통해 연결을 시작한다.
        // socketJs를 사용하여 websocket을 지원하지 않는 브라우저에서도 동작하도록 설정한다.
        registry.addEndpoint("/app/gs-guide-websocket").withSockJS();
        //  withSockJs는 웹소켓을 지원하지 않는 브라우저에서도 작동하도록 설정
    }

}