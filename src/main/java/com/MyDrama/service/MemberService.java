package com.MyDrama.service;

import com.MyDrama.dto.MemberFormDto;
import com.MyDrama.dto.MemberupdateDto;
import com.MyDrama.dto.PasswordChangeFormDto;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor  // 자동주입해준다.
// MemberService는 로그인을 확인하는 용도이기 때문에 MemberService에 부모를 심어줘야해서 UserDetailService로 상속을 받는다.
// UseDetailsService 회원 정보를 가져오는 역활
public class MemberService implements UserDetailsService {  // UserDetailService는 인터페이스이기 때문에 오버라이딩 해야한다.

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;  // MailService 주입

    public Member saveMember(Member member){  // controller에서 saveMember를 불렀을 때
        validateDuplicateMember(member);
        return memberRepository.save(member);  // 데이터베이스에 저장해
    }


    private void validateDuplicateMember(Member member) {
        Member findMember;
        findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다."); // 예외 발생
        }
        findMember = memberRepository.findByTel(member.getTel());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 전화번호입니다."); // 예외 발생
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

    //수정

    public void updateMember(MemberupdateDto memberupdateDto){

        Member member = memberRepository.findById(memberupdateDto.getId()).orElseThrow(EntityNotFoundException::new); // 멤버 리포지토리에서 id를 찾는데 없으면 예외처리해

        member.updateMember(memberupdateDto);
    }

    //비밀번호 수정
    public void updatePassword(PasswordChangeFormDto passwordChangeFormDto){
        Member member = memberRepository.findById(passwordChangeFormDto.getId())
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if(!passwordEncoder.matches(passwordChangeFormDto.getCurrentPassword(), member.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (!passwordChangeFormDto.getNewPassword().equals(passwordChangeFormDto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(passwordChangeFormDto.getNewPassword());
        member.updatePassword(encodedPassword);  // member 객체 대신 암호화된 비밀번호 전달
    }

    //회원 정보 삭제

    public void deleteMember(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
        }
        memberRepository.delete(member);
    }
    
    public Member findUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if(member == null) {
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
        }
        return member;  // Member 객체 반환
    }

    public String findUserIdAndSendEmail(String name, String email) {
        Member member = memberRepository.findByNameAndEmail(name, email)
            .orElseThrow(() -> new UsernameNotFoundException("해당 이름과 이메일로 가입된 계정이 없습니다."));

        // 로그 추가
        System.out.println("찾은 회원 정보:");
        System.out.println("이름: " + member.getName());
        System.out.println("이메일: " + member.getEmail());
        System.out.println("아이디: " + member.getUserId());

        // 이메일로 아이디 발송
        String subject = "MyDrama 아이디 찾기 결과";
        String text = "<div style='margin:20px;'>" +
                     "<h1>MyDrama 아이디 찾기 결과입니다.</h1>" +
                     "<br>" +
                     "<p>회원님의 아이디는 <strong>" + member.getUserId() + "</strong> 입니다.</p>" +
                     "<br>" +
                     "<p>감사합니다.</p>" +
                     "</div>";

        mailService.sendMail(email, subject, text);

        return "아이디가 이메일로 발송되었습니다.";
    }

    public String resetPassword(String userId, String email) {
        Member member = memberRepository.findByUserIdAndEmail(userId, email)  // findByNameAndEmail -> findByUserIdAndEmail
            .orElseThrow(() -> new UsernameNotFoundException("해당 아이디와 이메일로 가입된 계정이 없습니다."));

        // 로그 추가
        System.out.println("비밀번호 재설정 요청:");
        System.out.println("이메일: " + member.getEmail());
        System.out.println("아이디: " + member.getUserId());

        // 임시 비밀번호 생성
        String tempPassword = generateRandomPassword();
        System.out.println("임시 비밀번호: " + tempPassword);
        
        // 임시 비밀번호 암호화 후 저장
        member.updatePassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        // 이메일로 임시 비밀번호 발송
        String subject = "MyDrama 임시 비밀번호 발급";
        String text = "<div style='margin:20px;'>" +
                     "<h1>MyDrama 임시 비밀번호입니다.</h1>" +
                     "<br>" +
                     "<p>임시 비밀번호: <strong>" + tempPassword + "</strong></p>" +
                     "<br>" +
                     "<p>보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.</p>" +
                     "<br>" +
                     "<p>감사합니다.</p>" +
                     "</div>";

        mailService.sendMail(email, subject, text);

        return "임시 비밀번호가 이메일로 발송되었습니다.";
    }

    private String generateRandomPassword() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97)); // a-z
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65)); // A-Z
                    break;
                case 2:
                    key.append(random.nextInt(10)); // 0-9
                    break;
            }
        }
        return key.toString();
    }

}


