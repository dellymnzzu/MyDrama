package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class ItemImg extends BaseEntity {
    @Id
    @Column(name = "itemImg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String imgUrl; // 아이템 이미지 url
    private String imgName; // 아이템 저장 이름
    private String oriImgName; // 아이템 본래 이름
    private String repImgYn; //대표이미지
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;


    //업데이트를 명령문을 내리지 않아도 업데이트가 자동으로 되는 이유 : 변경감지를 때문에
    public void updateItemImg(String imgUrl, String imgName, String oriImgName){
        this.imgUrl = imgUrl;
        this.imgName = imgName;
        this.oriImgName = oriImgName;
    }
}
