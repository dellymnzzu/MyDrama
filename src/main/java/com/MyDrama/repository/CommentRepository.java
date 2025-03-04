package com.MyDrama.repository;

import com.MyDrama.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment findByIdAndMemberId(Long commentId, Long memberId);
    Page<Comment> findByItemId(Long itemId, Pageable pageable);
}
