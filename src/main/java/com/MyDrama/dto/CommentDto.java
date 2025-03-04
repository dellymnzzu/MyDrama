package com.MyDrama.dto;

import java.time.LocalDateTime;

import lombok.ToString;
import org.modelmapper.ModelMapper;

import com.MyDrama.entity.Comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@Builder
public class CommentDto {
    private Long id; //댓글 id
    private String memberUserId;
    private Long itemId; //아이템 id
    private String content; //댓글 내용
    private int like; //좋아요 숫자
    private LocalDateTime regTime; //작성 날짜
    private int rating; // 별점

    public static ModelMapper modelMapper = new ModelMapper();

    public static CommentDto of(Comment comment){
        return modelMapper.map(comment, CommentDto.class);
    }

}
