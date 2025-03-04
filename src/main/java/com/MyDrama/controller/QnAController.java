package com.MyDrama.controller;

import com.MyDrama.dto.QuestionForm;
import com.MyDrama.constant.QuestionStatus;
import com.MyDrama.dto.QuestionDto;
import com.MyDrama.entity.Member;
import com.MyDrama.entity.Question;
import com.MyDrama.repository.QuestionRepository;
import com.MyDrama.service.AnswerService;
import com.MyDrama.service.MemberService;
import com.MyDrama.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.MyDrama.constant.Role;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class QnAController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final MemberService memberService;

    // 질문 목록 페이지
    @GetMapping("/qna/list")
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<QuestionDto> questions = questionService.getQuestionList(pageable);
        model.addAttribute("questions", questions);
        return "qnA/QuestionList";
    }
    // 질문 작성 폼
    @GetMapping("/qna/create")
    public String createForm(Model model,Principal principal) {
        if (principal == null) {
            return "로그인이 필요합니다.";
        }
        model.addAttribute("questionForm", new QuestionForm());
        return "qnA/QuestionForm";
    }
    // 질문 등록 처리
    @PostMapping("/qna/create")
    public String create(@Valid QuestionForm questionForm, 
                        BindingResult bindingResult, 
                        Principal principal) {
        if (bindingResult.hasErrors()) {
            return "qnA/QuestionForm";
        }

        if (principal == null) {
            return "redirect:/member/signin";
        }

        questionService.createQuestion(questionForm, principal.getName());
        return "redirect:/qna/list";
    }

    // 질문 상세 페이지
    @GetMapping("/qna/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/signin";
        }

        try {
            QuestionDto questionDto = questionService.getQuestion(id);
            Member member = memberService.findUserId(principal.getName());
            
            // ADMIN이 아니고, 본인 질문이 아닌 경우에 에러 발생
            if(member.getRole() != Role.ADMIN && !questionDto.getUserId().equals(principal.getName())){
                redirectAttributes.addFlashAttribute("errorMessage", "본인의 질문만 볼 수 있습니다.");
                return "redirect:/qna/list";
            }

            model.addAttribute("question", questionDto);
            
            if (questionDto.getStatus() == QuestionStatus.COMPLETED) {
                model.addAttribute("answer", answerService.getAnswer(id));
            }
            return "qnA/QuestionDtl";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/qna/list";
        }
    }
    // 답변 등록 처리
    @PostMapping("/admin/qna/answer/{id}")
    @ResponseBody
    public String createAnswer(@PathVariable("id") Long id, 
                             @RequestParam String content,
                             Principal principal) {
        if (principal == null) {
            return "로그인이 필요합니다.";
        }

        try {
            answerService.createAnswer(id, content, principal.getName());
            return "답변이 등록되었습니다.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }
}
