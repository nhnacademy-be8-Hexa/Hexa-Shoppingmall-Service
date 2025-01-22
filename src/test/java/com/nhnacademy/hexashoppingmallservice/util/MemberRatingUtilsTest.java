package com.nhnacademy.hexashoppingmallservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberRatingUtilsTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private MemberRatingUtils memberRatingUtils;

    private Rating royal;
    private Rating gold;
    private Rating platinum;

    private Member member;
    private MemberOrderSummary3M memberOrderSummary3M;
    private MemberStatus activeStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Use the updated Rating.of() method to create Rating objects
        royal = Rating.of("Royal", 10);  // 예시로 ratingPercent 10%로 설정
        gold = Rating.of("Gold", 15);    // 예시로 ratingPercent 15%로 설정
        platinum = Rating.of("Platinum", 20); // 예시로 ratingPercent 20%로 설정

        // Mock RatingRepository behavior
        when(ratingRepository.findByRatingName("Royal")).thenReturn(royal);
        when(ratingRepository.findByRatingName("Gold")).thenReturn(gold);
        when(ratingRepository.findByRatingName("Platinum")).thenReturn(platinum);

        // Mock MemberStatus
        activeStatus = new MemberStatus("Active");

        // Create a new Member using the factory method
        member = Member.of("member123", "password", "John Doe", "01012345678", "john@example.com",
                LocalDate.of(1990, 1, 1), royal, activeStatus);

        // Use the `of()` method to create MemberOrderSummary3M
        memberOrderSummary3M = MemberOrderSummary3M.of("member123", 0);  // 초기값 설정
    }


    @Test
    void testRefreshRating_Normal() {
        // Set the total order price to be lower than ROYAL_AMOUNT
        memberOrderSummary3M.setTotalOrderPrice(50000);

        // Call the method under test
        member = memberRatingUtils.refreshRating(member, memberOrderSummary3M);

        // Assert that the member's rating is not set to Royal, Gold, or Platinum
        assertEquals(royal, member.getRating());  // 기본값을 Royal로 설정했으므로
    }


}
