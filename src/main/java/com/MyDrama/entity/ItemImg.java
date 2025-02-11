package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class ItemImg {
    @Id
    @Column(name = "itemImg_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String itemImgUrl; // 썸네일 경로
    private String itemImgName; // 썸네일 저장 이름
    private String oriItemImgName; // 썸네일 본래 이름

    public void updateItemImg(String itemImgUrl, String itemImgName, String oriItemImgName){
        this.itemImgUrl = itemImgUrl;
        this.itemImgName = itemImgName;
        this.oriItemImgName = oriItemImgName;
    }
}
