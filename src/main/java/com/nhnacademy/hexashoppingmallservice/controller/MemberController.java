package com.nhnacademy.hexashoppingmallservice.controller;

import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.exception.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/api/members")
    public Page<Member> getMembers(Pageable pageable) {
        return memberService.findMembers(pageable);
    }

    @GetMapping("/api/members/{memberId}")
    public Member getMember(@PathVariable String memberId) {
        return memberService.findMemberById(memberId).orElseThrow(
                () -> new MemberNotFoundException(memberId)
        );
    }

    @PostMapping("/api/members")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        return ResponseEntity.status(201).body(memberService.createMember(member));
    }
}
