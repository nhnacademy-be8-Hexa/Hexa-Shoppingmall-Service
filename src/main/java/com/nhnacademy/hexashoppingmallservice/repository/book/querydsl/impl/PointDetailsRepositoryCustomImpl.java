package com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.impl;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.entity.order.QPointDetails;
import com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.PointDetailsRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

@Component
public class PointDetailsRepositoryCustomImpl extends QuerydslRepositorySupport implements PointDetailsRepositoryCustom {

    public PointDetailsRepositoryCustomImpl() {
        super(PointDetails.class);
    }

    @Override
    public Long sumPointDetailsIncrementByMemberId(String memberId) {
        QPointDetails pointDetails = QPointDetails.pointDetails;

        // Querydsl 쿼리 작성
        JPQLQuery<Integer> query = from(pointDetails)
                .select(pointDetails.pointDetailsIncrement.sum())
                .where(pointDetails.member.memberId.eq(memberId));

        return query.fetchOne().longValue();
    }
}
