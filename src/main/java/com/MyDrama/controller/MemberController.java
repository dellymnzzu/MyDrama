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


    //회원가입 페이지
    @GetMapping(value = "/signup")
    public String userForm(Model model){  // 타임리프를 이용해서
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "/members/signup";
    }

    @PostMapping(value = "/signup")
    public String userForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,Model model){
        //Valid를 검사해서 결과가 에러가 있으면 실행
        if(bindingResult.hasErrors()){
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

    @GetMapping(value = "/signin")
    public String userLogin(){
        return "/members/signin";
    }

    @PostMapping(value = "/signin")
    public String userLoginPost(){
        return "/members/signin";
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
