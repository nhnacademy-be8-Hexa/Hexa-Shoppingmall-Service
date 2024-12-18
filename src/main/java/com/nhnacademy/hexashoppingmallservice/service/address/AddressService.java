package com.nhnacademy.hexashoppingmallservice.service.address;

import com.nhnacademy.hexashoppingmallservice.dto.address.AddressRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.address.Address;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.address.AddressNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import com.nhnacademy.hexashoppingmallservice.repository.address.AddressRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addAddress(AddressRequestDTO addressRequestDTO, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        Member member = memberRepository.findById(memberId).get();
        Address address = Address.of(
                addressRequestDTO.getAddressName(),
                addressRequestDTO.getZoneCode(),
                addressRequestDTO.getAddress(),
                addressRequestDTO.getAddressDetail(),
                member
        );
        addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public List<AddressProjection> getAddress(Pageable pageable, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return addressRepository.findAllByMemberMemberId(memberId, pageable).getContent();
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new AddressNotFoundException("Address %d not found".formatted(addressId));
        }
        addressRepository.deleteById(addressId);
    }
}
