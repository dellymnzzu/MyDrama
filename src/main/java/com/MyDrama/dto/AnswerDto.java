package com.MyDrama.dto;

import com.MyDrama.entity.Answer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDto {

    private Long id;
    private String title;
    private String QuestionId;
    private String content;



}
