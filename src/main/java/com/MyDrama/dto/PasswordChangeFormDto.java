package com.MyDrama.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class PasswordChangeFormDto {
    //단일 책임의 원칙(SRP) 때문에 보안적인 이유로 하나의 책임만 가지도록 설계한다.


    @NotEmpty(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @Length(min = 4, max = 16, message = "비밀번호는 4자이상, 16자 이하로 입력해주세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\\\W)(?=\\\\S+$).{4,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 4~16자의 비밀번호여야합니다.")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String confirmPassword;

}
