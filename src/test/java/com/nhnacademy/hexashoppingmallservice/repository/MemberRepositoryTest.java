package com.nhnacademy.hexashoppingmallservice.repository;

import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        memberStatus = new MemberStatus("활성");
        entityManager.persist(memberStatus);

        rating = new Rating("실버", 10);
        entityManager.persist(rating);

        member = new Member("test1", "password1234", "John Doe", "01012345678", LocalDate.of(1990, 5, 3), LocalDate.of(2024, 10, 10), LocalDateTime.now(), Role.MEMBER, rating, memberStatus);
        entityManager.persist(member);

        entityManager.flush();
    }

    @Test
    void findById() {
        Member member1 = memberRepository.findById("test1").get();

        assertNotNull(member1);
        assertEquals(member, member1);
    }

    @Test

}