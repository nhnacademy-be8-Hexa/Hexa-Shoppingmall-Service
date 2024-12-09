package com.nhnacademy.hexashoppingmallservice.service;

import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.repository.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberStatusService {
    private final MemberStatusRepository memberStatusRepository;

    @Transactional
    public MemberStatus createMemberStatus(MemberStatus memberStatus) {
        return memberStatusRepository.save(memberStatus);
    }

    @Transactional(readOnly = true)
    public List<MemberStatus> getAllMemberStatus() {
        return memberStatusRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MemberStatus getMemberStatus(Long id) {
        return memberStatusRepository.findById(id).orElse(null);
    }
}
