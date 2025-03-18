package com.MyDrama.controller;

import com.MyDrama.dto.BannerDto;
import com.MyDrama.dto.NoticeDto;
import com.MyDrama.entity.Banner;

import com.MyDrama.service.*;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final VisitorService visitorService;
    private final FileService fileService;
    private final BannerService bannerService;
    private final NoticeService noticeService;
    private final ExcelService excelService;


    //엑셀 다운로드
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadExcel() {
        try {
            ByteArrayInputStream in = excelService.generateExcelReport();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=content-analysis.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

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


    //공지사항 등록 페이지
    @GetMapping("/notice/new")
    public String notice(Model model) {
        model.addAttribute("noticeDto", new NoticeDto());
        return "notice/noticeForm";
    }

    //공지사항 등록 페이지
    @PostMapping("/notice/new")
    public String noticeForm(@Valid NoticeDto noticeDto,
                             BindingResult bindingResult,
                             @RequestParam("noticeImgFile") MultipartFile noticeImgFile,
                             Model model) {
        if(bindingResult.hasErrors()) {
            return "notice/noticeForm";
        }
        if(noticeImgFile.isEmpty() && noticeDto.getId() == null) {
            model.addAttribute("errorMessage", "이미지는 필수 입력 값입니다.");
            return "notice/noticeForm";
        }
        try {
            noticeService.saveNotice(noticeDto, noticeImgFile);
        } catch(Exception e) {
            model.addAttribute("errorMessage", "등록 중 에러가 발생하였습니다.");
            return "notice/noticeForm";
        }
        return "redirect:/admin/notice/list";
    }

    //공지사항 리스트 페이지
    @GetMapping("/notice/list")
    public String noticeList(Model model) {
        List<NoticeDto> noticeDtoList = noticeService.getNoticeList();
        model.addAttribute("noticeDtoList", noticeDtoList);
        return "notice/noticeList";
    }

    //공지사항 삭제
    @DeleteMapping("/notice/{noticeId}")
    @ResponseBody
    public ResponseEntity deleteNotice(@PathVariable("noticeId") Long noticeId){
        try{
            noticeService.deleteNotice(noticeId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("삭제 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("삭제완료",HttpStatus.OK);
    }

    //공지사항 업데이트 페이지
    @GetMapping("/notice/{noticeId}/modify")
    public String noticeModifyForm(@PathVariable("noticeId") Long noticeId, Model model) {
        try {
            NoticeDto noticeDto = noticeService.getNoticeDtl(noticeId);
            model.addAttribute("noticeDto", noticeDto);
            return "notice/noticeForm";
        } catch(Exception e) {
            model.addAttribute("errorMessage", "배너 정보를 불러오는데 실패했습니다.");
            return "notice/noticeList";
        }
    }

    //공지사항 업데이트
    @PostMapping("/notice/{noticeId}/modify")
    public String noticeUpdate(@PathVariable("noticeId") Long noticeId,
                               @Valid NoticeDto noticeDto,
                               BindingResult bindingResult,
                               @RequestParam("noticeImgFile") MultipartFile noticeImgFile,
                               Model model) {
        // ID 설정
        noticeDto.setId(noticeId);

        if(bindingResult.hasErrors()) {
            return "notice/noticeForm";
        }

        try {
            noticeService.updateNotice(noticeDto, noticeImgFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "배너 수정 중 오류가 발생했습니다.");
            return "notice/noticeForm";
        }
        return "redirect:/admin/notice/list";
    }

    @GetMapping(value = "/notice/{noticeId}")
    public String itemDtl(Model model, @PathVariable("noticeId") Long noticeId) {
        NoticeDto noticeDto = noticeService.getNoticeDtl(noticeId);
        model.addAttribute("noticeDto", noticeDto);


        return "notice/noticeDtl";
    }










}
