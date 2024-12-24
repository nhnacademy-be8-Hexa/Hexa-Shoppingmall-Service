package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.dto.book.MemberUpdateDTO;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.TokenPermissionDenied;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import com.nhnacademy.hexashoppingmallservice.service.book.LikeService;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final Integer SIZE = 10;
    private final MemberService memberService;
    private final LikeService likeService;
    private final JwtUtils jwtUtils;

    @GetMapping("/api/members")
    public List<MemberProjection> getMembers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String search, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        Pageable pageable = PageRequest.of(page, SIZE);
        if(search != null && !search.isEmpty()) {
            return memberService.searchMembersById(pageable, search);
        }
        return memberService.getMembers(pageable);
    }

    @GetMapping("/api/members/{memberId}")
    public Member getMember(@PathVariable String memberId) {
        return memberService.getMember(memberId);
    }

    @PostMapping("/api/members")
    public ResponseEntity<Member> createMember(@RequestBody @Valid MemberRequestDTO memberRequestDto) {
        return ResponseEntity.status(201).body(memberService.createMember(memberRequestDto));
    }

    @PutMapping("/api/members/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable String memberId, @RequestBody @Valid MemberUpdateDTO memberUpdateDTO, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return ResponseEntity.ok(memberService.updateMember(memberId, memberUpdateDTO));
    }

    @GetMapping("/api/members/{memberId}/liked-books")
    public ResponseEntity<List<Book>> getLikedBooks(@PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        List<Book> likedBooks = likeService.getBooksLikedByMember(memberId);
        return ResponseEntity.ok(likedBooks); // 200 OK
    }
  
    @PutMapping("/api/members/{memberId}/login")
    public ResponseEntity<Void> loginMember(
            @PathVariable String memberId
    ){
        memberService.login(memberId);
        return ResponseEntity.ok().build();
    }
}
