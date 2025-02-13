package com.MyDrama.repository;

import com.MyDrama.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepositoryCustom extends JpaRepository<Item,Long> {
}
