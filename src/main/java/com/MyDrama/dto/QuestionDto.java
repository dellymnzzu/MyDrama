package com.MyDrama.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

import com.MyDrama.constant.QuestionStatus;

@Getter
@Setter
@ToString
public class QuestionDto {
    private Long id;
    private String title;
    private String content;
    private String memberName;
    private String userId;
    private QuestionStatus status;
    private LocalDateTime createdTime;
} 