package com.MyDrama.service;

import com.MyDrama.entity.ChatRoom;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.ChatRoomRepository;
import com.MyDrama.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatRoom findByChatRoomId(Long roomId) {
        // roomId로 ChatRoom을 찾고, 해당 ChatRoom에 연결된 Member를 확인합니다.
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("ChatRoom not found for roomId: " + roomId);
        }

        // ChatRoom에 연결된 Member 확인
        Member member = chatRoom.getMember();
        String memberEmail = member.getEmail();
        System.out.println("Member Email : " + memberEmail);

        return chatRoom;
    }

    // 특정 회원(memberId)를 기반으로 새로운 채팅방을 생성합니다.
    // 동일한 회원으로 이미 생성된 채팅방이 있다면 기존 채팅방을 반환합니다.


    @Transactional
    public ChatRoom createRoom(Long memberId) {
        try {
            // memberId를 통해 회원 정보를 검색
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
            // 해당 회원으로 이미 존재하는 채팅방 확인
            ChatRoom existingRoom = chatRoomRepository.findByMember(member);
            if (existingRoom != null) {
                return existingRoom;  // 기존 채팅방이 있다면 해당 객체 반환
            }
            // 새로운 채팅방 생성
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setRoomId(memberId);  // RoomId를 회원 Id로 설정
            chatRoom.setMember(member); // 채팅방과 회원 연결
            return chatRoomRepository.save(chatRoom); // 생성된 채팅방 저장 및 반환
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 특정 회원(memberId)이 특정 채팅방(roomId)에 접근 할 수있는지 확인한다.
    // roomId는 확인 할 채팅방의 Id
    // memberId 접근 권한을 확인 할 회원의 ID
    // 접근 권한이 있으면 true, 없으면 false
    public boolean validateAccess(Long roomId, Long memberId) {
        // roomId로 ChatRoom을 찾고 해당 ChatRoom의 MemberId와 주어진 memberId를 비교
        return chatRoomRepository.findById(roomId)
                .map(room -> room.getMember().getId().equals(memberId)) // memberId가 일치하면 true 반환
                .orElse(false); // ChatRoom이 존재하지 않으면 false 반환
    }

    // 특정 회원(memberId)을 기준으로 해당 회원이 소유한 채팅방을 검색한다.
    // memberId를 기준으로 채팅방 검색
    public ChatRoom findByMemberId(Long memberId) {
        return chatRoomRepository.findByMember_Id(memberId);
    }

}