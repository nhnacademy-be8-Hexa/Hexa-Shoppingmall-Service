package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnsReasonRepository extends JpaRepository<ReturnsReason, Long> {
//    @Query("SELECT r FROM Returns r " +
//            "JOIN Order o ON r.orderId = o.orderId " +
//            "WHERE o.memberId = :memberId")
//    List<Returns> findByMemberId(@Param("memberId") String memberId);
}
