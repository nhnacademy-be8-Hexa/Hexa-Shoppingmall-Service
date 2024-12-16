package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberStatusRepository memberStatusRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void createMember_success() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 12, 1),
                LocalDateTime.now(),
                "MEMBER",
                "1",
                "1"
        );
        Rating rating = new Rating("Gold", 10);
        rating.setRatingId(1L);
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));
        when(memberRepository.findById("123")).thenReturn(Optional.empty());
        when(memberRepository.save(ArgumentMatchers.<Member>any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member member = memberService.createMember(requestDTO);

        assertNotNull(member);
        assertEquals("123", member.getMemberId());
        assertEquals(rating, member.getRating());
        assertEquals(memberStatus, member.getMemberStatus());
        verify(ratingRepository).findById(1L);
        verify(memberStatusRepository).findById(1L);
        verify(memberRepository).save(ArgumentMatchers.<Member>any());
    }

    @Test
    void createMember_ratingNotFound() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 12, 1),
                LocalDateTime.now(),
                "MEMBER",
                "99",
                "1"
        );

        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RatingNotFoundException.class, () -> memberService.createMember(requestDTO));
        verify(ratingRepository).findById(99L);
        verifyNoInteractions(memberStatusRepository);
        verifyNoInteractions(memberRepository);
    }

    @Test
    void createMember_memberStatusNotFound() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 12, 1),
                LocalDateTime.now(),
                "MEMBER",
                "1",
                "99"
        );

        Rating rating = new Rating("Gold", 10);
        rating.setRatingId(1L);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(memberStatusRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MemberStatusNotFoundException.class, () -> memberService.createMember(requestDTO));
        verify(ratingRepository).findById(1L);
        verify(memberStatusRepository).findById(99L);
        verifyNoInteractions(memberRepository);
    }

    @Test
    void createMember_memberAlreadyExists() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 12, 1),
                LocalDateTime.now(),
                "MEMBER",
                "1",
                "1"
        );

        Rating rating = new Rating("Gold", 10);
        rating.setRatingId(1L);

        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));
        when(memberRepository.findById("123")).thenReturn(Optional.of(new Member()));

        assertThrows(MemberAlreadyExistException.class, () -> memberService.createMember(requestDTO));

        verify(ratingRepository).findById(1L);
        verify(memberStatusRepository).findById(1L);
        verify(memberRepository).findById("123");
    }

    @Test
    void updateMember_success() {
        String memberId = "123";
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "newPassword",
                "John Updated",
                "0987654321",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2023, 12, 1),
                LocalDateTime.now(),
                "ADMIN",
                "2",
                "2"
        );
        Member member = new Member();
        member.setMemberId(memberId);
        Rating newRating = new Rating("Platinum", 20);
        newRating.setRatingId(2L);
        MemberStatus newStatus = new MemberStatus("Inactive");
        newStatus.setStatusId(2L);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(ratingRepository.findById(2L)).thenReturn(Optional.of(newRating));
        when(memberStatusRepository.findById(2L)).thenReturn(Optional.of(newStatus));

        Member result = memberService.updateMember(memberId, requestDTO);

        assertEquals("newPassword", result.getMemberPassword());
        assertEquals("John Updated", result.getMemberName());
        assertEquals("0987654321", result.getMemberNumber());
        assertEquals(newRating, result.getRating());
        assertEquals(newStatus, result.getMemberStatus());
        verify(memberRepository).findById(memberId);
        verify(ratingRepository).findById(2L);
        verify(memberStatusRepository).findById(2L);
    }

    @Test
    void updateMember_notFound() {
        String memberId = "123";
        MemberRequestDTO requestDTO = new MemberRequestDTO();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId, requestDTO));
        verify(memberRepository).findById(memberId);
    }

    @Test
    void findMembers_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> members = new PageImpl<>(List.of(new Member()));

        when(memberRepository.findAll(pageable)).thenReturn(members);

        Page<Member> result = memberService.findMembers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(memberRepository).findAll(pageable);
    }

    @Test
    void findMemberById_success() {
        String memberId = "123";
        Member member = new Member();
        member.setMemberId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Optional<Member> result = memberService.findMemberById(memberId);

        assertTrue(result.isPresent());
        assertEquals(memberId, result.get().getMemberId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    void findMembersById_success() {
        Pageable pageable = PageRequest.of(0, 10);
        String memberId = "123";
        Page<Member> members = new PageImpl<>(List.of(new Member()));

        when(memberRepository.findByMemberIdContaining(memberId, pageable)).thenReturn(members);

        Page<Member> result = memberService.findMembersById(pageable, memberId);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(memberRepository).findByMemberIdContaining(memberId, pageable);
    }
}

