package com.MyDrama.service;

import com.MyDrama.constant.Role;
import com.MyDrama.entity.Answer;
import com.MyDrama.entity.Member;
import com.MyDrama.entity.Question;
import com.MyDrama.repository.AnswerRepository;
import com.MyDrama.repository.MemberRepository;
import com.MyDrama.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.MyDrama.constant.QuestionStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    public void createAnswer(Long questionId, String content, String userId) {
        // 답변 작성자 조회
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // ADMIN 권한 체크
        if (member.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 답변을 작성할 수 있습니다.");
        }

        // 질문 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));

        // 이미 답변이 있는지 확인
        if (question.getStatus() == QuestionStatus.COMPLETED) {
            throw new IllegalStateException("이미 답변이 존재하는 질문입니다.");
        }

        // 답변 생성 및 저장
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setMember(member);

        // 질문의 답변 상태 업데이트
        question.setAnswer(answer);
        question.setStatus(QuestionStatus.COMPLETED);

        answerRepository.save(answer);
    }

    public void updateAnswer(Long answerId, String content, String userId) {
        // 답변 작성자 조회
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // ADMIN 권한 체크
        if (member.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 답변을 수정할 수 있습니다.");
        }

        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변을 찾을 수 없습니다."));

        // 답변 내용 업데이트
        answer.setContent(content);
    }

    public void deleteAnswer(Long answerId, String userId) {
        // 답변 작성자 조회
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // ADMIN 권한 체크
        if (member.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 답변을 삭제할 수 있습니다.");
        }

        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변을 찾을 수 없습니다."));

        // 질문의 답변 상태 업데이트
        Question question = answer.getQuestion();
        question.setAnswer(null);
        question.setStatus(QuestionStatus.WAITING);

        // 답변 삭제
        answerRepository.delete(answer);
    }

    @Transactional(readOnly = true)
    public Answer getAnswer(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));
        return question.getAnswer();
    }
}
