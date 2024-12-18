package com.nhnacademy.hexashoppingmallservice.repository.address;

import com.nhnacademy.hexashoppingmallservice.entity.address.Address;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<AddressProjection> findAllByMemberMemberId(String memberId, Pageable pageable);
}
