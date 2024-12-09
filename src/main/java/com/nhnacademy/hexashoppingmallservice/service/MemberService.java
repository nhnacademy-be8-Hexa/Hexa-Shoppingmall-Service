package com.nhnacademy.hexashoppingmallservice.service;

import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(Member member) {
        if(memberRepository.findById(member.getMemberId()).isPresent()) {
            throw new MemberAlreadyExistException("Member already exists");
        }
        return memberRepository.save(member);
    }

    @Transactional
    public Page<Member> findMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Transactional
    public Optional<Member> findMemberById(String memberId) {
        return memberRepository.findById(memberId);
    }
}
