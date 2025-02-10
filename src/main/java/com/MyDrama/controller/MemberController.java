package com.MyDrama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    @GetMapping(value = "/signup")
    public String userForm(){
        return "/members/signup";
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
