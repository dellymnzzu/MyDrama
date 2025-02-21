package com.MyDrama.entity;

import com.MyDrama.dto.BannerDto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "banners")
public class Banner extends BaseEntity {
    @Id
    @Column(name = "banner_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Long itemId;

    // 배너 이미지 정보 추가
    private String imgName;
    private String oriImgName;
    private String imgUrl;

    public void updateBanner(BannerDto bannerDto) {
        this.title = bannerDto.getTitle();
        this.description = bannerDto.getDescription();
        this.itemId = bannerDto.getItemId();
    }

    // 이미지 정보 업데이트 메서드 추가
    public void updateBannerImage(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
