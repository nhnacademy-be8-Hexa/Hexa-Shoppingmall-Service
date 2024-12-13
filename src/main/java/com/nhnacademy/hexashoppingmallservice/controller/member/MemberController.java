package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
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

    @GetMapping("/api/members")
    public List<Member> getMembers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, SIZE);
        if(search != null && !search.isEmpty()) {
//            return memberService.findMembersById(pageable, search).getContent();
            List<Member> members = memberService.findMembersById(pageable, search).getContent();
            return members;
        }
        return memberService.findMembers(pageable).getContent();
    }

    @GetMapping("/api/members/{memberId}")
    public Member getMember(@PathVariable String memberId) {
        return memberService.findMemberById(memberId).orElseThrow(
                () -> new MemberNotFoundException(memberId)
        );
    }

    @PostMapping("/api/members")
    public ResponseEntity<Member> createMember(@RequestBody @Valid MemberRequestDTO memberRequestDto) {
        return ResponseEntity.status(201).body(memberService.createMember(memberRequestDto));
    }

    @PatchMapping("/api/members/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable String memberId, @RequestBody @Valid MemberRequestDTO memberRequestDto) {
        return ResponseEntity.ok(memberService.updateMember(memberId, memberRequestDto));
    }
}
