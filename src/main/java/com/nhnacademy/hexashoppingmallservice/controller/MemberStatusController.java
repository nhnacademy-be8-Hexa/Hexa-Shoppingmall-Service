package com.nhnacademy.hexashoppingmallservice.controller;

import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.service.MemberStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberStatusController  {
    private final MemberStatusService memberStatusService;
    @GetMapping("/api/memberStatus")
    public List<MemberStatus> getMemberStatus() {
        return memberStatusService.getAllMemberStatus();
    }

    @PostMapping("/api/memberStatus")
    public ResponseEntity<MemberStatus> createMemberStatus(@RequestBody MemberStatus memberStatus) {
        return ResponseEntity.status(201).body(memberStatusService.createMemberStatus(memberStatus));
    }
}
