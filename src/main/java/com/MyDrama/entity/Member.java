package com.MyDrama.entity;


import com.MyDrama.constant.Gender;
import com.MyDrama.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member {
    @Id
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
    private Role role;  //

    @Column(nullable = false)
    private boolean mailingAgreement;  // 이메일 수신 동의

    @Column(nullable = false)
    private boolean smsAgreement;  // SMS 수신 동의

    private String provider; // 소셜 로그인 제공자 정보 (예 : 구글, 네이버 등)

    private String picture; // 소셜 로그인 사용자를 위한 필드
}


