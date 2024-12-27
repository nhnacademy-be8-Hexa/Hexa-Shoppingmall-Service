package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;



    @Test
    void testGetOrderStatus_Success() throws Exception {
        Long orderStatusId = 1L;
        OrderStatus orderStatus = OrderStatus.builder().orderStatus("Delivered").build();

        Field orderStatusField = orderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusField.setAccessible(true);
        orderStatusField.set(orderStatus, orderStatusId);

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.of(orderStatus));

        OrderStatus retrievedOrderStatus = orderStatusService.getOrderStatus(orderStatusId);

        assertNotNull(retrievedOrderStatus);
        assertEquals("Delivered", retrievedOrderStatus.getOrderStatus());
        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }



    @Test
    void testGetOrderStatus_NotFound() {
        Long orderStatusId = 999L;

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.empty());

        assertThrows(OrderStatusNotFoundException.class,
                () -> orderStatusService.getOrderStatus(orderStatusId));

        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }



    @Test
    void testUpdateOrderStatus_Success() {
        Long orderStatusId = 1L;
        OrderStatus orderStatus = OrderStatus.builder().orderStatus("Processing").build();
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Shipped");

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.of(orderStatus));

        OrderStatus updatedOrderStatus = orderStatusService.updateOrderStatus(orderStatusId, requestDTO);

        assertNotNull(updatedOrderStatus);
        assertEquals("Shipped", updatedOrderStatus.getOrderStatus());
        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }

    @Test
    void testUpdateOrderStatus_NotFound() {
        Long orderStatusId = 999L;
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Cancelled");

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.empty());

        assertThrows(OrderStatusNotFoundException.class,
                () -> orderStatusService.updateOrderStatus(orderStatusId, requestDTO));

        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }


    @Test
    void testGetAllOrderStatus_Success() {
        List<OrderStatus> orderStatuses = List.of(
                OrderStatus.builder().orderStatus("Processing").build(),
                OrderStatus.builder().orderStatus("Shipped").build()
        );

        when(orderStatusRepository.findAll()).thenReturn(orderStatuses);

        List<OrderStatus> retrievedOrderStatuses = orderStatusService.getAllOrderStatus();

        assertNotNull(retrievedOrderStatuses);
        assertEquals(2, retrievedOrderStatuses.size());
        assertEquals("Processing", retrievedOrderStatuses.get(0).getOrderStatus());
        assertEquals("Shipped", retrievedOrderStatuses.get(1).getOrderStatus());
        verify(orderStatusRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOrderStatus_EmptyList() {
        when(orderStatusRepository.findAll()).thenReturn(List.of());

        List<OrderStatus> retrievedOrderStatuses = orderStatusService.getAllOrderStatus();

        assertNotNull(retrievedOrderStatuses);
        assertTrue(retrievedOrderStatuses.isEmpty());
        verify(orderStatusRepository, times(1)).findAll();
    }

    @Test
    void testCreateOrderStatus_Success() {
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Processing");
        OrderStatus orderStatus = OrderStatus.builder().orderStatus("Processing").build();

        when(orderStatusRepository.save(any(OrderStatus.class))).thenReturn(orderStatus);
        when(orderStatusRepository.existsByOrderStatus(requestDTO.getOrderStatus())).thenReturn(true);

        OrderStatus createdOrderStatus = orderStatusService.createOrderStatus(requestDTO);

        assertNotNull(createdOrderStatus);
        assertEquals("Processing", createdOrderStatus.getOrderStatus());
        verify(orderStatusRepository, times(1)).save(any(OrderStatus.class));
    }

    @Test
    void testCreateOrderStatus_AlreadyExists() {
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Processing");

        when(orderStatusRepository.existsByOrderStatus(requestDTO.getOrderStatus())).thenReturn(false);

        assertThrows(OrderStatusNotFoundException.class,
                () -> orderStatusService.createOrderStatus(requestDTO));

        verify(orderStatusRepository, times(1)).existsByOrderStatus(requestDTO.getOrderStatus());
        verify(orderStatusRepository, never()).save(any(OrderStatus.class));
    }



    @Test
    void testUpdateOrderStatus_InvalidRequest() {
        Long orderStatusId = 1L;
        OrderStatus orderStatus = OrderStatus.builder().orderStatus(null).build();
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO(null);

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.empty());

        assertThrows(OrderStatusNotFoundException.class,
                () -> orderStatusService.updateOrderStatus(orderStatusId, requestDTO));

        verify(orderStatusRepository, times(1)).findById(orderStatusId);
    }

    @Test
    void testUpdateOrderStatus_NoChange() {
        Long orderStatusId = 1L;
        OrderStatus orderStatus = OrderStatus.builder().orderStatus("Processing").build();
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Processing");

        when(orderStatusRepository.findById(orderStatusId)).thenReturn(Optional.of(orderStatus));

        OrderStatus updatedOrderStatus = orderStatusService.updateOrderStatus(orderStatusId, requestDTO);

        assertNotNull(updatedOrderStatus);
        assertEquals("Processing", updatedOrderStatus.getOrderStatus());
        verify(orderStatusRepository, times(1)).findById(orderStatusId);
        verify(orderStatusRepository, never()).save(any(OrderStatus.class));
    }

    @Test
    void testDeleteOrderStatus_Success() {
        Long orderStatusId = 1L;

        when(orderStatusRepository.existsById(orderStatusId)).thenReturn(true);
        doNothing().when(orderStatusRepository).deleteById(orderStatusId);

        assertDoesNotThrow(() -> orderStatusService.deleteOrderStatus(orderStatusId));

        verify(orderStatusRepository, times(1)).existsById(orderStatusId);
        verify(orderStatusRepository, times(1)).deleteById(orderStatusId);
    }

    @Test
    void testDeleteOrderStatus_NotFound() {
        Long orderStatusId = 999L;

        when(orderStatusRepository.existsById(orderStatusId)).thenReturn(false);

        assertThrows(OrderStatusNotFoundException.class,
                () -> orderStatusService.deleteOrderStatus(orderStatusId));

        verify(orderStatusRepository, times(1)).existsById(orderStatusId);
        verify(orderStatusRepository, never()).deleteById(orderStatusId);
    }
}