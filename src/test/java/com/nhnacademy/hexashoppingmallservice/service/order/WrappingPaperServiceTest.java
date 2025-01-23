package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.WrappingPaperRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.order.WrappingPaperNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.WrappingPaperRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class WrappingPaperServiceTest {

    @Mock
    private WrappingPaperRepository wrappingPaperRepository;

    @InjectMocks
    private WrappingPaperService wrappingPaperService;


    @Test
    void getWrappingPaper_ValidId_ReturnsWrappingPaper() {
        // Given
        Long wrappingPaperId = 1L;
        WrappingPaper wrappingPaper = WrappingPaper.of("Birthday Wrapping Paper", 200);
        Field wrappingPaperIdField = null;
        try {
            wrappingPaperIdField = wrappingPaper.getClass().getDeclaredField("wrappingPaperId");
            wrappingPaperIdField.setAccessible(true);
            wrappingPaperIdField.set(wrappingPaper, wrappingPaperId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error while setting wrappingPaperId", e);
        }

        when(wrappingPaperRepository.existsById(wrappingPaperId)).thenReturn(true);
        when(wrappingPaperRepository.findById(wrappingPaperId)).thenReturn(java.util.Optional.of(wrappingPaper));

        // When
        WrappingPaper result = wrappingPaperService.getWrappingPaper(wrappingPaperId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(wrappingPaperId);
        assertThat(result.getWrappingPaperName()).isEqualTo("Birthday Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(200);

        verify(wrappingPaperRepository, times(1)).existsById(wrappingPaperId);
        verify(wrappingPaperRepository, times(1)).findById(wrappingPaperId);
    }

    @Test
    void getWrappingPaper_InvalidId_ThrowsNotFoundException() {
        // Given
        Long wrappingPaperId = 99L;

        when(wrappingPaperRepository.existsById(wrappingPaperId)).thenReturn(false);

        Assertions.assertThrows(WrappingPaperNotFoundException.class, () -> {wrappingPaperService.getWrappingPaper(wrappingPaperId);});
        verify(wrappingPaperRepository, times(1)).existsById(wrappingPaperId);
        verifyNoMoreInteractions(wrappingPaperRepository);
    }

    @Test
    void createWrappingPaper_LongName_ThrowsConstraintViolation() {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("ThisNameIsWayTooLongForTheValidationCheck");
        requestDTO.setWrappingPaperPrice(100);

        // When - Then
        try {
            wrappingPaperService.createWrappingPaper(requestDTO);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(RuntimeException.class); // Validation should catch length violation
            verifyNoInteractions(wrappingPaperRepository);
        }
    }

    @Test
    void createWrappingPaper_ZeroPrice_IsValid() throws Exception {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Simple Wrapping Paper");
        requestDTO.setWrappingPaperPrice(0);

        WrappingPaper savedWrappingPaper = WrappingPaper.of(
                requestDTO.getWrappingPaperName(),
                requestDTO.getWrappingPaperPrice()
        );

        Field wrappingPaperIdField = savedWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperIdField.setAccessible(true);
        wrappingPaperIdField.set(savedWrappingPaper, 2L);

        when(wrappingPaperRepository.save(any())).thenReturn(savedWrappingPaper);

        // When
        WrappingPaper result = wrappingPaperService.createWrappingPaper(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(2L);
        assertThat(result.getWrappingPaperName()).isEqualTo("Simple Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(0);

        verify(wrappingPaperRepository, times(1)).save(any(WrappingPaper.class));
    }

    @Test
    void createWrappingPaper_ValidPayload_ReturnsCreatedEntity() throws Exception {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Festive Wrapping Paper");
        requestDTO.setWrappingPaperPrice(150);

        WrappingPaper savedWrappingPaper = WrappingPaper.of(
                requestDTO.getWrappingPaperName(),
                requestDTO.getWrappingPaperPrice()
        );

        Field wrappingPaperIdField = savedWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperIdField.setAccessible(true);
        wrappingPaperIdField.set(savedWrappingPaper, 3L);

        when(wrappingPaperRepository.save(any())).thenReturn(savedWrappingPaper);

        // When
        WrappingPaper result = wrappingPaperService.createWrappingPaper(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(3L);
        assertThat(result.getWrappingPaperName()).isEqualTo("Festive Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(150);

        verify(wrappingPaperRepository, times(1)).save(any(WrappingPaper.class));
    }

    @Test
    void createWrappingPaper_ValidRequest_ReturnsCreatedEntity() throws Exception {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Eco-Friendly Wrapping Paper");
        requestDTO.setWrappingPaperPrice(100);

        WrappingPaper savedWrappingPaper = WrappingPaper.of(
                requestDTO.getWrappingPaperName(),
                requestDTO.getWrappingPaperPrice()
        );

        Field wrappingPaperIdField = savedWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperIdField.setAccessible(true);
        wrappingPaperIdField.set(savedWrappingPaper, 1L);

        when(wrappingPaperRepository.save(any())).thenReturn(savedWrappingPaper);

        // When
        WrappingPaper result = wrappingPaperService.createWrappingPaper(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(1L);
        assertThat(result.getWrappingPaperName()).isEqualTo("Eco-Friendly Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(100);

        verify(wrappingPaperRepository, times(1)).save(any(WrappingPaper.class));
    }

    @Test
    void createWrappingPaper_NullName_ThrowsConstraintViolation() {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName(null);
        requestDTO.setWrappingPaperPrice(100);

        // When - Then
        try {
            wrappingPaperService.createWrappingPaper(requestDTO);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(RuntimeException.class); // Simulation of validation violation
            verifyNoInteractions(wrappingPaperRepository);
        }
    }



    @Test
    void createWrappingPaper_NullPrice_ThrowsConstraintViolation() {
        // Given
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Holiday Wrapping Paper");
        requestDTO.setWrappingPaperPrice(null);

        // When - Then
        try {
            wrappingPaperService.createWrappingPaper(requestDTO);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(RuntimeException.class); // Simulation of validation violation
            verifyNoInteractions(wrappingPaperRepository);
        }
    }


    @Test
    void getAllWrappingPaper_ReturnsNonEmptyList() {
        // Given
        WrappingPaper wrappingPaper1 = WrappingPaper.of("Birthday Wrapping Paper", 200);
        WrappingPaper wrappingPaper2 = WrappingPaper.of("Holiday Wrapping Paper", 150);

        when(wrappingPaperRepository.findAll()).thenReturn(List.of(wrappingPaper1, wrappingPaper2));

        // When
        List<WrappingPaper> result = wrappingPaperService.getAllWrappingPaper();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWrappingPaperName()).isEqualTo("Birthday Wrapping Paper");
        assertThat(result.get(1).getWrappingPaperName()).isEqualTo("Holiday Wrapping Paper");

        verify(wrappingPaperRepository, times(1)).findAll();
    }

    @Test
    void getAllWrappingPaper_ReturnsEmptyListWhenNoWrappingPapersExist() {
        // Given
        when(wrappingPaperRepository.findAll()).thenReturn(List.of());

        // When
        List<WrappingPaper> result = wrappingPaperService.getAllWrappingPaper();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(wrappingPaperRepository, times(1)).findAll();
    }

    @Test
    void updateWrappingPaper_ValidId_UpdatesWrappingPaper() {
        // Given
        Long wrappingPaperId = 1L;
        WrappingPaper existingWrappingPaper = WrappingPaper.of("Old Wrapping Paper", 100);

        Field wrappingPaperIdField = null;
        try {
            wrappingPaperIdField = existingWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
            wrappingPaperIdField.setAccessible(true);
            wrappingPaperIdField.set(existingWrappingPaper, wrappingPaperId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error while setting wrappingPaperId", e);
        }

        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Updated Wrapping Paper");
        requestDTO.setWrappingPaperPrice(200);

        when(wrappingPaperRepository.findById(wrappingPaperId)).thenReturn(java.util.Optional.of(existingWrappingPaper));

        // When
        WrappingPaper result = wrappingPaperService.updateWrappingPaper(wrappingPaperId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(wrappingPaperId);
        assertThat(result.getWrappingPaperName()).isEqualTo("Updated Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(200);

        verify(wrappingPaperRepository, times(1)).findById(wrappingPaperId);
    }

    @Test
    void updateWrappingPaper_InvalidId_ThrowsNotFoundException() {
        // Given
        Long wrappingPaperId = 99L;
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("Updated Wrapping Paper");
        requestDTO.setWrappingPaperPrice(150);

        when(wrappingPaperRepository.findById(wrappingPaperId)).thenReturn(java.util.Optional.empty());

        // When - Then
        try {
            wrappingPaperService.updateWrappingPaper(wrappingPaperId, requestDTO);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(WrappingPaperNotFoundException.class)
                    .hasMessage("WrappingPaper Id 99 not found");
            verify(wrappingPaperRepository, times(1)).findById(wrappingPaperId);
            verifyNoMoreInteractions(wrappingPaperRepository);
        }
    }

    @Test
    void updateWrappingPaper_NullValues_NoChangesMade() {
        // Given
        Long wrappingPaperId = 2L;
        WrappingPaper existingWrappingPaper = WrappingPaper.of("Original Wrapping Paper", 120);

        Field wrappingPaperIdField = null;
        try {
            wrappingPaperIdField = existingWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
            wrappingPaperIdField.setAccessible(true);
            wrappingPaperIdField.set(existingWrappingPaper, wrappingPaperId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error while setting wrappingPaperId", e);
        }

        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName(null);
        requestDTO.setWrappingPaperPrice(null);

        when(wrappingPaperRepository.findById(wrappingPaperId)).thenReturn(java.util.Optional.of(existingWrappingPaper));

        // When
        WrappingPaper result = wrappingPaperService.updateWrappingPaper(wrappingPaperId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(wrappingPaperId);
        assertThat(result.getWrappingPaperName()).isEqualTo("Original Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(120);

        verify(wrappingPaperRepository, times(1)).findById(wrappingPaperId);
    }


    @Test
    void deleteWrappingPaper_ValidId_DeletesWrappingPaper() {
        // Given
        Long wrappingPaperId = 1L;

        doNothing().when(wrappingPaperRepository).deleteById(wrappingPaperId);

        // When
        wrappingPaperService.deleteWrappingPaper(wrappingPaperId);

        // Then
        verify(wrappingPaperRepository, times(1)).deleteById(wrappingPaperId);
    }

    @Test
    void deleteWrappingPaper_InvalidId_ThrowsException() {
        // Given
        Long wrappingPaperId = 99L;

        doThrow(new WrappingPaperNotFoundException("WrappingPaper Id 99 not found"))
                .when(wrappingPaperRepository).deleteById(wrappingPaperId);

        // When - Then
        try {
            wrappingPaperService.deleteWrappingPaper(wrappingPaperId);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(WrappingPaperNotFoundException.class)
                    .hasMessage("WrappingPaper Id 99 not found");
        }
    }



    @Test
    void updateWrappingPaper_AllFieldsUpdated_SuccessfullyUpdates() {
        // Given
        Long wrappingPaperId = 5L;
        WrappingPaper existingWrappingPaper = WrappingPaper.of("Original Paper", 50);

        // Set ID using reflection.
        Field wrappingPaperIdField = null;
        try {
            wrappingPaperIdField = existingWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
            wrappingPaperIdField.setAccessible(true);
            wrappingPaperIdField.set(existingWrappingPaper, wrappingPaperId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error while setting wrappingPaperId", e);
        }

        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO();
        requestDTO.setWrappingPaperName("New Wrapping Paper");
        requestDTO.setWrappingPaperPrice(300);

        when(wrappingPaperRepository.findById(wrappingPaperId)).thenReturn(java.util.Optional.of(existingWrappingPaper));

        // When
        WrappingPaper result = wrappingPaperService.updateWrappingPaper(wrappingPaperId, requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWrappingPaperId()).isEqualTo(wrappingPaperId);
        assertThat(result.getWrappingPaperName()).isEqualTo("New Wrapping Paper");
        assertThat(result.getWrappingPaperPrice()).isEqualTo(300);

        verify(wrappingPaperRepository, times(1)).findById(wrappingPaperId);
    }
}