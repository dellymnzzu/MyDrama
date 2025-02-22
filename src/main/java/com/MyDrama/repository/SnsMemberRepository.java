package com.MyDrama.repository;

import com.MyDrama.entity.SnsMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnsMemberRepository extends JpaRepository<SnsMember,Long> {
    Optional<SnsMember> findByEmail(String email);
}
