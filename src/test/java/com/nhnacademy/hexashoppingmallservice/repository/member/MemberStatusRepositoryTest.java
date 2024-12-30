package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberStatusRepositoryTest {
    @Autowired
    private MemberStatusRepository memberStatusRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MemberStatus memberStatus;

    @BeforeEach
    void setUp() {
        memberStatus = new MemberStatus("활성");
        entityManager.persist(memberStatus);

        entityManager.flush();
    }

    @Test
    void save() {
        MemberStatus memberStatus1 = memberStatusRepository.save(memberStatus);
        assertNotNull(memberStatus1);
        assertEquals(memberStatus, memberStatus1);
    }

    @Test
    void findAll() {
        List<MemberStatus> memberStatuses = memberStatusRepository.findAll();
        assertNotNull(memberStatuses);
        assertEquals(1, memberStatuses.size());
    }

    @Test
    void findById() {
        Long id = memberStatus.getStatusId(); // 실제 생성된 ID를 사용
        MemberStatus memberStatus1 = memberStatusRepository.findById(id).orElse(null);
        assertNotNull(memberStatus1);
        assertEquals(memberStatus, memberStatus1);
    }

    @Test
    void deleteById() {
        memberStatusRepository.deleteById(1L);

    }
}