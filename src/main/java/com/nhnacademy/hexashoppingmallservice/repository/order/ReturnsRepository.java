package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnsRepository extends JpaRepository<Returns, Long> {
    Optional<Returns> findByOrder_Member(Member member);
}
