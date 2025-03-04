package com.MyDrama.service;

import com.MyDrama.dto.QuestionDto;
import com.MyDrama.entity.Question;
import com.MyDrama.repository.QuestionRepository;
import com.MyDrama.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.MyDrama.entity.Member;
import com.MyDrama.constant.QuestionStatus;
import com.MyDrama.dto.QuestionForm;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    public Page<QuestionDto> getQuestionList(Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(q -> {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setTitle(q.getTitle());
            dto.setMemberName(q.getMember().getName());
            dto.setCreatedTime(q.getRegTime());
            dto.setStatus(q.getStatus());
            return dto;
        });
    }

    public void createQuestion(QuestionForm form, String userId) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new jakarta.persistence.EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Question question = new Question();
        question.setTitle(form.getTitle());
        question.setContent(form.getContent());
        question.setMember(member);
        question.setStatus(QuestionStatus.WAITING);

        questionRepository.save(question);
    }

    public QuestionDto getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));
        
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        dto.setMemberName(question.getMember().getName());
        dto.setUserId(question.getMember().getUserId());
        dto.setStatus(question.getStatus());
        dto.setCreatedTime(question.getRegTime());
        return dto;
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }
}