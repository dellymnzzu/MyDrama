package com.MyDrama.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

    private Long id;

    @NotEmpty(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotEmpty(message = "내용은 필수 입력값입니다.")
    private String content;


} 