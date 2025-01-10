package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.service.book.LikeService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final JwtUtils jwtUtils;

    /**
     * 새로운 좋아요를 추가하는 엔드포인트
     *
     * @param bookId   좋아요를 추가할 책의 ID
     * @param memberId 좋아요를 추가할 회원의 ID
     * @return 응답 상태 코드
     */
    @PostMapping("/likes")
    public ResponseEntity<Void> createLike(
            @RequestParam Long bookId,
            @RequestParam String memberId,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        likeService.createLike(bookId, memberId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 특정 책의 좋아요 합계를 조회하는 엔드포인트
     *
     * @param bookId 좋아요 합계를 조회할 책의 ID
     * @return 좋아요 합계
     */
    @GetMapping("/books/{bookId}/likes")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long bookId) {
        Long count = likeService.sumLikes(bookId);
        return ResponseEntity.ok(count);
    }


    /**
     * 좋아요를 토글(추가/취소)하는 엔드포인트
     *
     * @param bookId   좋아요를 토글할 책의 ID
     * @param memberId 좋아요를 토글할 회원의 ID
     * @return 응답 상태 코드
     */
    @PutMapping("/likes/toggle")
    public ResponseEntity<Void> toggleLike(
            @RequestParam Long bookId,
            @RequestParam String memberId,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        likeService.toggleLike(bookId, memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 특정 회원이 특정 도서에 대해 좋아요를 눌렀는지 확인하는 메서드
     *
     * @param bookId   확인할 도서의 ID
     * @param memberId 확인할 회원의 ID
     * @return 회원이 해당 도서에 좋아요를 눌렀으면 true, 그렇지 않으면 false
     */
    @GetMapping("/likes/status")
    public ResponseEntity<Boolean> hasLiked(@RequestParam Long bookId,
                                            @RequestParam String memberId) {
        return ResponseEntity.ok(likeService.hasLiked(bookId, memberId));
    }

}
