package com.nhnacademy.hexashoppingmallservice.service;

import com.nhnacademy.hexashoppingmallservice.dto.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.Role;
import com.nhnacademy.hexashoppingmallservice.exception.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.MemberNotFoundException;
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
import java.util.function.Consumer;

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

        if (memberRepository.findById(memberRequestDto.getMemberId()).isPresent()) {
            throw new MemberAlreadyExistException(String.format("%s", memberRequestDto.getMemberId()));
        }

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

    @Transactional
    public Member updateMember(String memberId, MemberRequestDTO memberRequestDto) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(String.format("%s", memberId))
        );

        updateIfNotNull(memberRequestDto.getMemberPassword(), member::setMemberPassword);
        updateIfNotNull(memberRequestDto.getMemberName(), member::setMemberName);
        updateIfNotNull(memberRequestDto.getMemberNumber(), member::setMemberNumber);
        updateIfNotNull(memberRequestDto.getMemberBirthAt(), member::setMemberBirthAt);
        updateIfNotNull(memberRequestDto.getMemberCreatedAt(), member::setMemberCreatedAt);
        updateIfNotNull(memberRequestDto.getMemberLastLoginAt(), member::setMemberLastLoginAt);
        updateIfNotNull(memberRequestDto.getMemberRole(), role -> member.setMemberRole(Role.valueOf(role)));

        if (memberRequestDto.getRatingId() != null) {
            Rating rating = ratingRepository.findById(Long.parseLong(memberRequestDto.getRatingId()))
                    .orElseThrow(() -> new RatingNotFoundException(String.format("%s", memberRequestDto.getRatingId())));
            member.setRating(rating);
        }

        if (memberRequestDto.getStatusId() != null) {
            MemberStatus memberStatus = memberStatusRepository.findById(Long.parseLong(memberRequestDto.getStatusId()))
                    .orElseThrow(() -> new MemberStatusNotFoundException(String.format("%s", memberRequestDto.getStatusId())));
            member.setMemberStatus(memberStatus);
        }

        return member;
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }

    @Transactional
    public Page<Member> findMembersById(Pageable pageable, String memberId) {
        return memberRepository.findByMemberIdContaining(memberId, pageable);
    }
}
