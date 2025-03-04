package com.MyDrama.entity;

import com.MyDrama.entity.BaseEntity;
import com.MyDrama.entity.Member;
import com.MyDrama.constant.QuestionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.WAITING;

    public void updateAnswerStatus() {
        this.status = (this.answer != null) ? 
            QuestionStatus.COMPLETED : QuestionStatus.WAITING;
    }
}