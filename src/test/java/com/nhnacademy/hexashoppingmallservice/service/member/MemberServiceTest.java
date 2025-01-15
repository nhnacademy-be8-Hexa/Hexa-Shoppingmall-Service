package com.nhnacademy.hexashoppingmallservice.service.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.nhnacademy.hexashoppingmallservice.dto.book.MemberUpdateDTO;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.MemberDeletedException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        MemberUpdateDTO requestDTO = new MemberUpdateDTO(
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
        MemberUpdateDTO updateDTO = new MemberUpdateDTO();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId, updateDTO));
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

    // getMembers (검색어 없을 때)
    @Test
    void getMembers_withoutSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        MemberProjection projection = mock(MemberProjection.class);

        when(memberRepository.findAllBy(pageable))
                .thenReturn(new PageImpl<>(List.of(projection)));

        List<MemberProjection> result = memberService.getMembers(pageable, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository).findAllBy(pageable);
    }

    // getMember (회원 존재할 때)
    @Test
    void getMember_success() {
        String memberId = "123";
        Member member = mock(Member.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Member result = memberService.getMember(memberId);

        assertNotNull(result);
        assertEquals(member, result);
        verify(memberRepository).findById(memberId);
    }

    // getMember (회원이 존재하지 않을 때)
    @Test
    void getMember_notFound() {
        String memberId = "123";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(memberId));
        verify(memberRepository).findById(memberId);
    }

    // login (회원 존재하고 삭제되지 않은 상태일 때)
    @Test
    void login_success() {
        String memberId = "123";

        // Mock 객체 생성
        Member member = mock(Member.class);
        MemberStatus status = mock(MemberStatus.class);

        // Active 상태로 설정
        status.setStatusId(1L);

        // Member 객체에 status 설정 (이 부분은 mock에서는 동작하지 않으므로 getMemberStatus()가 status를 반환하도록 설정)
        when(member.getMemberStatus()).thenReturn(status);  // getMemberStatus가 status를 반환하도록 설정

        // memberRepository.findById()가 member를 반환하도록 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // status.getStatusId()가 1L을 반환하도록 설정
        when(status.getStatusId()).thenReturn(1L);

        // login 메서드 실행
        memberService.login(memberId);

        // verify
        verify(memberRepository).findById(memberId);
        verify(member).login();
    }

    // login (회원 존재하지만 삭제된 상태일 때)
    @Test
    void login_memberDeleted() {
        String memberId = "123";

        // Mock 객체 생성
        Member member = mock(Member.class);
        MemberStatus status = mock(MemberStatus.class);

        // Deleted 상태로 설정 (status.setStatusId(3L);)
        when(status.getStatusId()).thenReturn(3L); // Deleted 상태

        // Member 객체에 status 설정 (mock에서는 setMemberStatus가 동작하지 않으므로 getMemberStatus가 status를 반환하도록 설정)
        when(member.getMemberStatus()).thenReturn(status);  // getMemberStatus가 status를 반환하도록 설정

        // memberRepository.findById()가 member를 반환하도록 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // 로그인 시 Deleted 상태인 경우 MemberDeletedException을 던지는지 확인
        assertThrows(MemberDeletedException.class, () -> memberService.login(memberId));

        // 검증: login() 메서드가 호출되었는지 확인
        verify(memberRepository).findById(memberId);
        verify(member, never()).login();  // login()이 호출되지 않아야 함 (삭제된 회원이므로 로그인 시도 실패)
    }


    // saveAll (회원 목록 저장)
    @Test
    void saveAll_success() {
        List<Member> members = List.of(mock(Member.class), mock(Member.class));

        memberService.saveAll(members);

        verify(memberRepository).saveAll(members);
    }

    // countBySearch (검색어 있을 때)
    @Test
    void countBySearch_withSearch() {
        String searchQuery = "123";

        when(memberRepository.countByMemberIdContaining(searchQuery)).thenReturn(5L);

        long result = memberService.countBySearch(searchQuery);

        assertEquals(5L, result);
        verify(memberRepository).countByMemberIdContaining(searchQuery);
    }

    // countBySearch (검색어 없을 때)
    @Test
    void countBySearch_withoutSearch() {
        when(memberRepository.count()).thenReturn(10L);

        long result = memberService.countBySearch(null);

        assertEquals(10L, result);
        verify(memberRepository).count();
    }

}



