package com.nhnacademy.hexashoppingmallservice.service.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.CreatePointDetailDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl.PointDetailsRepositoryCustomImpl;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.PointDetailsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PointDetailsServiceTest {

    @Mock
    PointDetailsRepository pointDetailsRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointDetailsRepositoryCustomImpl pointDetailsRepositoryCustom;

    @InjectMocks
    PointDetailsService pointDetailsService;


    private MemberStatus memberStatus;
    private Rating rating;
    private Member member;

    @BeforeEach
    void setUp() {
        // Create and persist MemberStatus
        memberStatus = MemberStatus.builder()
                .statusName("활성")
                .build();


        // Create and persist Rating
        rating = Rating.builder()
                .ratingName("실버")
                .ratingPercent(10)
                .build();


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

    }

    @Test
    void createPointDetails_success(){



        PointDetails pointDetailsOutput = PointDetails.builder()
                .pointDetailsId(1L)
                .member(member)
                .pointDetailsIncrement(10000)
                .pointDetailsComment("10000 증가")
                .pointDetailsDatetime(LocalDateTime.now())
                .build();

        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.TRUE);
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.ofNullable(member));
        when(pointDetailsRepository.save(ArgumentMatchers.<PointDetails>any())).thenReturn(pointDetailsOutput);

        CreatePointDetailDTO createPointDetailDTO = new CreatePointDetailDTO(10000,"10000 증가");
        PointDetails pointDetails = pointDetailsService.createPointDetails(createPointDetailDTO,member.getMemberId());



        assertEquals(pointDetailsOutput.getPointDetailsId(),pointDetails.getPointDetailsId());
        assertEquals(pointDetailsOutput.getMember(),pointDetails.getMember());
        assertEquals(createPointDetailDTO.getPointDetailsComment(),pointDetails.getPointDetailsComment());
        assertEquals(createPointDetailDTO.getPointDetailsIncrement(),pointDetails.getPointDetailsIncrement());
        assertEquals(pointDetailsOutput.getPointDetailsDatetime(),pointDetails.getPointDetailsDatetime());
    }


    @Test
    void createPointDetails_fail_existsById(){
        CreatePointDetailDTO createPointDetailDTO = new CreatePointDetailDTO(10000,"10000 증가");
        PointDetails pointDetailsInput = PointDetails.builder()
                .pointDetailsId(2L)
                .member(null)
                .pointDetailsIncrement(10000)
                .pointDetailsComment("10000 증가")
                .pointDetailsDatetime(null)
                .build();
        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.FALSE);
        Assertions.assertThrows(MemberNotFoundException.class,()->pointDetailsService.createPointDetails(createPointDetailDTO,member.getMemberId()));
    }

    @Test
    void sumPoint_success(){

        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.TRUE);
        when(pointDetailsRepositoryCustom.sumPointDetailsIncrementByMemberId(member.getMemberId())).thenReturn(20000L);


        assertEquals(20000L, pointDetailsService.sumPoint(member.getMemberId()));

    }

    @Test
    void sumPoint_fail_existsById(){
        PointDetails pointDetailsInput = PointDetails.builder()
                .pointDetailsId(2L)
                .member(null)
                .pointDetailsIncrement(10000)
                .pointDetailsComment("10000 증가")
                .pointDetailsDatetime(null)
                .build();
        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.FALSE);
        Assertions.assertThrows(MemberNotFoundException.class,()->pointDetailsService.sumPoint(member.getMemberId()));
    }

    @Test
    void getPointDetails_success(){

        Pageable pageable = PageRequest.of(0, 10);
        List<PointDetailsProjection> mockProjections = Arrays.asList(
                (PointDetailsProjection) mock(PointDetailsProjection.class),
                (PointDetailsProjection) mock(PointDetailsProjection.class)
        );

        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.TRUE);
        when(pointDetailsRepository.findAllByMemberMemberId(member.getMemberId(), pageable)).thenReturn(new PageImpl<>(mockProjections));

        List<PointDetailsProjection> result = pointDetailsService.getPointDetails(pageable, member.getMemberId());
        assertNotNull(result);
        assertEquals(2, result.size()); // mockProjections에 2개 있는 것을 확인
        verify(memberRepository, times(1)).existsById(member.getMemberId());
        verify(pointDetailsRepository, times(1)).findAllByMemberMemberId(member.getMemberId(), pageable);




    }

    @Test
    void getPointDetails_fail_existsById(){
        when(memberRepository.existsById(member.getMemberId())).thenReturn(Boolean.FALSE);
        Assertions.assertThrows(MemberNotFoundException.class,()->pointDetailsService.getPointDetails(PageRequest.of(0,3),member.getMemberId()));

    }

}