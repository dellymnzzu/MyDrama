package com.MyDrama.dto;

import com.MyDrama.entity.Banner;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString

public class BannerDto {
    private static ModelMapper modelMapper = new ModelMapper();
    
    private Long id;
    @NotEmpty(message = "배너 제목은 필수 입력 값입니다.")
    private String title;
    @NotEmpty(message = "설명은 필수 입력 값입니다.")
    private String description;
    @NotNull(message = "아이템 ID는 필수 입력 값입니다.")
    private Long itemId;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    
    // 시간 필드 추가
    private LocalDateTime regTime;
    private LocalDateTime updateTime;



    public Banner createBanner() {
        Banner banner = new Banner();
        banner.setTitle(this.title);
        banner.setDescription(this.description);
        banner.setItemId(this.itemId);
        banner.setImgName(this.imgName);
        banner.setOriImgName(this.oriImgName);
        banner.setImgUrl(this.imgUrl);
        return banner;
    }

    public static BannerDto of(Banner banner) {
        return modelMapper.map(banner, BannerDto.class);
    }
}
