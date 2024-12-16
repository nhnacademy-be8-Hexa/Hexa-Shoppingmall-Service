package com.nhnacademy.hexashoppingmallservice.repository;

import com.nhnacademy.hexashoppingmallservice.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {
    Page<Member> findByMemberIdContaining(String search, Pageable pageable);
}