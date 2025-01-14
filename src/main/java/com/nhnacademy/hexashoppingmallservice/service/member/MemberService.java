package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.book.MemberUpdateDTO;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.MemberDeletedException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RatingRepository ratingRepository;
    private final MemberStatusRepository memberStatusRepository;

    @Transactional
    public Member createMember(MemberRequestDTO memberRequestDto) {
        if (!ratingRepository.existsById(Long.parseLong(memberRequestDto.getRatingId()))) {
            throw new RatingNotFoundException("Rating ID %s is not found".formatted(memberRequestDto.getRatingId()));
        }

        Rating rating = ratingRepository.findById(Long.parseLong(memberRequestDto.getRatingId())).orElseThrow();

        if (!memberStatusRepository.existsById(Long.parseLong(memberRequestDto.getStatusId()))) {
            throw new MemberStatusNotFoundException(
                    "Status ID %s is not found".formatted(memberRequestDto.getStatusId()));
        }

        MemberStatus memberStatus =
                memberStatusRepository.findById(Long.parseLong(memberRequestDto.getStatusId())).orElseThrow();

        if (memberRepository.existsById(memberRequestDto.getMemberId())) {
            throw new MemberAlreadyExistException(String.format("%s", memberRequestDto.getMemberId()));
        }

        Member member = Member.of(
                memberRequestDto.getMemberId(),
                memberRequestDto.getMemberPassword(),
                memberRequestDto.getMemberName(),
                memberRequestDto.getMemberNumber(),
                memberRequestDto.getMemberEmail(),
                memberRequestDto.getMemberBirthAt(),
                rating,
                memberStatus

        );
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<MemberProjection> getMembers(Pageable pageable, String search) {
        if (search != null && !search.isEmpty()) {
            return memberRepository.findByMemberIdContaining(search, pageable).getContent();
        }
        return memberRepository.findAllBy(pageable).getContent();
    }

    @Transactional
    public Member getMember(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("Member ID %s not found".formatted(memberId))
        );
    }

    @Transactional
    public Member updateMember(String memberId, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(String.format("%s", memberId))
        );

        updateIfNotNull(memberUpdateDTO.getMemberName(), member::setMemberName);
        updateIfNotNull(memberUpdateDTO.getMemberPassword(), member::setMemberPassword);
        updateIfNotNull(memberUpdateDTO.getMemberNumber(), member::setMemberNumber);
        updateIfNotNull(memberUpdateDTO.getMemberBirthAt(), member::setMemberBirthAt);
        updateIfNotNull(memberUpdateDTO.getMemberLastLoginAt(), member::setMemberLastLoginAt);
        updateIfNotNull(memberUpdateDTO.getMemberEmail(), member::setMemberEmail);

        if (memberUpdateDTO.getRatingId() != null) {
            Rating rating = ratingRepository.findById(Long.parseLong(memberUpdateDTO.getRatingId()))
                    .orElseThrow(
                            () -> new RatingNotFoundException(String.format("%s", memberUpdateDTO.getRatingId())));
            member.setRating(rating);
        }

        if (memberUpdateDTO.getStatusId() != null) {
            MemberStatus memberStatus = memberStatusRepository.findById(Long.parseLong(memberUpdateDTO.getStatusId()))
                    .orElseThrow(() -> new MemberStatusNotFoundException(
                            String.format("%s", memberUpdateDTO.getStatusId())));
            member.setMemberStatus(memberStatus);
        }

        return member;
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }

    @Transactional(readOnly = true)
    public List<MemberProjection> searchMembersById(Pageable pageable, String memberId) {
        return memberRepository.findByMemberIdContaining(memberId, pageable).getContent();
    }

    @Transactional
    public void login(String memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(String.format("%s", memberId))
        );
        if(member.getMemberStatus().getStatusId() == 3) {
            throw new MemberDeletedException(String.format("%s", memberId));
        }
        member.login();
    }

    @Transactional
    public void saveAll(List<? extends Member> members) {
        memberRepository.saveAll(members);
    }


    // 검색 조건에 따른 회원 수 조회
    public long countBySearch(String search) {
        if (search != null && !search.isEmpty()) {
            return memberRepository.countByMemberIdContaining(search);
        }
        return memberRepository.count();
    }

}
