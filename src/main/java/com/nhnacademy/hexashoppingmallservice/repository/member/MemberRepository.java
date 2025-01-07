package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Page<MemberProjection> findByMemberIdContaining(String search, Pageable pageable);
    Page<MemberProjection> findAllBy(Pageable pageable);
    // 검색 조건이 있는 경우 회원 수 계산
    long countByMemberIdContaining(String search);}
