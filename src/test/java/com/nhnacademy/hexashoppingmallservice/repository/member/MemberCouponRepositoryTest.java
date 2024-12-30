package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.*;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberCouponRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MemberStatusRepository memberStatusRepository;

    private Rating rating;
    private MemberStatus memberStatus;
    private Member member;

    @BeforeEach
    void setUp() {
        // Rating 생성 및 영속화
        rating = Rating.builder()
                .ratingName("Gold")
                .ratingPercent(10)
                .build();
        entityManager.persist(rating);

        // MemberStatus 생성 및 영속화
        memberStatus = MemberStatus.builder()
                .statusName("Active")
                .build();
        entityManager.persist(memberStatus);

        // Member 생성 및 영속화
        member = Member.builder()
                .memberId("member123")
                .memberPassword("password123")
                .memberName("홍길동")
                .memberNumber("01012345678")
                .memberEmail("hong@example.com")
                .memberBirthAt(LocalDate.of(1990, 1, 1))
                .memberCreatedAt(LocalDateTime.now())
                .memberLastLoginAt(LocalDateTime.now())
                .memberRole(Role.MEMBER)
                .rating(rating)
                .memberStatus(memberStatus)
                .build();
        entityManager.persist(member);

        // Flush to ensure data is written to the database before tests
        entityManager.flush();
    }

    @Test
    @DisplayName("findByMemberMemberId_Success - 특정 회원의 쿠폰 목록을 성공적으로 조회")
    void findByMemberMemberId_Success() {
        // Arrange
        // 쿠폰 생성 및 영속화
        MemberCoupon coupon1 = MemberCoupon.of(100L, member);
        MemberCoupon coupon2 = MemberCoupon.of(101L, member);
        entityManager.persist(coupon1);
        entityManager.persist(coupon2);
        entityManager.flush();

        // Act
        List<MemberCouponProjection> coupons = memberCouponRepository.findByMemberMemberId("member123");

        // Assert
        assertThat(coupons).hasSize(2);
        assertThat(coupons).extracting(MemberCouponProjection::getCouponId)
                .containsExactlyInAnyOrder(100L, 101L);
    }

    @Test
    @DisplayName("findByMemberMemberId_NoCoupons - 특정 회원에게 쿠폰이 없는 경우 빈 리스트 반환")
    void findByMemberMemberId_NoCoupons() {
        // Arrange
        // 새로운 회원 생성 및 영속화
        Member memberNoCoupons = Member.builder()
                .memberId("member456")
                .memberPassword("password456")
                .memberName("이순신")
                .memberNumber("01087654321")
                .memberEmail("lee@example.com")
                .memberBirthAt(LocalDate.of(1985, 5, 15))
                .memberCreatedAt(LocalDateTime.now())
                .memberLastLoginAt(LocalDateTime.now())
                .memberRole(Role.MEMBER)
                .rating(rating)
                .memberStatus(memberStatus)
                .build();
        entityManager.persist(memberNoCoupons);
        entityManager.flush();

        // Act
        List<MemberCouponProjection> coupons = memberCouponRepository.findByMemberMemberId("member456");

        // Assert
        assertThat(coupons).isEmpty();
    }

    @Test
    @DisplayName("findByMemberMemberId_MemberNotFound - 존재하지 않는 회원 ID로 조회 시 빈 리스트 반환")
    void findByMemberMemberId_MemberNotFound() {
        // Arrange
        // 아무 회원도 추가로 저장하지 않음

        // Act
        List<MemberCouponProjection> coupons = memberCouponRepository.findByMemberMemberId("nonexistent");

        // Assert
        assertThat(coupons).isEmpty();
    }

    @Test
    @DisplayName("save_MemberCoupon_Success - 회원 쿠폰을 성공적으로 저장")
    void save_MemberCoupon_Success() {
        // Arrange
        // 새로운 쿠폰 생성
        MemberCoupon coupon = MemberCoupon.of(102L, member);
        entityManager.persist(coupon);
        entityManager.flush();

        // Act
        List<MemberCouponProjection> coupons = memberCouponRepository.findByMemberMemberId("member123");

        // Assert
        assertThat(coupons).hasSize(1);
        assertThat(coupons.get(0).getCouponId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("deleteById_Success - 회원 쿠폰을 성공적으로 삭제")
    void deleteById_Success() {
        // Arrange
        // 쿠폰 생성 및 영속화
        MemberCoupon coupon = MemberCoupon.of(103L, member);
        entityManager.persist(coupon);
        entityManager.flush();

        // Act
        memberCouponRepository.deleteById(coupon.getMemberCouponId());
        entityManager.flush(); // Force deletion to be executed

        // Assert
        List<MemberCouponProjection> coupons = memberCouponRepository.findByMemberMemberId("member123");
        assertThat(coupons).extracting(MemberCouponProjection::getMemberCouponId)
                .doesNotContain(coupon.getMemberCouponId());
        assertThat(coupons).extracting(MemberCouponProjection::getCouponId)
                .doesNotContain(coupon.getCouponId());
    }
}
