package com.nhnacademy.hexashoppingmallservice.repository;

import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatusRepository extends JpaRepository<MemberStatus, Long> {
}
