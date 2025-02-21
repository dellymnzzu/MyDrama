package com.MyDrama.controller;

import com.MyDrama.config.SecurityUtil;
import com.MyDrama.entity.Member;
import com.MyDrama.service.MemberService;
import com.MyDrama.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MemberService memberService;
    private final VisitorService visitorService;

    private Member getLoggedInMember(Principal principal) {
        String userId = SecurityUtil.getCurrentUserEmail();
        return memberService.findUserId(userId);

    }

    @GetMapping(value = "/about")
    public String about() {
        return "about/about";
    }

    @GetMapping(value = "/about/map")
    public String map() {
        return "about/kakaoApi";
    }

    @GetMapping(value = "/")
    public String main(HttpServletRequest request, Model model, Principal principal, HttpServletResponse response) {
        if (principal != null) {  // 로그인한 사용자가 있는 경우
            String userId = principal.getName();  // 현재 로그인한 사용자의 ID를 가져옴
            Member member = memberService.findUserId(userId);  // userId를 매개변수로 전달
            model.addAttribute("mainDto", member);
        }

        visitorService.incrementVisitorCount();
        model.addAttribute("todayCount", visitorService.getTodayVisitorCount());
        model.addAttribute("totalCount", visitorService.getTotalVisitorCount());

        return "main";
    }
}
