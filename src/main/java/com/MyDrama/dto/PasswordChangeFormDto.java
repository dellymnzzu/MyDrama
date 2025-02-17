package com.MyDrama.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeFormDto {
    //단일 책임의 원칙(SRP) 때문에 보안적인 이유로 하나의 책임만 가지도록 설계한다.
    private Long id;

    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{4,16}",
//            message = "비밀번호는 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수 입력값입니다.")
    private String confirmPassword;

}
