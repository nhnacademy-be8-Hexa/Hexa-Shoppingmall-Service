package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    @Transactional
    public void deleteMemberStatus(Long id) {
        memberStatusRepository.deleteById(id);
    }

    @Transactional
    public MemberStatus updateMemberStatus(Long id, MemberStatusRequestDTO memberStatusRequestDTO) {
        MemberStatus memberStatus = memberStatusRepository.findById(id).orElse(null);
        if (Objects.isNull(memberStatus)) {
            throw new MemberStatusNotFoundException(Long.toString(id));
        }
        memberStatus.setStatusName(memberStatusRequestDTO.getStatusName());
        return memberStatus;
    }
}
