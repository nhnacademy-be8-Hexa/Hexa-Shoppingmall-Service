package com.nhnacademy.hexashoppingmallservice.repository.member;


import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOrderSummary3MRepository extends JpaRepository<MemberOrderSummary3M, String> {

    Boolean existsByMemberId(String memberId);
    MemberOrderSummary3M findByMemberId(String memberId);
}