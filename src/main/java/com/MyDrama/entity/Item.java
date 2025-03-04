package com.MyDrama.entity;

import com.MyDrama.constant.*;
import com.MyDrama.dto.ItemFormDto;
import com.MyDrama.entity.BaseEntity;
import com.MyDrama.exception.OutOfstockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Entity
@Getter
@Setter
@ToString
@Table(name = "item")
public class Item extends BaseEntity {
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 제목
    
    @Column(nullable = false)
    private Integer price;   // 가격

    @Column(nullable = false,columnDefinition = "LONGTEXT")
    private String description;  // 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MainCategory mainCategory;  // 전체 카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; //카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinConcern skinConcern; // 피부고민별

    @Enumerated(EnumType.STRING)
    private LINE line;  // 라인별

    private int viewCount;  //조회수

    private int stockNumber; // 수량

    private ItemSellStatus itemSellStatus; // 상품 상태

    @OneToMany(mappedBy = "item" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ItemLike> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ItemImg> itemImgs = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    public void updateItem(ItemFormDto itemFormDto){
        this.title = itemFormDto.getTitle();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.description = itemFormDto.getDescription();
        this.mainCategory = itemFormDto.getMainCategory();
        this.category = itemFormDto.getCategory();
        this.skinConcern = itemFormDto.getSkinConcern();
        this.line = itemFormDto.getLine();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }



public void removeStock(int stockNumber){
    int restStock = this.stockNumber - stockNumber;
            if(restStock<0){
                throw new OutOfstockException("상품의 재고가 부족합니다.(현재 재고 수량 : "+this.stockNumber+")");
            }
            this.stockNumber = restStock;
}


public void addStock(int stockNumber){this.stockNumber += stockNumber;}
}
