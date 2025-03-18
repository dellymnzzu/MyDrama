package com.MyDrama.repository;

import com.MyDrama.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Member findByEmail(String email);  // 이메일 찾기
    Member findByUserId(String userId); // 아이디 찾기
    Member findByTel(String tel);  // 전화번호 찾기

    Optional<Member> findByNameAndEmail(String name, String email);
    Optional<Member> findByUserIdAndEmail(String userId, String email);
}
