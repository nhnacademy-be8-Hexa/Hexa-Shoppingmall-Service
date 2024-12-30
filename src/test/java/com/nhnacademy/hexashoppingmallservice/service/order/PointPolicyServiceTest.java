package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointPolicy;
import com.nhnacademy.hexashoppingmallservice.exception.point.PointPolicyAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.point.PointPolicyNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.PointPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PointPolicyServiceTest {

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @InjectMocks
    private PointPolicyService pointPolicyService;

    private PointPolicy pointPolicy;

    @BeforeEach
    public void setUp() {
        pointPolicy = PointPolicy.builder()
                .pointPolicyName("testPolicy")
                .pointDelta(100)
                .build();
    }

    @Test
    public void testCreatePointPolicy() {
        // Arrange
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        // Act
        PointPolicy createdPolicy = pointPolicyService.createPointPolicy(pointPolicy);

        // Assert
        assertNotNull(createdPolicy);
        assertEquals("testPolicy", createdPolicy.getPointPolicyName());
        assertEquals(100, createdPolicy.getPointDelta());

        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    public void testUpdatePointPolicy_Success() {
        // Arrange
        when(pointPolicyRepository.existsById(anyString())).thenReturn(true);
        when(pointPolicyRepository.findById(anyString())).thenReturn(Optional.of(pointPolicy));

        pointPolicy.setPointDelta(200);

        // Act
        PointPolicy updatedPolicy = pointPolicyService.updatePointPolicy(pointPolicy);

        // Assert
        assertNotNull(updatedPolicy);
        assertEquals(200, updatedPolicy.getPointDelta());

        verify(pointPolicyRepository, times(1)).existsById(anyString());
        verify(pointPolicyRepository, times(1)).findById(anyString());
    }

    @Test
    public void testUpdatePointPolicy_Failure_PointPolicyNotFound() {
        // Arrange
        when(pointPolicyRepository.existsById(anyString())).thenReturn(false);

        // Act & Assert
        PointPolicyNotFoundException exception = assertThrows(PointPolicyNotFoundException.class, () -> {
            pointPolicyService.updatePointPolicy(pointPolicy);
        });
        assertEquals("Point policy testPolicy is not found", exception.getMessage());

        verify(pointPolicyRepository, times(1)).existsById(anyString());
    }

    @Test
    public void testDeletePointPolicy_Success() {
        // Arrange
        when(pointPolicyRepository.existsById(anyString())).thenReturn(true);

        // Act
        pointPolicyService.deletePointPolicy("testPolicy");

        // Assert
        verify(pointPolicyRepository, times(1)).deleteById(anyString());
    }

    @Test
    public void testDeletePointPolicy_Failure_PointPolicyNotFound() {
        // Arrange
        when(pointPolicyRepository.existsById(anyString())).thenReturn(false);

        // Act & Assert
        PointPolicyAlreadyExistException exception = assertThrows(PointPolicyAlreadyExistException.class, () -> {
            pointPolicyService.deletePointPolicy("testPolicy");
        });
        assertEquals("Point policy testPolicy is not found", exception.getMessage());

        verify(pointPolicyRepository, times(1)).existsById(anyString());
    }

    @Test
    public void testGetAllPointPolicies() {
        // Arrange
        when(pointPolicyRepository.findAll()).thenReturn(List.of(pointPolicy));

        // Act
        List<PointPolicy> policies = pointPolicyService.getAllPointPolicies();

        // Assert
        assertNotNull(policies);
        assertEquals(1, policies.size());
        assertEquals("testPolicy", policies.get(0).getPointPolicyName());

        verify(pointPolicyRepository, times(1)).findAll();
    }

}