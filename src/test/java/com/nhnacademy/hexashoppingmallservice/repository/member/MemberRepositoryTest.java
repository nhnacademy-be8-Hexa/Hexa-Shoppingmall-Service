package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

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

    @Test
    void findById() {
        // Test the repository's findById method
        Optional<Member> foundMember = memberRepository.findById("test1");

        assertTrue(foundMember.isPresent());
        assertEquals(member.getMemberId(), foundMember.get().getMemberId());
        assertEquals(member.getMemberName(), foundMember.get().getMemberName());
        assertEquals(member.getMemberEmail(), foundMember.get().getMemberEmail());
        assertEquals(member.getRating().getRatingName(), foundMember.get().getRating().getRatingName());
        assertEquals(member.getMemberStatus().getStatusName(), foundMember.get().getMemberStatus().getStatusName());
    }

    @Test
    void saveMember() {
        // Create a new Member
        Member newMember = Member.of(
                "test2",
                "securePassword5678",
                "Jane Doe",
                "01098765432",
                "jane.doe@test.com",
                LocalDate.of(1985, 7, 15),
                rating,
                memberStatus
        );

        // Save and retrieve the new member
        memberRepository.save(newMember);
        Optional<Member> foundMember = memberRepository.findById("test2");

        assertTrue(foundMember.isPresent());
        assertEquals(newMember.getMemberId(), foundMember.get().getMemberId());
        assertEquals(newMember.getMemberName(), foundMember.get().getMemberName());
    }

    @Test
    void findByNonExistentId() {
        Optional<Member> foundMember = memberRepository.findById("nonExistent");

        assertFalse(foundMember.isPresent());
    }
}
