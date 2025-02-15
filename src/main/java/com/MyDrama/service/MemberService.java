package com.MyDrama.service;

import com.MyDrama.entity.Member;
import com.MyDrama.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor  // 자동주입해준다.
// MemberService는 로그인을 확인하는 용도이기 때문에 MemberService에 부모를 심어줘야해서 UserDetailService로 상속을 받는다.
// UseDetailsService 회원 정보를 가져오는 역활
public class MemberService implements UserDetailsService {  // UserDetailService는 인터페이스이기 때문에 오버라이딩 해야한다.

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){  // controller에서 saveMember를 불렀을 때
        validateDuplicateMember(member);
        return memberRepository.save(member);  // 데이터베이스에 저장해
    }


    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByUserId(member.getUserId());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        findMember = memberRepository.findByTel(member.getTel());
        if(findMember !=null){
            throw new IllegalStateException("이미 가입된 번호입니다.");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {  // userId가 넘어온다.
        // 로그인 시 userId를 받아서 DB에서 회원 정볼르 찾는다.
        Member member = memberRepository.findByUserId(userId);
        if(member==null) {  // userId가 null 값이라면?
            throw new UsernameNotFoundException(userId);  // 예외처리
        }
        //빌더 패턴 (객체를 리턴한다.)
        //Spring Security의 UserDetails 객체 생성
        return User.builder().username(member.getUserId())  // User로 변경 후 검사를 한다.
                .password(member.getPassword())  // 비밀번호 설정 (DB에 암호화된 비밀번호 저장됨)
                .roles(member.getRole().toString())  // 권한도 검사하고
                .build(); // 된다면 로그인이 된다.
    }
}
