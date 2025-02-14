package com.MyDrama.repository;

import com.MyDrama.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


/*
* ItemRepository : Item 엔티티의 데이터베이스 액세스를 담당하는 JPA Repository
* - JpaRepository<Item,Long> : 기본적인 CRUD 기능을 제공한다.
* - QuerydsLPredicateExecutor<Item> : QueryDSL을 활용한 동적 쿼리 기능을 제공한다.
*/
@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

//    /*
//    * 상품명 기준으로 검색하는 메서드
//    * SQL : select * form item where title = ?(title)
//    */
//
//    List<Item> findByTitle(String title);
//
//
//    /*
//    * 상품명 또는 상품 상세 설명을 기준으로 검색하는 메서드
//    * SQL : select * form item where title = ? or desciprion = ?
//    * */
//    List<Item> findByItemNmOrDesciprion(String title,String description);
//






}
