package com.MyDrama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping(value = "/about")
    public String about(){
        return "about/about";
    }

    @GetMapping(value = "/")
    public String main(){
        return "/main";
    }


}
