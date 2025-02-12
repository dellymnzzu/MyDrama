package com.MyDrama.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_like")
public class ItemLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 고유 번호
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;  // 아이템아이디

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;  // 멤버 아이디
    private LocalDateTime viewedAt; // 조회 시간
}
