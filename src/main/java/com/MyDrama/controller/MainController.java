package com.MyDrama.controller;

import com.MyDrama.config.SecurityUtil;
import com.MyDrama.dto.ItemSearchDto;
import com.MyDrama.dto.MainItemDto;
import com.MyDrama.dto.ItemImgDto;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.Member;
import com.MyDrama.service.ItemService;
import com.MyDrama.service.MemberService;
import com.MyDrama.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MemberService memberService;
    private final VisitorService visitorService;
    private final ItemService itemService;

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
    public String main(HttpServletRequest request, Model model, ItemSearchDto itemSearchDto, Optional<Integer> page, Principal principal, HttpServletResponse response) {
        if (principal != null) {  // 로그인한 사용자가 있는 경우
            String userId = principal.getName();  // 현재 로그인한 사용자의 ID를 가져옴
            Member member = memberService.findUserId(userId);  // userId를 매개변수로 전달
            model.addAttribute("mainDto", member);
        }
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0,5);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        System.out.println(items.getNumber()+" -> 아이템 현재 페이지");
        System.out.println(items.getTotalPages()+" -> 전체 페이지");
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);

        visitorService.incrementVisitorCount();
        model.addAttribute("todayCount", visitorService.getTodayVisitorCount());
        model.addAttribute("totalCount", visitorService.getTotalVisitorCount());

        return "main";
    }
}
