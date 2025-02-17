package com.MyDrama.controller;

import com.MyDrama.dto.MemberFormDto;
import com.MyDrama.dto.MemberupdateDto;
import com.MyDrama.dto.PasswordChangeFormDto;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    boolean confirmCheck = false;



    //회원가입 페이지
    @GetMapping(value = "/signup")
    public String userForm(Model model){  // 타임리프를 이용해서
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "members/signup";
    }

//회원가입 post
    @PostMapping(value = "/signup")
    public String userForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,Model model){
        //Valid를 검사해서 결과가 에러가 있으면 실행
        if(bindingResult.hasErrors()){
            return "members/signup";
        }
        // 비밀번호와 비밀번호 재입력이 틀리다면 비밀번호가 일치하지 않습니다 출력
        if(!memberFormDto.getPassword().equals(memberFormDto.getConfirmPassword())){
            model.addAttribute("errorMessage","비밀번호가 일치하지 않습니다.");
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


//로그인 페이지
    @GetMapping(value = "/signin")
    public String userLogin(){
        return "/members/signin";
    }
    @GetMapping(value = "/signin/error")
    public String userLoginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
        return "members/signin";

    }


    @GetMapping(value = "/mypage")
    public String mypage(Model model, Principal principal){
        //principal을 사용하는 이유는 로그인한 사용자의 정보를 받아오기 위해 사용한다.
//주로 인증된 사용자 정보를 얻기 위해 사용한다.
        String loginId = principal.getName(); // 로그인한 아이디를 가져온다.

        Member userId = memberRepository.findByUserId(loginId);  // memberRepository에서 로그인한 아이디를 이용해서 사용자의 정보를 조회한다.

        userId.setPassword(null);  // 비밀번호는 초기값으로 null값을 넣었다.

        model.addAttribute("memberFormDto", userId); //조회한 사용자 정보를 memberFormDto로 모델을 추가해서 뷰에서 사용가능하도록 한다.
        return "members/myPage";
    }

    //회원 정보 수정 (비밀번호 제외)
    @PostMapping(value = "/mypage/update")
    public String updateMypage(@Valid MemberupdateDto memberupdateDto, BindingResult bindingResult, Model model, Principal principal){
        System.out.println("=== 회원정보 수정 시작 ===");
        System.out.println("memberupdateDto: " + memberupdateDto);
        if (bindingResult.hasErrors()) {
            System.out.println("입력값 오류 : "+ bindingResult.getAllErrors());
            // 에러 발생 시 현재 사용자 정보를 다시 가져옴
            model.addAttribute("errorMessage", "입력값에 오류가 있습니다. 다시 확인해주세요.");
            return "members/myPage";
        }

        try{
            // 현재 로그인한 사용자의 id를 설정
            String loginId = principal.getName();
            Member userId = memberRepository.findByUserId(loginId);
            memberupdateDto.setId(userId.getId());  // id 설정
            
            System.out.println("회원정보 수정 시도");
            memberService.updateMember(memberupdateDto);
            System.out.println("회원정보 수정 완료");
            model.addAttribute("successMessage","정보가 성공적으로 수정되었습니다.");
        }catch (Exception e){
            System.out.println("회원정보 수정 중 에러 발생");
            System.out.println("에러 내용: " + e.getMessage());
            model.addAttribute("errorMessage","정보 수정 중 에러가 발생했습니다.");
            return "members/myPage";
        }
        
        return "redirect:/member/mypage";
    }

    @GetMapping(value = "/mypage/rePw")
    public String repw(Model model){
        model.addAttribute("passwordChangeFormDto",new PasswordChangeFormDto());
        return "members/updatePw";
    }

    @PostMapping(value = "/mypage/rePw")
    public String rePw(@Valid PasswordChangeFormDto passwordChangeFormDto,
                       BindingResult bindingResult,
                       Principal principal,
                       Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("입력값 오류 : " + bindingResult.getAllErrors());
            model.addAttribute("errorMessage", "입력값에 오류가 있습니다. 다시 확인해주세요.");
            return "members/updatePw";
        }

        if (principal == null) {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다. 다시 로그인해주세요.");
            return "/member/signin";
        }

        String loginId = principal.getName();
        Member member = memberRepository.findByUserId(loginId);
        
        passwordChangeFormDto.setId(member.getId());  // id를 설정해야함함

        if (!passwordEncoder.matches(passwordChangeFormDto.getCurrentPassword(), member.getPassword())) {
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
            return "members/updatePw";
        }

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!passwordChangeFormDto.getNewPassword().equals(passwordChangeFormDto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
            return "members/updatePw";
        }
        // 현재 비밀번호가 일치하는지 확인
        try{
            memberService.updatePassword(passwordChangeFormDto);
            model.addAttribute("successMessage","비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "members/updatePw";
        }
        return "redirect:/member/signin";
    }

    @DeleteMapping("/mypage/delete")  // DELETE를 POST로 변경
    public String deleteMember(Principal principal, Model model) {
        try {
            if(principal == null) {
                model.addAttribute("errorMessage", "로그인 정보가 없습니다.");
                return "redirect:/member/signin";
            }
            
            memberService.deleteMember(principal.getName());
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "members/myPage";
        }
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
