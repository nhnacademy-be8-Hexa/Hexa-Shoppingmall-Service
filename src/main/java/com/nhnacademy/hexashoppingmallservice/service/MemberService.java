package com.nhnacademy.hexashoppingmallservice.service;

import com.nhnacademy.hexashoppingmallservice.dto.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.Role;
import com.nhnacademy.hexashoppingmallservice.exception.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.RatingRepository;
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
    private final RatingRepository ratingRepository;
    private final MemberStatusRepository memberStatusRepository;

    @Transactional
    public Member createMember(MemberRequestDTO memberRequestDto) {
        Rating rating = ratingRepository.findById(Long.parseLong(memberRequestDto.getRatingId())).orElseThrow(
                () -> new RatingNotFoundException(String.format("%s", memberRequestDto.getRatingId()))
        );
        MemberStatus memberStatus = memberStatusRepository.findById(Long.parseLong(memberRequestDto.getStatusId())).orElseThrow(
                () -> new MemberStatusNotFoundException(String.format("%s", memberRequestDto.getStatusId()))
        );

        Member member = new Member(
                memberRequestDto.getMemberId(),
                memberRequestDto.getMemberPassword(),
                memberRequestDto.getMemberName(),
                memberRequestDto.getMemberNumber(),
                memberRequestDto.getMemberBirthAt(),
                memberRequestDto.getMemberCreatedAt(),
                memberRequestDto.getMemberLastLoginAt(),
                Role.valueOf(memberRequestDto.getMemberRole()),
                rating,
                memberStatus
        );
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
