package com.nhnacademy.hexashoppingmallservice.service.address;

import com.nhnacademy.hexashoppingmallservice.dto.address.AddressRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.address.Address;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;
import com.nhnacademy.hexashoppingmallservice.exception.address.AddressNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import com.nhnacademy.hexashoppingmallservice.repository.address.AddressRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("addAddress: 성공 - 회원이 존재하고 주소가 저장됨")
    void testAddAddress_Success() {
        // Arrange
        String memberId = "member123";
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setAddressName("집");
        addressRequestDTO.setZoneCode("12345");
        addressRequestDTO.setAddress("서울시 강남구");
        addressRequestDTO.setAddressDetail("서울시 강남구 역삼동 123-45");

        Rating rating = Rating.of("Gold", 90); // Rating 엔티티 인스턴스 생성
        rating.setRatingId(1L); // ID 설정 (필요 시)

        MemberStatus status = MemberStatus.of("Active"); // MemberStatus 엔티티 인스턴스 생성
        status.setStatusId(1L); // ID 설정 (필요 시)

        Member member = Member.of(
                memberId,
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

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        addressService.addAddress(addressRequestDTO, memberId);

        // Assert
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());

        Address savedAddress = addressCaptor.getValue();
        assertEquals("집", savedAddress.getAddressName());
        assertEquals("12345", savedAddress.getZoneCode());
        assertEquals("서울시 강남구", savedAddress.getAddress());
        assertEquals("서울시 강남구 역삼동 123-45", savedAddress.getAddressDetail());
        assertEquals(member, savedAddress.getMember());
    }

    @Test
    @DisplayName("addAddress: 실패 - 회원이 존재하지 않음")
    void testAddAddress_MemberNotFound() {
        // Arrange
        String memberId = "nonexistent_member";
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setAddressName("집");
        addressRequestDTO.setZoneCode("12345");
        addressRequestDTO.setAddress("서울시 강남구");
        addressRequestDTO.setAddressDetail("서울시 강남구 역삼동 123-45");

        when(memberRepository.existsById(memberId)).thenReturn(false);

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            addressService.addAddress(addressRequestDTO, memberId);
        });
    }

    @Nested
    @DisplayName("getAddress 메서드 테스트")
    class GetAddressTests {

        @Test
        @DisplayName("getAddress: 성공 - 회원이 존재하고 주소 목록을 반환함")
        void testGetAddress_Success() {
            // Arrange
            String memberId = "member123";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("addressName").ascending());

            AddressProjection address1 = mock(AddressProjection.class);
            when(address1.getAddressId()).thenReturn("addr1");
            when(address1.getAddressName()).thenReturn("집");
            when(address1.getZoneCode()).thenReturn("12345");
            when(address1.getAddress()).thenReturn("서울시 강남구");
            when(address1.getAddressDetail()).thenReturn("서울시 강남구 역삼동 123-45");

            AddressProjection address2 = mock(AddressProjection.class);
            when(address2.getAddressId()).thenReturn("addr2");
            when(address2.getAddressName()).thenReturn("회사");
            when(address2.getZoneCode()).thenReturn("67890");
            when(address2.getAddress()).thenReturn("서울시 서초구");
            when(address2.getAddressDetail()).thenReturn("서울시 서초구 서초동 678-90");

            List<AddressProjection> addressList = Arrays.asList(address1, address2);
            Page<AddressProjection> addressPage = new PageImpl<>(addressList, pageable, addressList.size());

            when(memberRepository.existsById(memberId)).thenReturn(true);
            when(addressRepository.findAllByMemberMemberId(memberId, pageable)).thenReturn(addressPage);

            // Act
            List<AddressProjection> result = addressService.getAddress(pageable, memberId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            // 첫 번째 주소 검증
            AddressProjection resultAddress1 = result.get(0);
            assertEquals("addr1", resultAddress1.getAddressId());
            assertEquals("집", resultAddress1.getAddressName());
            assertEquals("12345", resultAddress1.getZoneCode());
            assertEquals("서울시 강남구", resultAddress1.getAddress());
            assertEquals("서울시 강남구 역삼동 123-45", resultAddress1.getAddressDetail());

            // 두 번째 주소 검증
            AddressProjection resultAddress2 = result.get(1);
            assertEquals("addr2", resultAddress2.getAddressId());
            assertEquals("회사", resultAddress2.getAddressName());
            assertEquals("67890", resultAddress2.getZoneCode());
            assertEquals("서울시 서초구", resultAddress2.getAddress());
            assertEquals("서울시 서초구 서초동 678-90", resultAddress2.getAddressDetail());

            verify(addressRepository, times(1)).findAllByMemberMemberId(memberId, pageable);
        }

        @Test
        @DisplayName("getAddress: 실패 - 회원이 존재하지 않음")
        void testGetAddress_MemberNotFound() {
            // Arrange
            String memberId = "nonexistent_member";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("addressName").ascending());

            when(memberRepository.existsById(memberId)).thenReturn(false);

            // Act & Assert
            MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
                addressService.getAddress(pageable, memberId);
            });
        }
    }

    @Nested
    @DisplayName("deleteAddress 메서드 테스트")
    class DeleteAddressTests {

        @Test
        @DisplayName("deleteAddress: 성공 - 주소가 존재하고 삭제됨")
        void testDeleteAddress_Success() {
            // Arrange
            Long addressId = 1L;

            when(addressRepository.existsById(addressId)).thenReturn(true);
            doNothing().when(addressRepository).deleteById(addressId);

            // Act
            addressService.deleteAddress(addressId);

            // Assert
            verify(addressRepository, times(1)).existsById(addressId);
            verify(addressRepository, times(1)).deleteById(addressId);
        }

        @Test
        @DisplayName("deleteAddress: 실패 - 주소가 존재하지 않음")
        void testDeleteAddress_AddressNotFound() {
            // Arrange
            Long addressId = 999L;

            when(addressRepository.existsById(addressId)).thenReturn(false);

            // Act & Assert
            AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
                addressService.deleteAddress(addressId);
            });

            assertEquals("Address 999 not found", exception.getMessage());
            verify(addressRepository, times(1)).existsById(addressId);
            verify(addressRepository, never()).deleteById(anyLong());
        }
    }
}