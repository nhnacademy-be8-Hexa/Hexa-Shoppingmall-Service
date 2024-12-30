package com.nhnacademy.hexashoppingmallservice.repository.address;

import com.nhnacademy.hexashoppingmallservice.entity.address.Address;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MemberStatusRepository memberStatusRepository;

    @Test
    @DisplayName("findAllByMemberMemberId: 성공 - 회원이 존재하고 주소 목록을 반환함")
    void testFindAllByMemberMemberId_Success() {
        // Arrange
        // Rating 생성 및 저장
        Rating rating = Rating.of("Gold", 90);
        rating = ratingRepository.save(rating);

        // MemberStatus 생성 및 저장
        MemberStatus status = MemberStatus.of("Active");
        status = memberStatusRepository.save(status);

        // Member 생성 및 저장
        Member member = Member.of(
                "member123",
                "password123",
                "홍길동",
                "01012345678",
                "hong@example.com",
                LocalDate.of(1990, 1, 1),
                rating,
                status
        );
        member.setMemberCreatedAt(LocalDateTime.now());
        member.setMemberRole(Role.MEMBER);
        member = memberRepository.save(member);

        // Address 생성 및 저장
        Address address1 = Address.of(
                "집",
                "12345",
                "서울시 강남구",
                "서울시 강남구 역삼동 123-45",
                member
        );
        addressRepository.save(address1);

        Address address2 = Address.of(
                "회사",
                "67890",
                "서울시 서초구",
                "서울시 서초구 서초동 678-90",
                member
        );
        addressRepository.save(address2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("addressName").ascending());

        // Act
        Page<AddressProjection> addressPage = addressRepository.findAllByMemberMemberId("member123", pageable);
        List<AddressProjection> addressList = addressPage.getContent();

        // Assert
        assertThat(addressList).hasSize(2);
        AddressProjection resultAddress1 = addressList.get(0);
        AddressProjection resultAddress2 = addressList.get(1);

        // 첫 번째 주소 검증
        assertThat(resultAddress1.getAddressName()).isEqualTo("집");
        assertThat(resultAddress1.getZoneCode()).isEqualTo("12345");
        assertThat(resultAddress1.getAddress()).isEqualTo("서울시 강남구");
        assertThat(resultAddress1.getAddressDetail()).isEqualTo("서울시 강남구 역삼동 123-45");

        // 두 번째 주소 검증
        assertThat(resultAddress2.getAddressName()).isEqualTo("회사");
        assertThat(resultAddress2.getZoneCode()).isEqualTo("67890");
        assertThat(resultAddress2.getAddress()).isEqualTo("서울시 서초구");
        assertThat(resultAddress2.getAddressDetail()).isEqualTo("서울시 서초구 서초동 678-90");
    }

    @Test
    @DisplayName("findAllByMemberMemberId: 실패 - 회원이 존재하지 않음")
    void testFindAllByMemberMemberId_MemberNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("addressName").ascending());

        // Act
        Page<AddressProjection> addressPage = addressRepository.findAllByMemberMemberId("nonexistent_member", pageable);
        List<AddressProjection> addressList = addressPage.getContent();

        // Assert
        assertThat(addressList).isEmpty();
    }
}