package com.MyDrama.controller;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.entity.Banner;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.MyDrama.service.VisitorService;
import com.MyDrama.service.FileService;
import com.MyDrama.service.BannerService;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final VisitorService visitorService;
    private final FileService fileService;
    private final BannerService bannerService;

    @GetMapping("/dashboard")
    public String adminPage(Model model) {
        model.addAttribute("monthlyStats", visitorService.getMonthlyStats());
        model.addAttribute("totalVisitors", visitorService.getTotalVisitorCount());
        model.addAttribute("monthlyVisitors", visitorService.getMonthlyStats());
        model.addAttribute("dailyVisitors", visitorService.getDailyStats());
        model.addAttribute("todayVisitors", visitorService.getTodayVisitorCount());
        model.addAttribute("recentDailyStats", visitorService.getRecentDailyStats());
        return "admin/admin";
    }


    // 배너 페이지
    @GetMapping("/banner")
    public String banner(Model model) {
        model.addAttribute("bannerDto", new BannerDto());
        return "banner/bannerForm";
    }

    //배너 등록 페이지
    @PostMapping("/banner")
    public String bannerForm(@Valid BannerDto bannerDto, 
                            BindingResult bindingResult,
                            @RequestParam("bannerImgFile") MultipartFile bannerImgFile,
                            Model model) {
        
        if(bindingResult.hasErrors()) {
            return "banner/bannerForm";
        }
        if(bannerImgFile.isEmpty() && bannerDto.getId() == null) {
            model.addAttribute("errorMessage", "배너 이미지는 필수 입력 값입니다.");
            return "banner/bannerForm";
        }
        
        try {
            bannerService.saveBanner(bannerDto, bannerImgFile);
        } catch(Exception e) {
            model.addAttribute("errorMessage", "배너 등록 중 에러가 발생하였습니다.");
            return "banner/bannerForm";
        }
        return "redirect:/";
    }

    @GetMapping("/banner/list")
    public String bannerList(Model model) {
        List<BannerDto> bannerDtoList = bannerService.getBannerList();
        System.out.println("bannerDtoList: " + bannerDtoList);
        model.addAttribute("bannerDtoList", bannerDtoList);
        return "banner/bannerList";
    }

    //배너 삭제
    @DeleteMapping("/banner/{bannerId}")
    @ResponseBody
    public ResponseEntity deleteBanner(@PathVariable("bannerId") Long bannerId){
        try{
            bannerService.deleteBanner(bannerId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("삭제 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("삭제완료",HttpStatus.OK);
    }

    @GetMapping("/banner/{bannerId}/modify")
    public String bannerModifyForm(@PathVariable("bannerId") Long bannerId, Model model) {
        try {
            BannerDto bannerDto = bannerService.getBannerDtl(bannerId);
            model.addAttribute("bannerDto", bannerDto);
            return "banner/bannerForm";
        } catch(Exception e) {
            model.addAttribute("errorMessage", "배너 정보를 불러오는데 실패했습니다.");
            return "banner/bannerList";
        }
    }

    @PostMapping("/banner/{bannerId}/modify")
    public String bannerUpdate(@PathVariable("bannerId") Long bannerId, 
                              @Valid BannerDto bannerDto, 
                              BindingResult bindingResult, 
                              @RequestParam("bannerImgFile") MultipartFile bannerImgFile, 
                              Model model) {
        // ID 설정
        bannerDto.setId(bannerId);
        
        if(bindingResult.hasErrors()) {
            return "banner/bannerForm";
        }
        
        try {
            bannerService.updateBanner(bannerDto, bannerImgFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "배너 수정 중 오류가 발생했습니다.");
            return "banner/bannerForm";
        }
        return "redirect:/admin/banner/list";
    }




}
