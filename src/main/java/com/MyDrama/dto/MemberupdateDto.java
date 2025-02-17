package com.MyDrama.dto;

import com.MyDrama.constant.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@NoArgsConstructor
public class MemberupdateDto {
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @Email
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자이상, 16자 이하로 입력해주세요.")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{4,16}",
//    message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 4~16자의 비밀번호여야합니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

    @NotBlank(message = "우편번호 찾기를 클릭해서 집주소를 알려주세요.")

    private String zipcode;
    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;
    private String detailAddress;

    private boolean mailingAgreement;

    private  boolean smsAgreement;
}
