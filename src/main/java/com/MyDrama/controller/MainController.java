package com.MyDrama.controller;

import com.MyDrama.config.SecurityUtil;
import com.MyDrama.dto.ItemDto;
import com.MyDrama.dto.ItemSearchDto;
import com.MyDrama.dto.MainItemDto;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.ItemCrawl;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.ItemCrawlerRepository;
import com.MyDrama.service.ItemService;
import com.MyDrama.service.MemberService;
import com.MyDrama.service.VisitorService;
import com.MyDrama.service.WebCrawlerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    private final WebCrawlerService webCrawlerService;
    private final ItemCrawlerRepository itemCrawlerRepository;
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
        model.addAttribute("mainItems", items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);

        visitorService.incrementVisitorCount();
        model.addAttribute("todayCount", visitorService.getTodayVisitorCount());
        model.addAttribute("totalCount", visitorService.getTotalVisitorCount());
        List<ItemCrawl> crawledItems = itemCrawlerRepository.findAll();
        model.addAttribute("crawledItems", crawledItems);

        return "main";
    }

    @GetMapping("/crawling")
    public String executeCrawling(Model model) {
        System.out.println("크롤링 컨트롤러 시작");
        try {
            webCrawlerService.crawl();
            List<ItemCrawl> items = itemCrawlerRepository.findAll();
            System.out.println("크롤링된 아이템 수: " + items.size());
            
            model.addAttribute("message", "크롤링이 성공적으로 완료되었습니다.");
            model.addAttribute("items", items);
        } catch (Exception e) {
            System.out.println("컨트롤러에서 오류 발생: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "크롤링 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "crawling/result";
    }

    @GetMapping("/items")
    public String getItems(Model model) {
        List<ItemCrawl> items = itemCrawlerRepository.findAll();
        model.addAttribute("items", items);
        return "crawling/itemList";
    }
}
