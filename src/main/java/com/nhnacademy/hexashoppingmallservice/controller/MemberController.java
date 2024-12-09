package com.nhnacademy.hexashoppingmallservice.controller;

import com.nhnacademy.hexashoppingmallservice.dto.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.exception.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, SIZE);
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
}
