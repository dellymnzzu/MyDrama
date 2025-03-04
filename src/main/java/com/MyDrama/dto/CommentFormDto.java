package com.MyDrama.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentFormDto {
    private Long itemId;
    private String content;
}