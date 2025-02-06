package com.MyDrama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

}
