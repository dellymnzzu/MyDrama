package com.MyDrama.controller;

import com.MyDrama.dto.MemberFormDto;
import com.MyDrama.entity.Member;
import com.MyDrama.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    boolean confirmCheck = false;



    //회원가입 페이지
    @GetMapping(value = "/signup")
    public String userForm(Model model){  // 타임리프를 이용해서
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "members/signup";
    }

//회원가입 post
    @PostMapping(value = "/signup")
    public String userForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,Model model){
        //Valid를 검사해서 결과가 에러가 있으면 실행
        if(bindingResult.hasErrors()){
            return "members/signup";
        }
        // 비밀번호와 비밀번호 재입력이 틀리다면 비밀번호가 일치하지 않습니다 출력
        if(!memberFormDto.getPassword().equals(memberFormDto.getConfirmPassword())){
            model.addAttribute("errorMessage","비밀번호가 일치하지 않습니다.");
            return "members/signup";
        }
        try{
            Member member = Member.createMember(memberFormDto,passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage",e.getMessage());
            return "members/signup";
        }
        return "redirect:/";
    }


//로그인 페이지
    @GetMapping(value = "/signin")
    public String userLogin(){
        return "/members/signin";
    }
    @GetMapping(value = "/signin/error")
    public String userLoginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
        return "members/signin";

    }



    @GetMapping(value = "/findEmail")
    public String findEmailForm(){
        return "/members/findEmail";
    }
    @PostMapping(value = "/findEmail")
    public String findEmailFormPost(){
        return "/members/findEmail";
    }

    @GetMapping(value = "/findPassword")
    public String findPasswordForm(){
        return "/members/findPassword";
    }
    @PostMapping(value = "/findPassword")
    public String findPasswordFormPost(){
        return "/members/findPassword";
    }

}
