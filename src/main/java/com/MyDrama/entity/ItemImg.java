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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    private String itemImgUrl; // 아이템 이미지 url
    private String itemImgName; // 아이템 저장 이름
    private String oriItemImgName; // 아이템 본래 이름

    public void updateItemImg(String itemImgUrl, String itemImgName, String oriItemImgName){
        this.itemImgUrl = itemImgUrl;
        this.itemImgName = itemImgName;
        this.oriItemImgName = oriItemImgName;
    }
}
