package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.exception.order.GuestOrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderPasswordProjection;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderProjection;
import com.nhnacademy.hexashoppingmallservice.repository.order.GuestOrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GuestOrderServiceTest {

    @InjectMocks
    private GuestOrderService guestOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GuestOrderRepository guestOrderRepository;

    @Test
    void createGuestOrder_Success() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "12345678901", "guest@example.com");
        Order mockOrder = mock(Order.class);
        GuestOrder mockGuestOrder = mock(GuestOrder.class);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(guestOrderRepository.save(any(GuestOrder.class))).thenReturn(mockGuestOrder);

        GuestOrder result = guestOrderService.createGuestOrder(requestDTO);

        assertEquals(mockGuestOrder, result);
        verify(orderRepository, times(1)).findById(1L);
        verify(guestOrderRepository, times(1)).save(any(GuestOrder.class));
    }

    @Test
    void createGuestOrder_NullSavedGuestOrder() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "12345678901", "guest@example.com");
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(guestOrderRepository.save(any(GuestOrder.class))).thenReturn(null);

        GuestOrder result = guestOrderService.createGuestOrder(requestDTO);

        assertEquals(null, result);
        verify(orderRepository, times(1)).findById(1L);
        verify(guestOrderRepository, times(1)).save(any(GuestOrder.class));
    }

    @Test
    void createGuestOrder_SaveThrowsException() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "12345678901", "guest@example.com");
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(guestOrderRepository.save(any(GuestOrder.class))).thenThrow(new RuntimeException("Database save error"));

        assertThrows(RuntimeException.class, () -> guestOrderService.createGuestOrder(requestDTO));
    }

    @Test
    void createGuestOrder_OrderNotFound() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "12345678901", "guest@example.com");

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> guestOrderService.createGuestOrder(requestDTO));
        verify(orderRepository, times(1)).findById(1L);
        verify(guestOrderRepository, never()).save(any(GuestOrder.class));
    }


    @Test
    void getGuestOrders_Success() {
        Pageable pageable = Mockito.mock(Pageable.class);
        GuestOrderProjection mockProjection = Mockito.mock(GuestOrderProjection.class);

        when(guestOrderRepository.findAllBy(pageable)).thenReturn(new PageImpl<>(List.of(mockProjection)));

        List<GuestOrderProjection> result = guestOrderService.getGuestOrders(pageable);

        assertEquals(1, result.size());
        assertEquals(mockProjection, result.get(0));
        verify(guestOrderRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void getGuestOrders_EmptyPage() {
        Pageable pageable = Mockito.mock(Pageable.class);

        when(guestOrderRepository.findAllBy(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<GuestOrderProjection> result = guestOrderService.getGuestOrders(pageable);

        assertEquals(0, result.size());
        verify(guestOrderRepository, times(1)).findAllBy(pageable);
    }


    @Test
    void getGuestOrder_Success() {
        Long orderId = 1L;
        GuestOrderProjection mockProjection = Mockito.mock(GuestOrderProjection.class);

        when(guestOrderRepository.existsById(orderId)).thenReturn(true);
        when(guestOrderRepository.findByOrderId(orderId)).thenReturn(mockProjection);

        GuestOrderProjection result = guestOrderService.getGuestOrder(orderId);

        assertEquals(mockProjection, result);
        verify(guestOrderRepository, times(1)).existsById(orderId);
        verify(guestOrderRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void getGuestOrder_OrderNotFoundException() {
        Long orderId = 1L;

        when(guestOrderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(GuestOrderNotFoundException.class, () -> guestOrderService.getGuestOrder(orderId));
        verify(guestOrderRepository, times(1)).existsById(orderId);
        verify(guestOrderRepository, never()).findByOrderId(any());
    }


    @Test
    void updateGuestOrder_Success() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "newPassword", "98765432101", "new@example.com");
        GuestOrder mockGuestOrder = mock(GuestOrder.class);

        when(guestOrderRepository.existsById(1L)).thenReturn(true);
        when(guestOrderRepository.findById(1L)).thenReturn(Optional.of(mockGuestOrder));

        GuestOrder result = guestOrderService.updateGuestOrder(requestDTO);

        verify(mockGuestOrder, times(1)).setGuestOrderPassword("newPassword");
        verify(mockGuestOrder, times(1)).setGuestOrderNumber("98765432101");
        verify(mockGuestOrder, times(1)).setGuestOrderEmail("new@example.com");
        assertEquals(mockGuestOrder, result);
    }

    @Test
    void updateGuestOrder_PartialUpdate() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, null, "98765432101", null);
        GuestOrder mockGuestOrder = mock(GuestOrder.class);

        when(guestOrderRepository.existsById(1L)).thenReturn(true);
        when(guestOrderRepository.findById(1L)).thenReturn(Optional.of(mockGuestOrder));

        GuestOrder result = guestOrderService.updateGuestOrder(requestDTO);

        verify(mockGuestOrder, never()).setGuestOrderPassword(any());
        verify(mockGuestOrder, times(1)).setGuestOrderNumber("98765432101");
        verify(mockGuestOrder, never()).setGuestOrderEmail(any());
        assertEquals(mockGuestOrder, result);
    }

    @Test
    void updateGuestOrder_GuestOrderNotFound() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "newPassword", "98765432101", "new@example.com");

        when(guestOrderRepository.existsById(1L)).thenReturn(false);

        assertThrows(GuestOrderNotFoundException.class, () -> guestOrderService.updateGuestOrder(requestDTO));
        verify(guestOrderRepository, times(1)).existsById(1L);
        verify(guestOrderRepository, never()).findById(any());
    }

    @Test
    void updateGuestOrder_NullOrderId() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(null, "newPassword", "98765432101", "new@example.com");

        assertThrows(GuestOrderNotFoundException.class, () -> guestOrderService.updateGuestOrder(requestDTO));

    }

    @Test
    void updateGuestOrder_RepositoryInteraction() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "newPassword", null, "new@example.com");
        GuestOrder mockGuestOrder = mock(GuestOrder.class);

        when(guestOrderRepository.existsById(1L)).thenReturn(true);
        when(guestOrderRepository.findById(1L)).thenReturn(Optional.of(mockGuestOrder));

        guestOrderService.updateGuestOrder(requestDTO);

        verify(guestOrderRepository, times(1)).existsById(1L);
        verify(guestOrderRepository, times(1)).findById(1L);
    }


    @Test
    void createGuestOrder_WithInvalidEmail() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "12345678901", "invalidEmail");

        // Exception will be thrown before even interacting with repositories
        assertThrows(OrderNotFoundException.class, () -> guestOrderService.createGuestOrder(requestDTO));

    }

    @Test
    void createGuestOrder_NullGuestOrderPassword() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, null, "12345678901", "guest@example.com");

        assertThrows(OrderNotFoundException.class, () -> guestOrderService.createGuestOrder(requestDTO));

    }

    @Test
    void createGuestOrder_WithEmptyGuestOrderNumber() {
        GuestOrderRequestDTO requestDTO = new GuestOrderRequestDTO(1L, "password123", "", "guest@example.com");

        assertThrows(OrderNotFoundException.class, () -> guestOrderService.createGuestOrder(requestDTO));

    }

    @Test
    void getGuestOrderPassword_Success() {
        Long orderId = 1L;
        String expectedPassword = "password123";
        GuestOrderPasswordProjection mockProjection = Mockito.mock(GuestOrderPasswordProjection.class);

        // Mock the behavior of the repository
        when(guestOrderRepository.existsByOrderId(orderId)).thenReturn(true);
        when(guestOrderRepository.findGuestOrderPasswordByOrderId(orderId)).thenReturn(Optional.of(mockProjection));
        when(mockProjection.getGuestOrderPassword()).thenReturn(expectedPassword);

        // Call the method
        String result = guestOrderService.getGuestOrderPassword(orderId, "anyPassword");

        // Assertions
        assertEquals(expectedPassword, result);
        verify(guestOrderRepository, times(1)).existsByOrderId(orderId);
        verify(guestOrderRepository, times(1)).findGuestOrderPasswordByOrderId(orderId);
    }

    @Test
    void getGuestOrderPassword_NotFound() {
        Long orderId = 1L;

        // Mock the behavior of the repository
        when(guestOrderRepository.existsByOrderId(orderId)).thenReturn(false);

        // Call the method
        String result = guestOrderService.getGuestOrderPassword(orderId, "anyPassword");

        // Assertions
        assertEquals("", result);
        verify(guestOrderRepository, times(1)).existsByOrderId(orderId);
        verify(guestOrderRepository, never()).findGuestOrderPasswordByOrderId(orderId);
    }
}