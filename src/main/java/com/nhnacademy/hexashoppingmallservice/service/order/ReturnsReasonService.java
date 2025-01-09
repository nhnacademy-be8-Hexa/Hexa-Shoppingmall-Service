package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsReasonRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsReasonNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsReasonRepository;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Formatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnsReasonService {
    private final ReturnsReasonRepository returnsReasonRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReturnsReason createReturnsReason(ReturnsReasonRequestDTO returnsReasonRequestDTO) {
    ReturnsReason returnsReason = ReturnsReason.of(
                returnsReasonRequestDTO.getReturnsReason()
        );
        return returnsReasonRepository.save(returnsReason);

    }

    @Transactional
    public List<ReturnsReason> getReturnsReasons() {
        return returnsReasonRepository.findAll();
    }

    @Transactional
    public ReturnsReason getReturnsReason(Long returnsReasonId) {
        return returnsReasonRepository.findById(returnsReasonId).orElseThrow(
                () -> new ReturnsReasonNotFoundException("ReturnsReason ID: %s not found".formatted(returnsReasonId))
        );

    }

    @Transactional
    public ReturnsReason updateReturnsReason(Long returnsReasonId ,ReturnsReasonRequestDTO returnsReasonRequestDTO) {
        ReturnsReason returnsReason = returnsReasonRepository.findById(returnsReasonId)
                .orElseThrow(() -> new ReturnsReasonNotFoundException("ReturnsReason ID: " + returnsReasonId + " not found"));
        returnsReason.setReturnsReason(returnsReasonRequestDTO.getReturnsReason());

        return returnsReasonRepository.save(returnsReason);
    }

    @Transactional
    public void deleteReturnsReason(Long returnsReasonId) {
        returnsReasonRepository.findById(returnsReasonId).orElseThrow(
                () -> new ReturnsReasonNotFoundException("ReturnsReason ID: %s not found".formatted(returnsReasonId))
        );
        returnsReasonRepository.deleteById(returnsReasonId);
    }
}
