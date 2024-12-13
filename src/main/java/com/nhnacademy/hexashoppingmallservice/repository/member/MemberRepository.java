package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Page<Member> findByMemberIdContaining(String search, Pageable pageable);
}
