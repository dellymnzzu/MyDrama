package com.MyDrama.entity;

import com.MyDrama.constant.Category;
import com.MyDrama.constant.LINE;
import com.MyDrama.constant.MainCategory;
import com.MyDrama.constant.SkinConcern;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@ToString
@Entity
@Table(name = "item")
public class Item {
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 제목

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

    @OneToMany(mappedBy = "item" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ItemLike> likeList = new ArrayList<>();







}
