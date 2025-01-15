package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PointDetailsRepositoryTest {

    @Autowired
    private PointDetailsRepository pointDetailsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MemberStatus memberStatus;
    private Rating rating;
    private Member member;

    @BeforeEach
    void setUp() {
        // Create and persist MemberStatus
        memberStatus = MemberStatus.builder()
                .statusName("활성")
                .build();
        entityManager.persist(memberStatus);

        // Create and persist Rating
        rating = Rating.builder()
                .ratingName("실버")
                .ratingPercent(10)
                .build();
        entityManager.persist(rating);

        // Create and persist Member
        member = Member.of(
                "test1",
                "password1234",
                "John Doe",
                "01012345678",
                "test@test.com",
                LocalDate.of(1990, 5, 3),
                rating,
                memberStatus
        );
        entityManager.persist(member);

        entityManager.flush();
    }


//    @Test
//    void sumPointDetailsIncrementByMemberId(){
//
//        PointDetails pointDetails1 = PointDetails.builder()
//                .pointDetailsId(1L)
//                .member(member)
//                .pointDetailsIncrement(10000)
//                .pointDetailsComment("10000 증가")
//                .pointDetailsDatetime(LocalDateTime.now())
//                .build();
//
//        PointDetails pointDetails2 = PointDetails.builder()
//                .pointDetailsId(2L)
//                .member(member)
//                .pointDetailsIncrement(-5000)
//                .pointDetailsComment("5000 감소")
//                .pointDetailsDatetime(LocalDateTime.now())
//                .build();
//
//        PointDetails pointDetails3 = PointDetails.builder()
//                .pointDetailsId(3L)
//                .member(member)
//                .pointDetailsIncrement(20000)
//                .pointDetailsComment("20000 증가")
//                .pointDetailsDatetime(LocalDateTime.now())
//                .build();
//
//        pointDetailsRepository.save(pointDetails1);
//        assertEquals(10000, pointDetailsRepository.sumPointDetailsIncrementByMemberId(member.getMemberId()));
//
//        pointDetailsRepository.save(pointDetails2);
//        assertEquals(5000, pointDetailsRepository.sumPointDetailsIncrementByMemberId(member.getMemberId()));
//
//        pointDetailsRepository.save(pointDetails3);
//        assertEquals(25000, pointDetailsRepository.sumPointDetailsIncrementByMemberId(member.getMemberId()));
//
//    }

    @Test
    void findAllByMemberMemberId(){

        PointDetails pointDetails4 = PointDetails.builder()
                .pointDetailsId(4L)
                .member(member)
                .pointDetailsIncrement(10000)
                .pointDetailsComment("10000 증가")
                .pointDetailsDatetime(LocalDateTime.now())
                .build();
        pointDetailsRepository.save(pointDetails4);
        Pageable pageable = PageRequest.of(0,3);
        Page<PointDetailsProjection> pointDetailsProjection = pointDetailsRepository.findAllByMemberMemberId(member.getMemberId(),pageable);
        assertEquals(pointDetails4.getPointDetailsId(),pointDetailsRepository.findAllByMemberMemberId(member.getMemberId(),pageable).getContent().getFirst().getPointDetailsId());
        assertEquals(pointDetails4.getPointDetailsIncrement(),pointDetailsRepository.findAllByMemberMemberId(member.getMemberId(),pageable).getContent().getFirst().getPointDetailsIncrement());
        assertEquals(pointDetails4.getPointDetailsComment(),pointDetailsRepository.findAllByMemberMemberId(member.getMemberId(),pageable).getContent().getFirst().getPointDetailsComment());
    }


}