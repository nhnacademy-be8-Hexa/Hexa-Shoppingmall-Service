package com.nhnacademy.hexashoppingmallservice.controller;

import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.exception.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.service.MemberStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @DeleteMapping("/api/memberStatus/{memberStatusId}")
    public ResponseEntity<MemberStatus> deleteMemberStatus(@PathVariable Long memberStatusId) {
        MemberStatus memberStatus = memberStatusService.getMemberStatus(memberStatusId);
        if (Objects.isNull(memberStatus)) {
            throw new MemberStatusNotFoundException(Long.toString(memberStatusId));
        }
        try {
            memberStatusService.deleteMemberStatus(memberStatusId);
        }
        catch (RuntimeException e) {
            throw new SqlQueryExecuteFailException(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
