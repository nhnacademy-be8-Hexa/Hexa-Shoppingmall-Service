package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.CreatePointDetailDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl.PointDetailsRepositoryCustomImpl;
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
    private final PointDetailsRepositoryCustomImpl pointDetailsRepositoryCustom;

    @Transactional
    public PointDetails createPointDetails(CreatePointDetailDTO createPointDetailDTO, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        Member member = memberRepository.findById(memberId).get();
        PointDetails pointDetails = PointDetails.builder()
                .pointDetailsId(null)
                .member(member)
                .pointDetailsIncrement(createPointDetailDTO.getPointDetailsIncrement())
                .pointDetailsComment(createPointDetailDTO.getPointDetailsComment())
                .pointDetailsDatetime(LocalDateTime.now())
                .build();
        return pointDetailsRepository.save(pointDetails);
    }

    @Transactional(readOnly = true)
    public Long sumPoint(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return pointDetailsRepositoryCustom.sumPointDetailsIncrementByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<PointDetailsProjection> getPointDetails(Pageable pageable, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return pointDetailsRepository.findAllByMemberMemberId(memberId, pageable).getContent();
    }
}
