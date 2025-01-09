package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsReasonRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsReasonNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsReasonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReturnsReasonServiceTest {

    @Mock
    private ReturnsReasonRepository returnsReasonRepository;

    @InjectMocks
    private ReturnsReasonService returnsReasonService;


    @Test
    void testCreateReturnsReason() {
        // Arrange
        String reasonDescription = "Damaged Item";
        ReturnsReasonRequestDTO requestDTO = mock(ReturnsReasonRequestDTO.class);
        when(requestDTO.getReturnsReason()).thenReturn(reasonDescription);

        ReturnsReason expectedReturnsReason = ReturnsReason.of(reasonDescription);
        when(returnsReasonRepository.save(any(ReturnsReason.class))).thenReturn(expectedReturnsReason);

        // Act
        ReturnsReason createdReason = returnsReasonService.createReturnsReason(requestDTO);

        // Assert
        verify(returnsReasonRepository, times(1)).save(any(ReturnsReason.class));
        assertThat(createdReason.getReturnsReason()).isEqualTo(reasonDescription);
    }

    @Test
    void testGetReturnsReasons() {
        // Arrange
        List<ReturnsReason> expectedReasons = List.of(
                ReturnsReason.of("Damaged Item"),
                ReturnsReason.of("Wrong Item")
        );
        when(returnsReasonRepository.findAll()).thenReturn(expectedReasons);

        // Act
        List<ReturnsReason> actualReasons = returnsReasonService.getReturnsReasons();

        // Assert
        verify(returnsReasonRepository, times(1)).findAll();
        assertThat(actualReasons).hasSize(expectedReasons.size());
        assertThat(actualReasons).containsAll(expectedReasons);
    }

    @Test
    void testGetReturnsReason() {
        // Arrange
        Long returnsReasonId = 1L;
        ReturnsReason expectedReason = ReturnsReason.of("Damaged Item");
        when(returnsReasonRepository.findById(returnsReasonId)).thenReturn(Optional.of(expectedReason));

        // Act
        ReturnsReason actualReason = returnsReasonService.getReturnsReason(returnsReasonId);

        // Assert
        verify(returnsReasonRepository, times(1)).findById(returnsReasonId);
        assertThat(actualReason).isEqualTo(expectedReason);
    }

    @Test
    void getReturnsReason_throwsException_whenReturnsReasonNotFound() {
        // Given
        ReturnsReasonRepository returnsReasonRepository = Mockito.mock(ReturnsReasonRepository.class);
        ReturnsReasonService returnsReasonService = new ReturnsReasonService(returnsReasonRepository, null); // null for second repository

        Long invalidReturnsReasonId = 999L;

        // Mocking behavior: empty optional returned from the repository
        when(returnsReasonRepository.findById(invalidReturnsReasonId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ReturnsReasonNotFoundException.class,
                () -> returnsReasonService.getReturnsReason(invalidReturnsReasonId));
    }

    @Test
    void testUpdateReturnsReason() {
        // Arrange
        Long returnsReasonId = 1L;
        String updatedReasonDescription = "Updated Reason";
        ReturnsReasonRequestDTO requestDTO = mock(ReturnsReasonRequestDTO.class);
        when(requestDTO.getReturnsReason()).thenReturn(updatedReasonDescription);

        ReturnsReason existingReturnsReason = ReturnsReason.of("Original Reason");
        when(returnsReasonRepository.findById(returnsReasonId)).thenReturn(Optional.of(existingReturnsReason));
        when(returnsReasonRepository.save(any(ReturnsReason.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReturnsReason updatedReason = returnsReasonService.updateReturnsReason(returnsReasonId, requestDTO);

        // Assert
        verify(returnsReasonRepository, times(1)).findById(returnsReasonId);
        verify(returnsReasonRepository, times(1)).save(existingReturnsReason);
        assertThat(updatedReason.getReturnsReason()).isEqualTo(updatedReasonDescription);
    }


    @Test
    void testDeleteReturnsReason() {
        // Arrange
        Long returnsReasonId = 1L;
        ReturnsReason existingReturnsReason = ReturnsReason.of("Damaged Item");
        when(returnsReasonRepository.findById(returnsReasonId)).thenReturn(Optional.of(existingReturnsReason));
        doNothing().when(returnsReasonRepository).deleteById(returnsReasonId);

        // Act
        returnsReasonService.deleteReturnsReason(returnsReasonId);

        // Assert
        verify(returnsReasonRepository, times(1)).findById(returnsReasonId);
        verify(returnsReasonRepository, times(1)).deleteById(returnsReasonId);
    }

    @Test
    void deleteReturnsReason_throwsException_whenReturnsReasonNotFound() {
        // Given
        ReturnsReasonRepository returnsReasonRepository = Mockito.mock(ReturnsReasonRepository.class);
        ReturnsReasonService returnsReasonService = new ReturnsReasonService(returnsReasonRepository, null); // null for second repository

        Long invalidReturnsReasonId = 999L;

        // Mocking behavior: empty optional returned from the repository
        when(returnsReasonRepository.findById(invalidReturnsReasonId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ReturnsReasonNotFoundException.class,
                () -> returnsReasonService.deleteReturnsReason(invalidReturnsReasonId));

        // Verify that deleteById is never called
        verify(returnsReasonRepository, never()).deleteById(invalidReturnsReasonId);
    }
}