package com.MyDrama.entity;

import com.MyDrama.dto.NoticeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "notice")
public class Notice extends BaseEntity{
    @Id
    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String imgName;
    private String oriImgName;
    private String imgUrl;

    public void updateNotice(String title,String description){
        this.title = title;
        this.description = description;

    }

    public void updateNoticeImg(String title, String description,String imgName,String oriImgName, String imgUrl){
        this.title = title;
        this.description = description;
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }


}
