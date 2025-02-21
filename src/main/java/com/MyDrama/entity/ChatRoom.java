package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@ToString(exclude = {"messages", "member"})
public class ChatRoom {
    @Id
    private Long roomId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    private boolean hasNewMessage = false;
}
