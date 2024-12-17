package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
                "John Doe",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                null,
                "1",
                "1"
        );

        Rating rating = Rating.of("Gold", 10);
        rating.setRatingId(1L);
        MemberStatus memberStatus = MemberStatus.builder().statusName("Active").build();
        memberStatus.setStatusId(1L);

        when(ratingRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(memberStatusRepository.existsById(1L)).thenReturn(true);
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));
        when(memberRepository.existsById("123")).thenReturn(false);
        when(memberRepository.save(ArgumentMatchers.<Member>any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member member = memberService.createMember(requestDTO);

        assertNotNull(member);
        assertEquals("123", member.getMemberId());
        assertEquals(rating, member.getRating());
        assertEquals(memberStatus, member.getMemberStatus());
        verify(ratingRepository).existsById(1L);
        verify(ratingRepository).findById(1L);
        verify(memberStatusRepository).existsById(1L);
        verify(memberStatusRepository).findById(1L);
        verify(memberRepository).existsById("123");
        verify(memberRepository).save(ArgumentMatchers.<Member>any());
    }

    @Test
    void createMember_ratingNotFound() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John Doe",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                null,
                "99",
                "1"
        );

        when(ratingRepository.existsById(99L)).thenReturn(false);

        assertThrows(RatingNotFoundException.class, () -> memberService.createMember(requestDTO));
        verify(ratingRepository).existsById(99L);
        verifyNoInteractions(memberStatusRepository);
        verifyNoInteractions(memberRepository);
    }

    @Test
    void createMember_memberStatusNotFound() {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "123",
                "password",
                "John Doe",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                null,
                "1",
                "99"
        );

        when(ratingRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(Rating.of("Gold", 10)));
        when(memberStatusRepository.existsById(99L)).thenReturn(false);

        assertThrows(MemberStatusNotFoundException.class, () -> memberService.createMember(requestDTO));
        verify(ratingRepository).existsById(1L);
        verify(memberStatusRepository).existsById(99L);
        verifyNoInteractions(memberRepository);
    }

    @Test
    void updateMember_success() {
        String memberId = "123";
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                null,
                "newPassword",
                null,
                "0987654321",
                null,
                LocalDate.of(1990, 1, 1),
                LocalDateTime.now(),
                "2",
                "2"
        );

        Member member = Member.of(
                memberId,
                "password",
                "John Doe",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                Rating.of("Gold", 10),
                MemberStatus.builder().statusName("Active").build()
        );

        Rating newRating = Rating.of("Platinum", 20);
        newRating.setRatingId(2L);
        MemberStatus newStatus = MemberStatus.builder().statusName("Inactive").build();
        newStatus.setStatusId(2L);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(ratingRepository.findById(2L)).thenReturn(Optional.of(newRating));
        when(memberStatusRepository.findById(2L)).thenReturn(Optional.of(newStatus));

        Member updatedMember = memberService.updateMember(memberId, requestDTO);

        assertNotNull(updatedMember);
        assertEquals("newPassword", updatedMember.getMemberPassword());
        assertEquals("0987654321", updatedMember.getMemberNumber());
        assertEquals(newRating, updatedMember.getRating());
        assertEquals(newStatus, updatedMember.getMemberStatus());
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
    void searchMembersById_success() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchQuery = "123";

        MemberProjection projection = mock(MemberProjection.class);
        when(memberRepository.findByMemberIdContaining(searchQuery, pageable))
                .thenReturn(new PageImpl<>(List.of(projection)));

        List<MemberProjection> result = memberService.searchMembersById(pageable, searchQuery);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository).findByMemberIdContaining(searchQuery, pageable);
    }
}



