package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.service.book.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /**
     * 새로운 좋아요를 추가하는 엔드포인트
     *
     * @param bookId   좋아요를 추가할 책의 ID
     * @param memberId 좋아요를 추가할 회원의 ID
     * @return 응답 상태 코드
     */
    @PostMapping("/auth/likes")
    public ResponseEntity<Void> createLike(
            @RequestParam Long bookId,
            @RequestParam String memberId) {
        likeService.createLike(bookId, memberId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 특정 책의 좋아요 합계를 조회하는 엔드포인트
     *
     * @param bookId 좋아요 합계를 조회할 책의 ID
     * @return 좋아요 합계
     */
    @GetMapping("/books/{bookId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long bookId) {
        Long count = likeService.sumLikes(bookId);
        return ResponseEntity.ok(count);
    }
}
