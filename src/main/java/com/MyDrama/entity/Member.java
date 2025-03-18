package com.MyDrama.entity;


import com.MyDrama.constant.Gender;
import com.MyDrama.constant.Role;
import com.MyDrama.dto.MemberFormDto;
import com.MyDrama.dto.MemberupdateDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ToString(exclude = {"chatRooms"})  // 순환 참조 방지
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 유저의 고유 아이디

    @Column(unique = true)
    private String userId;  // 유저아이디

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(unique = true)
        private String tel;  // 전화번호


    @Column(nullable = false)
    private String name;  // 유저 이름

    @Column(nullable = false)
    private String email;  // 유저 이메일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;  // 성별 (ENUM 타입)

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 10)
    private String zipcode; // 우편번호

    @Column(length = 100)
    private String address; // 기본주소

    @Column(length = 100)
    private String detailAddress;  // 상세주소

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT false")
    private boolean mailingAgreement = false;  // 이메일 수신 동의, 기본값 설정

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean smsAgreement = false;  // SMS 수신 동의

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int reportCount = 0; // 신고 횟수

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean blacklisted = false; // 블랙리스트 여부

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setUserId(memberFormDto.getUserId());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);  // 단방향 암호화
        member.setTel(memberFormDto.getTel());
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setGender(memberFormDto.getGender());
        member.setZipcode(memberFormDto.getZipcode());
        member.setRole(Role.USER);
        member.setAddress(memberFormDto.getAddress());
        member.setDetailAddress(memberFormDto.getDetailAddress());
        member.setMailingAgreement(memberFormDto.isMailingAgreement());
        return member;
    }

    public void updateMember(MemberupdateDto memberupdateDto){
        this.name = memberupdateDto.getName();
        this.email = memberupdateDto.getEmail();
        this.tel = memberupdateDto.getTel();
        this.zipcode = memberupdateDto.getZipcode();
        this.address = memberupdateDto.getAddress();
        this.detailAddress = memberupdateDto.getDetailAddress();
        this.smsAgreement = memberupdateDto.isSmsAgreement();
        this.mailingAgreement = memberupdateDto.isMailingAgreement();
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

    // 신고 횟수 증가 메서드
    public void increaseReportCount() {
        this.reportCount++;
        if (this.reportCount >= 5) {
            this.blacklisted = true;
        }
    }

    // 블랙리스트 상태 확인 메서드
    public boolean isBlacklisted() {
        return this.blacklisted;
    }
}


