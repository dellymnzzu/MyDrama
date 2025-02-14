package com.MyDrama.service;

import com.MyDrama.entity.Member;
import com.MyDrama.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor  // 자동주입해준다.
public class MemberService {

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
}
