package com.MyDrama.repository;

import com.MyDrama.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("select o from Order o where o.member.userId = :userId order by o.orderDate desc")
    List<Order> findOrders(@Param("userId") String userId, Pageable pageable);

    @Query("select count(o) from Order o where o.member.userId = :userId")
    Long countOrder(@Param("userId") String userId);
}