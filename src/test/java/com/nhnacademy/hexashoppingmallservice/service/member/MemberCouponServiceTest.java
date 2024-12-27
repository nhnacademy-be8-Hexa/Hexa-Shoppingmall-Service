package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.*;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberCouponRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCouponServiceTest {

    @Mock
    private MemberCouponRepository memberCouponRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberCouponService memberCouponService;

    private Member member;

    @BeforeEach
    void setUp() {
        // Mock Member 객체 생성
        Rating rating = Rating.builder()
                .ratingId(1L)
                .ratingName("Gold")
                .ratingPercent(10)
                .build();

        MemberStatus memberStatus = MemberStatus.builder()
                .statusId(1L)
                .statusName("Active")
                .build();

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
    }

    @Test
    @DisplayName("getMemberCoupon_Success - 특정 회원의 쿠폰 목록을 성공적으로 반환")
    void getMemberCoupon_Success() {
        // Arrange
        String memberId = "member123";
        MemberCouponProjection coupon1 = mock(MemberCouponProjection.class);
        when(coupon1.getMemberCouponId()).thenReturn(1L);
        when(coupon1.getCouponId()).thenReturn(100L);

        MemberCouponProjection coupon2 = mock(MemberCouponProjection.class);
        when(coupon2.getMemberCouponId()).thenReturn(2L);
        when(coupon2.getCouponId()).thenReturn(101L);

        List<MemberCouponProjection> mockCoupons = Arrays.asList(coupon1, coupon2);

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberCouponRepository.findByMemberMemberId(memberId)).thenReturn(mockCoupons);

        // Act
        List<MemberCouponProjection> result = memberCouponService.getMemberCoupon(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getMemberCouponId());
        assertEquals(100L, result.get(0).getCouponId());
        assertEquals(2L, result.get(1).getMemberCouponId());
        assertEquals(101L, result.get(1).getCouponId());

        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberCouponRepository, times(1)).findByMemberMemberId(memberId);
    }

    @Test
    @DisplayName("getMemberCoupon_MemberNotFound - 존재하지 않는 회원 ID로 조회 시 예외 발생")
    void getMemberCoupon_MemberNotFound() {
        // Arrange
        String memberId = "nonexistent";
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberCouponService.getMemberCoupon(memberId);
        });

        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberCouponRepository, never()).findByMemberMemberId(anyString());
    }

    @Test
    @DisplayName("createMemberCoupon_Success - 특정 회원에게 쿠폰을 성공적으로 생성")
    void createMemberCoupon_Success() {
        // Arrange
        String memberId = "member123";
        Long couponId = 200L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        MemberCoupon savedCoupon = MemberCoupon.of(couponId, member);
        savedCoupon.setMemberCouponId(3L);

        when(memberCouponRepository.save(any(MemberCoupon.class))).thenReturn(savedCoupon);

        // Act
        memberCouponService.createMemberCoupon(couponId, memberId);

        // Assert
        ArgumentCaptor<MemberCoupon> couponCaptor = ArgumentCaptor.forClass(MemberCoupon.class);
        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberCouponRepository, times(1)).save(couponCaptor.capture());

        MemberCoupon capturedCoupon = couponCaptor.getValue();
        assertNotNull(capturedCoupon);
        assertEquals(couponId, capturedCoupon.getCouponId());
        assertEquals(member, capturedCoupon.getMember());
    }

    @Test
    @DisplayName("createMemberCoupon_MemberNotFound - 존재하지 않는 회원에게 쿠폰 생성 시 예외 발생")
    void createMemberCoupon_MemberNotFound() {
        // Arrange
        String memberId = "nonexistent";
        Long couponId = 200L;

        when(memberRepository.existsById(memberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberCouponService.createMemberCoupon(couponId, memberId);
        });


        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberRepository, never()).findById(anyString());
        verify(memberCouponRepository, never()).save(any(MemberCoupon.class));
    }

    @Test
    @DisplayName("deleteMemberCoupon_Success - 특정 회원의 쿠폰을 성공적으로 삭제")
    void deleteMemberCoupon_Success() {
        // Arrange
        String memberId = "member123";
        Long couponId = 200L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberCouponRepository.existsById(couponId)).thenReturn(true);

        MemberCoupon memberCoupon = MemberCoupon.of(couponId, member);
        memberCoupon.setMemberCouponId(couponId); // Assuming memberCouponId == couponId
        when(memberCouponRepository.findById(couponId)).thenReturn(Optional.of(memberCoupon));

        // Act
        memberCouponService.deleteMemberCoupon(couponId, memberId);

        // Assert
        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberCouponRepository, times(1)).existsById(couponId);
        verify(memberCouponRepository, times(1)).findById(couponId);
        verify(memberCouponRepository, times(1)).delete(memberCoupon);
    }

    @Test
    @DisplayName("deleteMemberCoupon_MemberNotFound - 존재하지 않는 회원의 쿠폰 삭제 시 예외 발생")
    void deleteMemberCoupon_MemberNotFound() {
        // Arrange
        String memberId = "nonexistent";
        Long couponId = 200L;

        when(memberRepository.existsById(memberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberCouponService.deleteMemberCoupon(couponId, memberId);
        });

        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberCouponRepository, never()).existsById(anyLong());
        verify(memberCouponRepository, never()).findById(anyLong());
        verify(memberCouponRepository, never()).delete(any(MemberCoupon.class));
    }

    @Test
    @DisplayName("deleteMemberCoupon_CouponNotFound - 존재하지 않는 쿠폰 삭제 시 예외 발생")
    void deleteMemberCoupon_CouponNotFound() {
        // Arrange
        String memberId = "member123";
        Long couponId = 200L;

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberCouponRepository.existsById(couponId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberCouponService.deleteMemberCoupon(couponId, memberId);
        });

        assertEquals("Coupon 200 is not found", exception.getMessage());

        verify(memberRepository, times(1)).existsById(memberId);
        verify(memberCouponRepository, times(1)).existsById(couponId);
        verify(memberCouponRepository, never()).findById(anyLong());
        verify(memberCouponRepository, never()).delete(any(MemberCoupon.class));
    }

}