package com.MyDrama.controller;

import com.MyDrama.config.SecurityUtil;
import com.MyDrama.constant.Role;
import com.MyDrama.entity.ChatMessage;
import com.MyDrama.entity.ChatRoom;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.ChatMessageRepository;
import com.MyDrama.repository.ChatRoomRepository;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.service.ChatService;
import com.MyDrama.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatbotService chatbotService;
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    @GetMapping("/chatbot")
    public String chatbotPage(){return "chatbot/chatBot";}

    @PostMapping("/chatbot/query")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> query(@RequestBody Map<String,String> request){
        String question = request.get("question");
        Map<String,Object> response = chatbotService.processQuery(question);
        return ResponseEntity.ok(response);
    }

    // 채팅방 조회 및 생성
    @GetMapping("/chat/{roomId}")  // 특정 채팅방에 접근하는 요청 처리(user의 고유 Id를 roomId로 줌)
    public String chat(@PathVariable Long roomId, Model model) {
        try {

            String userId = SecurityUtil.getCurrentUserEmail();
            System.out.println("채팅방 이메일 : "+userId);  // 이메일 잘 나옴
            if (userId == null) {
                throw new IllegalArgumentException("Unable to retrieve user email");
            }
            // 이메일을 통해 회원 정보 조회
            Member member = memberRepository.findByUserId(userId);
            System.out.println("이메일을 통해 회원 정보 조회 멤버 : "+member);
            if(member == null){
                throw new IllegalArgumentException("멤버를 찾을 수 없다.");
            }

            // 요청된 채팅방 정보 조회
            ChatRoom chatRoom = chatService.findByChatRoomId(roomId);

            System.out.println("chatRoom : "+ chatRoom);

            // 현재 로그인한 사용자 정보 가져오기

            // 사용자가 채팅방에 접근 권한이 있는지 확인 (소유자 또는 관리자만 접근이 가능하다.)
            if (chatRoom != null && !chatRoom.getMember().getId().equals(member.getId()) &&
                    !member.getRole().equals(Role.ADMIN)) {
                model.addAttribute("alertMessage", "접근할 수 없는 채팅방입니다.");
                return "redirect:/?error=unauthorized"; // 권한이 없을 경우, 접근할 수 없는 채팅방입니다 출력 후 홈으로 리다이렉트
            }
            // 채팅방이 존재하지 않을 경우 채팅방 새로 생성
            if (chatRoom == null) {
                chatRoom = chatService.createRoom(member.getId());
            }

            // 디비에 있는 채팅 이전 메시지들 가져오기
            List<ChatMessage> previousMessages = chatMessageRepository.findByChatRoomOrderByIdAsc(chatRoom);

            //view에 데이터 전달
            model.addAttribute("chatroom", chatRoom);
            model.addAttribute("messages", previousMessages);
            model.addAttribute("memberUser", member);
            return "chat/chatting"; // 채팅 페이지로 이동

        } catch (Exception e) {  // 에러 발생시 처리
            System.out.println("에러 발생: " + e.getMessage());
            model.addAttribute("alertMessage", "접근할 수 없는 채팅방입니다.");
            return "redirect:/?error=unauthorized";
        }

    }

    // 채팅 메시지 전송
    @MessageMapping("/{roomId}") // webSocket 메시지 매핑
    @SendTo("/topic/{roomId}") // 특정 채팅방에 메시지 브로드 캐스트
    @Transactional
    public Map<String, Object> handleChat(@DestinationVariable Long roomId, @RequestBody Map<String, String> messageData) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 채팅방 정보 조회
            ChatRoom chatRoom = chatService.findByChatRoomId(roomId);
            String message = messageData.get("message"); // 메시지 내용
            String sender = messageData.get("sender");  //메시지 보낸 사람

            // 채팅방이 없을 경우 에러 반환
            if (chatRoom == null) {
                response.put("sender", "System");
                response.put("message", "Room not found");
                response.put("error", true);
                return response;
            }

            // 메시지 저장 로직 추가
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatRoom(chatRoom); // 메시지가 속한 채팅방
            chatMessage.setMessage(message); // 메시지 내용
            chatMessage.setSender(sender);  //메시지 보낸사람 / userName 대신 sender 사용
            chatMessageRepository.save(chatMessage);  // ChatMessageRepository 필요



            response.put("sender", sender);  // "User" 대신 실제 sender 사용
            response.put("message", message);
            response.put("error", false);
            return response;
        } catch (Exception e) {
            response.put("sender", "System");
            response.put("message", "Error: " + e.getMessage());
            response.put("error", true);
            return response;
        }
    }

    // 채팅방 생성
    @PostMapping("/create-room")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Long> request) {
        try {
            Long memberId = request.get("memberId"); // 요청으로부터 memberId 가져오기
            System.out.println("memberId : "+ memberId);

            if (memberId == null) { // memberId가 없는 경우 에러 반환
                return ResponseEntity.badRequest().body("memberId is required");
            }

            // 이미 존재하는 채팅방 확인
            ChatRoom existingRoom = chatService.findByMemberId(memberId);
            if (existingRoom != null) {  // 이미 존재하는 채팅방 반환
                return ResponseEntity.ok(existingRoom.getRoomId());
            }

            // 새 채팅방 생성
            ChatRoom chatRoom = chatService.createRoom(memberId);
            if (chatRoom == null) { //채팅방 생성 실패 시 에러 반환
                return ResponseEntity.badRequest().body("Failed to create chat room");
            }

            // 환영 메시지 추가
            ChatMessage welcomeMessage = new ChatMessage();
            welcomeMessage.setChatRoom(chatRoom);  // 새 채팅방
            welcomeMessage.setMessage("환영합니다."); // 환영 메시지
            welcomeMessage.setSender("관리자"); // 메시지 발신자
            chatMessageRepository.save(welcomeMessage); // 메시지 저장

            return ResponseEntity.ok(chatRoom.getRoomId()); // 생성된 채팅방 Id 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
