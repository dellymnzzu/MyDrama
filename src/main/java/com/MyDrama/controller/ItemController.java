package com.MyDrama.controller;


import com.MyDrama.dto.ItemFormDto;
import com.MyDrama.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //상품 등록 페이지 조회
    @GetMapping(value = "/write")
    public String itemNew(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    //상품 등록
    @PostMapping(value = "/write")
    public String itemNewForm(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                              @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){
        //@RequestParam에는 이미지 파일 데이타가 들어간다.
        if(bindingResult.hasErrors()){

        return "item/itemForm";
    }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null){
        model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다. ");
        return "item/itemForm";
        }
        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }



}
