package com.MyDrama.service;

import com.MyDrama.constant.Gender;
import com.MyDrama.dto.MemberFormDto;
import com.MyDrama.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setUserId("test");
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setTel("01012345678");
        memberFormDto.setZipcode("521352");
        memberFormDto.setGender(Gender.valueOf("MEN"));
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setDetailAddress("182호");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest() {
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);
        assertEquals(member.getUserId(), savedMember.getUserId());
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getTel(), savedMember.getTel());
        assertEquals(member.getZipcode(), savedMember.getZipcode());
        assertEquals(member.getGender(), savedMember.getGender());
        assertEquals(member.getDetailAddress(), savedMember.getDetailAddress());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }
}