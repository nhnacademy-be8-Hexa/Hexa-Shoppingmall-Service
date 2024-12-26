package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.exception.TokenPermissionDenied;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberStatusService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class MemberStatusController  {
    private final MemberStatusService memberStatusService;
    private final JwtUtils jwtUtils;
    @GetMapping("/api/memberStatus")
    public List<MemberStatus> getMemberStatus() {
        return memberStatusService.getAllMemberStatus();
    }

    @PostMapping("/api/memberStatus")
    public ResponseEntity<MemberStatus> createMemberStatus(@RequestBody MemberStatus memberStatus, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.status(201).body(memberStatusService.createMemberStatus(memberStatus));
    }

    @DeleteMapping("/api/memberStatus/{memberStatusId}")
    public ResponseEntity<MemberStatus> deleteMemberStatus(@PathVariable Long memberStatusId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
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

    @PutMapping("/api/memberStatus/{memberStatusId}")
    public ResponseEntity<MemberStatus> updateMemberStatus(@PathVariable Long memberStatusId, @RequestBody MemberStatusRequestDTO memberStatusRequestDTO, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(memberStatusService.updateMemberStatus(memberStatusId, memberStatusRequestDTO));
    }
}
