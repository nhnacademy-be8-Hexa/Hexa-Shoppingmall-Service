package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.MemberStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberStatusServiceTest {

    @Mock
    private MemberStatusRepository memberStatusRepository;

    @InjectMocks
    private MemberStatusService memberStatusService;

    @Test
    void createMemberStatus_success() {
        MemberStatus memberStatus = new MemberStatus("Active");
        when(memberStatusRepository.save(memberStatus)).thenReturn(memberStatus);

        MemberStatus result = memberStatusService.createMemberStatus(memberStatus);

        assertNotNull(result);
        assertEquals("Active", result.getStatusName());
        verify(memberStatusRepository).save(memberStatus);
    }

    @Test
    void getAllMemberStatus_success() {
        List<MemberStatus> memberStatusList = List.of(
                new MemberStatus("Active"),
                new MemberStatus("Inactive")
        );
        when(memberStatusRepository.findAll()).thenReturn(memberStatusList);

        List<MemberStatus> result = memberStatusService.getAllMemberStatus();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(memberStatusRepository).findAll();
    }

    @Test
    void getMemberStatus_success() {
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));

        MemberStatus result = memberStatusService.getMemberStatus(1L);

        assertNotNull(result);
        assertEquals("Active", result.getStatusName());
        verify(memberStatusRepository).findById(1L);
    }

    @Test
    void getMemberStatus_notFound() {
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.empty());

        MemberStatus result = memberStatusService.getMemberStatus(1L);

        assertNull(result);
        verify(memberStatusRepository).findById(1L);
    }

    @Test
    void deleteMemberStatus_success() {
        Long id = 1L;

        doNothing().when(memberStatusRepository).deleteById(id);

        memberStatusService.deleteMemberStatus(id);

        verify(memberStatusRepository).deleteById(id);
    }

    @Test
    void updateMemberStatus_success() {
        Long id = 1L;
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(id);
        MemberStatusRequestDTO requestDTO = new MemberStatusRequestDTO("Inactive");

        when(memberStatusRepository.findById(id)).thenReturn(Optional.of(memberStatus));

        MemberStatus result = memberStatusService.updateMemberStatus(id, requestDTO);

        assertNotNull(result);
        assertEquals("Inactive", result.getStatusName());
        verify(memberStatusRepository).findById(id);
    }

    @Test
    void updateMemberStatus_notFound() {
        Long id = 1L;
        MemberStatusRequestDTO requestDTO = new MemberStatusRequestDTO("Inactive");

        when(memberStatusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MemberStatusNotFoundException.class, () -> memberStatusService.updateMemberStatus(id, requestDTO));

        verify(memberStatusRepository).findById(id);
    }
}