package com.MyDrama.service;

import com.MyDrama.dto.CommentDto;
import com.MyDrama.entity.Comment;
import com.MyDrama.entity.Item;
import com.MyDrama.entity.Member;
import com.MyDrama.repository.CommentRepository;
import com.MyDrama.repository.ItemRepository;
import com.MyDrama.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //1.댓글을 저장하는 기능
    public CommentDto saveComment(String content, String userId, Long itemId, int rating) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1-5 사이여야 합니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("사용자 정보가 필요합니다.");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("상품 ID가 필요합니다.");
        }

        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 블랙리스트 체크
        if (member.isBlacklisted()) {
            throw new IllegalStateException("신고 누적으로 인해 댓글 작성이 제한된 사용자입니다.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setMember(member);
        comment.setItem(item);
        comment.setContent(content.trim());
        comment.setRating(rating);
        comment.setLike(0);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    //2.댓글을 불러오는 기능(최신 순)
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByItemId(Long itemId, int page, int size) {
        if (itemId == null) {
            throw new IllegalArgumentException("상품 ID가 필요합니다.");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<Comment> comments = commentRepository.findByItemId(itemId, pageRequest);
        
        return comments.getContent().stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .memberUserId(comment.getMember().getUserId())
                        .itemId(comment.getItem().getId())
                        .rating(comment.getRating())
                        .regTime(comment.getRegTime())
                        .build())
                .collect(Collectors.toList());
    }

    //dto -> entity로 변환
    private CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberUserId(comment.getMember().getUserId())
                .rating(comment.getRating())
                .regTime(comment.getRegTime())
                .build();
    }

    //3.댓글을 불러오는 기능(좋아요 순)
    //4.작성자가 댓글을 수정하는 기능 (수정됨)표시

    //5.작성자가 댓글을 삭제하는 기능 (관리자 삭제 기능 추후 추가)
    public void deleteComment(Long commentId, String userId){
        Member member = memberRepository.findByUserId(userId);
        if(member == null){
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Comment comment = commentRepository.findByIdAndMemberId(commentId, member.getId());
        commentRepository.delete(comment);
    }

    // 댓글 신고 메서드 추가
    public void reportComment(Long commentId, String reporterId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        
        Member reporter = memberRepository.findByUserId(reporterId);
        if (reporter == null) {
            throw new EntityNotFoundException("신고자를 찾을 수 없습니다.");
        }
        
        // 자신의 댓글은 신고할 수 없음
        if (comment.getMember().getUserId().equals(reporterId)) {
            throw new IllegalStateException("자신의 댓글은 신고할 수 없습니다.");
        }
        
        Member commentAuthor = comment.getMember();
        commentAuthor.increaseReportCount();
        memberRepository.save(commentAuthor);
    }
}
