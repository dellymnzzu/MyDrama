package com.MyDrama.repository;

import com.MyDrama.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);  // 이메일 찾기
    Member findByUserId(String userId); // 아이디 찾기
    Member findByTel(String tel);  // 전화번호 찾기
}
