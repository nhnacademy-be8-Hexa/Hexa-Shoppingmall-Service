package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.PointDetailsRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointDetailsRepository extends JpaRepository<PointDetails, Long>, PointDetailsRepositoryCustom {
//    @Query("SELECT SUM(pd.pointDetailsIncrement) FROM PointDetails pd WHERE pd.member.memberId = :memberId")
//    Long sumPointDetailsIncrementByMemberId(@Param("memberId") String memberId);
    Page<PointDetailsProjection> findAllByMemberMemberId(String memberId, Pageable pageable);

    int countAllByMemberMemberId(String memberId);
}
