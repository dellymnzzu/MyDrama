package com.MyDrama.dto;

import com.MyDrama.entity.Notice;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class NoticeDto {
    private Long id;
    @NotEmpty(message = "공지 제목은 필수 입력 값입니다.")
    private String title;
    @NotEmpty(message = "설명은 필수 입력 값입니다.")
    private String description;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;


    private static ModelMapper modelMapper = new ModelMapper();
    public Notice createNotice(){
        // noticeDto->notice 연결
        return modelMapper.map(this, Notice.class);

    }

    public static NoticeDto of(Notice notice){
       //notice -> noticeDto와 연결
        return modelMapper.map(notice, NoticeDto.class);
    }
}
