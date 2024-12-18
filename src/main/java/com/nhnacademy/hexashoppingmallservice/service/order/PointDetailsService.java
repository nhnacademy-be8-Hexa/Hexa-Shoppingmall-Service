package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.PointDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointDetailsService {
    private final PointDetailsRepository pointDetailsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PointDetails createPointDetails(PointDetails pointDetails, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        Member member = memberRepository.findById(memberId).get();
        pointDetails.setMember(member);
        pointDetails.setPointDetailsDatetime(LocalDateTime.now());
        return pointDetailsRepository.save(pointDetails);
    }

    @Transactional
    public Long sumPoint(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return pointDetailsRepository.sumPointDetailsIncrementByMemberId(memberId);
    }

    @Transactional
    public List<PointDetailsProjection> getPointDetails(Pageable pageable, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return pointDetailsRepository.findAllByMemberMemberId(memberId, pageable).getContent();
    }
}
