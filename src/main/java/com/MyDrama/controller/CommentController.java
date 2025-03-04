package com.MyDrama.controller;

import com.MyDrama.config.SecurityUtil;
import com.MyDrama.dto.CommentDto;
import com.MyDrama.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final static int PAGE_SIZE = 5;

    @PostMapping("/new") //댓글 등록
    public @ResponseBody ResponseEntity saveComment(@RequestBody CommentDto commentDto, Principal principal) {
        try {
            if (principal == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }

            String userId = principal.getName(); // SecurityUtil 대신 principal에서 직접 가져옴
            
            // 요청 데이터 검증
            if (commentDto.getItemId() == null) {
                return new ResponseEntity<>("상품 ID가 필요합니다.", HttpStatus.BAD_REQUEST);
            }
            if (commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
                return new ResponseEntity<>("댓글 내용을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (commentDto.getRating() < 1 || commentDto.getRating() > 5) {
                return new ResponseEntity<>("별점은 1-5 사이여야 합니다.", HttpStatus.BAD_REQUEST);
            }

            CommentDto savedComment = commentService.saveComment(
                    commentDto.getContent(),
                    userId,
                    commentDto.getItemId(),
                    commentDto.getRating()
            );
            return new ResponseEntity<>(savedComment, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            String userId = SecurityUtil.getCurrentUserEmail();
            commentService.deleteComment(commentId, userId);
            return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list/{itemId}")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable("itemId") Long itemId,
            @RequestParam(defaultValue = "0") int page) {
        try {
            List<CommentDto> comments = commentService.getCommentsByItemId(itemId, page, PAGE_SIZE);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/report/{commentId}")
    public ResponseEntity<String> reportComment(@PathVariable Long commentId, Principal principal) {
        try {
            // 로그인 체크
            if (principal == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }

            String userId = principal.getName();
            commentService.reportComment(commentId, userId);
            return ResponseEntity.ok("신고가 접수되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}