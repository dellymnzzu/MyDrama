package com.MyDrama.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ChatbotService {

    private final String[] greetings = {
            "안녕하세요! 오늘도 좋은 하루 보내세요!",
            "안녕하세요 무엇을 도와드릴까요?",
            "어서오세요 MyDrama입니다 잘 부탁드립니다^^",
            "반가워요!"
    };

    public String getRandomGreeting(){
        Random random = new Random();
        return greetings[random.nextInt(greetings.length)];  // 랜덤으로 4개중 하나를 선택한다.
    }

    public Map<String, Object> processQuery(String question) {
        question = question.replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
                .toLowerCase()
                .trim();

        Map<String, Object> response = new HashMap<>();

        // 인사 관련 질문
        if (question.contains("안녕") || question.contains("하이") || question.contains("반가워")) {
            response.put("type", "faq");
            response.put("answer", getRandomGreeting());
            return response;
        }

        // 더 많은 질문을 원하면 if문으로 작성하면 된다.

        // 기본 응답
        response.put("type", "redirect");
        response.put("message", "죄송합니다. 질문을 이해하지 못했습니다. 더 자세한 내용은 1:1 채팅으로 문의해주세요.");

        return response;
    }


}
