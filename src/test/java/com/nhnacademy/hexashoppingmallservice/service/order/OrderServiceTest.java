package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ParameterNotEnouthException;
import com.nhnacademy.hexashoppingmallservice.exception.order.WrappingPaperNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.WrappingPaperRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WrappingPaperRepository wrappingPaperRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("createOrder 메서드 테스트")
    class CreateOrderTests {

        @Test
        @DisplayName("성공적으로 주문을 생성한다")
        void createOrder_Success() {
            // Arrange
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    50000,
                    1L,
                    1L,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            List<Long> bookIds = Arrays.asList(1L, 2L);
            List<Integer> amounts = Arrays.asList(2, 3);
            Long couponId = 1L;

            // 실제 Member, WrappingPaper, OrderStatus 생성
            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            WrappingPaper wrappingPaper = WrappingPaper.of("Premium Wrapping", 5000);

            OrderStatus orderStatus = OrderStatus.of("Processing");

            Order order = Order.of(
                    member,
                    dto.getOrderPrice(),
                    wrappingPaper,
                    orderStatus,
                    dto.getZoneCode(),
                    dto.getAddress(),
                    dto.getAddressDetail()
            );

            when(memberRepository.existsById(dto.getMemberId())).thenReturn(true);
            when(memberRepository.findById(dto.getMemberId())).thenReturn(Optional.of(member));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(true);
            when(wrappingPaperRepository.findById(dto.getWrappingPaperId())).thenReturn(Optional.of(wrappingPaper));

            when(orderStatusRepository.existsById(dto.getOrderStatusId())).thenReturn(true);
            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.of(orderStatus));

            when(bookRepository.countByBookIdIn(bookIds)).thenReturn((long) bookIds.size());
            when(bookRepository.findById(1L)).thenReturn(Optional.of(mock(Book.class)));
            when(bookRepository.findById(2L)).thenReturn(Optional.of(mock(Book.class)));

            when(orderRepository.save(any(Order.class))).thenReturn(order);
            when(orderBookRepository.save(any(OrderBook.class))).thenReturn(mock(OrderBook.class));

            // Act
            orderService.createOrder(dto, bookIds, amounts, couponId);

            // Assert
            verify(memberRepository, times(1)).existsById(dto.getMemberId());
            verify(memberRepository, times(1)).findById(dto.getMemberId());

            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, times(1)).findById(dto.getWrappingPaperId());

            verify(orderStatusRepository, times(1)).existsById(dto.getOrderStatusId());
            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());

            verify(bookRepository, times(1)).countByBookIdIn(bookIds);
            // existsById 검증을 제거
            // verify(bookRepository, times(bookIds.size())).existsById(anyLong());

            verify(bookRepository, times(bookIds.size())).findById(anyLong());

            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository, times(1)).save(orderCaptor.capture());

            Order savedOrder = orderCaptor.getValue();
            assertEquals(dto.getOrderPrice(), savedOrder.getOrderPrice());
            assertEquals(dto.getZoneCode(), savedOrder.getZoneCode());
            assertEquals(dto.getAddress(), savedOrder.getAddress());
            assertEquals(dto.getAddressDetail(), savedOrder.getAddressDetail());
            assertEquals(member, savedOrder.getMember());
            assertEquals(wrappingPaper, savedOrder.getWrappingPaper());
            assertEquals(orderStatus, savedOrder.getOrderStatus());

            verify(orderBookRepository, times(bookIds.size())).save(any(OrderBook.class));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 주문 생성 시 MemberNotFoundException 발생")
        void createOrder_MemberNotFound() {
            // Arrange
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    50000,
                    1L,
                    1L,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            List<Long> bookIds = Arrays.asList(1L, 2L);
            List<Integer> amounts = Arrays.asList(2, 3);
            Long couponId = 1L;

            when(memberRepository.existsById(dto.getMemberId())).thenReturn(false);

            // Act & Assert
            MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
                orderService.createOrder(dto, bookIds, amounts, couponId);
            });

            assertEquals("Member ID member123 not found", exception.getMessage());

            verify(memberRepository, times(1)).existsById(dto.getMemberId());
            verify(memberRepository, never()).findById(dto.getMemberId());

            verify(wrappingPaperRepository, never()).existsById(anyLong());
            verify(wrappingPaperRepository, never()).findById(anyLong());

            verify(orderStatusRepository, never()).existsById(anyLong());
            verify(orderStatusRepository, never()).findById(anyLong());

            verify(bookRepository, never()).countByBookIdIn(anyList());
            verify(bookRepository, never()).findById(anyLong());

            verify(orderRepository, never()).save(any(Order.class));
            verify(orderBookRepository, never()).save(any(OrderBook.class));
        }

        @Test
        @DisplayName("존재하지 않는 포장지 ID로 주문 생성 시 WrappingPaperNotFoundException 발생")
        void createOrder_WrappingPaperNotFound() {
            // Arrange
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    50000,
                    999L, // non-existing wrappingPaperId
                    1L,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            List<Long> bookIds = Arrays.asList(1L, 2L);
            List<Integer> amounts = Arrays.asList(2, 3);
            Long couponId = 1L;

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            when(memberRepository.existsById(dto.getMemberId())).thenReturn(true);
            when(memberRepository.findById(dto.getMemberId())).thenReturn(Optional.of(member));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(false);

            // Act & Assert
            WrappingPaperNotFoundException exception = assertThrows(WrappingPaperNotFoundException.class, () -> {
                orderService.createOrder(dto, bookIds, amounts, couponId);
            });

            assertEquals("WrappingPaper ID 999 not found", exception.getMessage());

            verify(memberRepository, times(1)).existsById(dto.getMemberId());
            verify(memberRepository, times(1)).findById(dto.getMemberId());

            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, never()).findById(dto.getWrappingPaperId());

            verify(orderStatusRepository, never()).existsById(anyLong());
            verify(orderStatusRepository, never()).findById(anyLong());

            verify(bookRepository, never()).countByBookIdIn(anyList());
            verify(bookRepository, never()).findById(anyLong());

            verify(orderRepository, never()).save(any(Order.class));
            verify(orderBookRepository, never()).save(any(OrderBook.class));
        }

        @Test
        @DisplayName("존재하지 않는 주문 상태 ID로 주문 생성 시 OrderStatusNotFoundException 발생")
        void createOrder_OrderStatusNotFound() {
            // Arrange
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    50000,
                    1L,
                    999L, // non-existing orderStatusId
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            List<Long> bookIds = Arrays.asList(1L, 2L);
            List<Integer> amounts = Arrays.asList(2, 3);
            Long couponId = 1L;

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            WrappingPaper wrappingPaper = WrappingPaper.of("Premium Wrapping", 5000);

            when(memberRepository.existsById(dto.getMemberId())).thenReturn(true);
            when(memberRepository.findById(dto.getMemberId())).thenReturn(Optional.of(member));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(true);
            when(wrappingPaperRepository.findById(dto.getWrappingPaperId())).thenReturn(Optional.of(wrappingPaper));

            when(orderStatusRepository.existsById(dto.getOrderStatusId())).thenReturn(false);

            // Act & Assert
            OrderStatusNotFoundException exception = assertThrows(OrderStatusNotFoundException.class, () -> {
                orderService.createOrder(dto, bookIds, amounts, couponId);
            });

            assertEquals("OrderStatus ID 999 is not found", exception.getMessage());

            verify(memberRepository, times(1)).existsById(dto.getMemberId());
            verify(memberRepository, times(1)).findById(dto.getMemberId());

            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, times(1)).findById(dto.getWrappingPaperId());

            verify(orderStatusRepository, times(1)).existsById(dto.getOrderStatusId());
            verify(orderStatusRepository, never()).findById(dto.getOrderStatusId());

            verify(bookRepository, never()).countByBookIdIn(anyList());
            verify(bookRepository, never()).findById(anyLong());

            verify(orderRepository, never()).save(any(Order.class));
            verify(orderBookRepository, never()).save(any(OrderBook.class));
        }

        @Test
        @DisplayName("존재하지 않는 책 ID로 주문 생성 시 BookNotFoundException 발생")
        void createOrder_BookNotFound() {
            // Arrange
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    50000,
                    1L,
                    1L,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            List<Long> bookIds = Arrays.asList(1L, 999L); // 999L is non-existing
            List<Integer> amounts = Arrays.asList(2, 3);
            Long couponId = 1L;

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            WrappingPaper wrappingPaper = WrappingPaper.of("Premium Wrapping", 5000);

            OrderStatus orderStatus = OrderStatus.of("Processing");

            Order order = Order.of(
                    member,
                    dto.getOrderPrice(),
                    wrappingPaper,
                    orderStatus,
                    dto.getZoneCode(),
                    dto.getAddress(),
                    dto.getAddressDetail()
            );

            when(memberRepository.existsById(dto.getMemberId())).thenReturn(true);
            when(memberRepository.findById(dto.getMemberId())).thenReturn(Optional.of(member));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(true);
            when(wrappingPaperRepository.findById(dto.getWrappingPaperId())).thenReturn(Optional.of(wrappingPaper));

            when(orderStatusRepository.existsById(dto.getOrderStatusId())).thenReturn(true);
            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.of(orderStatus));

            when(bookRepository.countByBookIdIn(bookIds)).thenReturn((long) bookIds.size());
            when(bookRepository.findById(1L)).thenReturn(Optional.of(mock(Book.class)));
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
                orderService.createOrder(dto, bookIds, amounts, couponId);
            });

            assertEquals("Book ID 999 not found", exception.getMessage());

            verify(memberRepository, times(1)).existsById(dto.getMemberId());
            verify(memberRepository, times(1)).findById(dto.getMemberId());

            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, times(1)).findById(dto.getWrappingPaperId());

            verify(orderStatusRepository, times(1)).existsById(dto.getOrderStatusId());
            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());

            verify(bookRepository, times(1)).countByBookIdIn(bookIds);
            verify(bookRepository, times(bookIds.size())).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("getAllOrders 메서드 테스트")
    class GetAllOrdersTests {

        @Test
        @DisplayName("성공적으로 모든 주문을 조회한다")
        void getAllOrders_Success() {
            // Arrange
            OrderProjection order1 = mock(OrderProjection.class);
            OrderProjection order2 = mock(OrderProjection.class);
            List<OrderProjection> orders = Arrays.asList(order1, order2);

            Pageable pageable = mock(Pageable.class);
            when(orderRepository.findAllBy(pageable)).thenReturn(new PageImpl<>(orders));

            // Act
            List<OrderProjection> result = orderService.getAllOrders(pageable);

            // Assert
            assertEquals(2, result.size());
            verify(orderRepository, times(1)).findAllBy(pageable);
        }
    }

    @Nested
    @DisplayName("getOrdersByMemberId 메서드 테스트")
    class GetOrdersByMemberIdTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 주문을 조회한다")
        void getOrdersByMemberId_Success() {
            // Arrange
            String memberId = "member123";
            OrderProjection order1 = mock(OrderProjection.class);
            OrderProjection order2 = mock(OrderProjection.class);
            List<OrderProjection> orders = Arrays.asList(order1, order2);

            Pageable pageable = mock(Pageable.class);
            when(orderRepository.findByMember_MemberId(memberId, pageable)).thenReturn(new PageImpl<>(orders));

            // Act
            List<OrderProjection> result = orderService.getOrdersByMemberId(memberId, pageable);

            // Assert
            assertEquals(2, result.size());
            verify(orderRepository, times(1)).findByMember_MemberId(memberId, pageable);
        }

        @Test
        @DisplayName("회원 ID로 주문을 조회할 때 주문이 없는 경우 빈 리스트 반환")
        void getOrdersByMemberId_NoOrders() {
            // Arrange
            String memberId = "member123";
            List<OrderProjection> orders = Arrays.asList();

            Pageable pageable = mock(Pageable.class);
            when(orderRepository.findByMember_MemberId(memberId, pageable)).thenReturn(new PageImpl<>(orders));

            // Act
            List<OrderProjection> result = orderService.getOrdersByMemberId(memberId, pageable);

            // Assert
            assertTrue(result.isEmpty());
            verify(orderRepository, times(1)).findByMember_MemberId(memberId, pageable);
        }
    }

    @Nested
    @DisplayName("getOrder 메서드 테스트")
    class GetOrderTests {

        @Test
        @DisplayName("성공적으로 특정 주문을 조회한다")
        void getOrder_Success() {
            // Arrange
            Long orderId = 1L;
            OrderProjection order = mock(OrderProjection.class);

            when(orderRepository.existsById(orderId)).thenReturn(true);
            when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

            // Act
            OrderProjection result = orderService.getOrder(orderId);

            // Assert
            assertNotNull(result);
            verify(orderRepository, times(1)).existsById(orderId);
            verify(orderRepository, times(1)).findByOrderId(orderId);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 주문을 조회 시 OrderNotFoundException 발생")
        void getOrder_NotFound() {
            // Arrange
            Long orderId = 1L;

            when(orderRepository.existsById(orderId)).thenReturn(false);

            // Act & Assert
            OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
                orderService.getOrder(orderId);
            });

            assertEquals("Order ID 1 not found", exception.getMessage());
            verify(orderRepository, times(1)).existsById(orderId);
            verify(orderRepository, never()).findByOrderId(orderId);
        }
    }

    @Nested
    @DisplayName("getAmount 메서드 테스트")
    class GetAmountTests {

        @Test
        @DisplayName("성공적으로 특정 주문의 책 수량을 조회한다")
        void getAmount_Success() {
            // Arrange
            Long orderId = 1L;
            Long bookId = 2L;
            Long expectedAmount = 3L;

            when(orderRepository.existsById(orderId)).thenReturn(true);
            when(bookRepository.existsById(bookId)).thenReturn(true);
            when(orderBookRepository.sumOrderBookAmountByOrderIdAndBookId(orderId, bookId)).thenReturn(expectedAmount);

            // Act
            Long result = orderService.getAmount(orderId, bookId);

            // Assert
            assertEquals(expectedAmount, result);
            verify(orderRepository, times(1)).existsById(orderId);
            verify(bookRepository, times(1)).existsById(bookId);
            verify(orderBookRepository, times(1)).sumOrderBookAmountByOrderIdAndBookId(orderId, bookId);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 책 수량 조회 시 OrderNotFoundException 발생")
        void getAmount_OrderNotFound() {
            // Arrange
            Long orderId = 1L;
            Long bookId = 2L;

            when(orderRepository.existsById(orderId)).thenReturn(false);

            // Act & Assert
            OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
                orderService.getAmount(orderId, bookId);
            });

            assertEquals("Order ID 1 not found", exception.getMessage());
            verify(orderRepository, times(1)).existsById(orderId);
            verify(bookRepository, never()).existsById(bookId);
            verify(orderBookRepository, never()).sumOrderBookAmountByOrderIdAndBookId(orderId, bookId);
        }

        @Test
        @DisplayName("존재하지 않는 책 ID로 책 수량 조회 시 BookNotFoundException 발생")
        void getAmount_BookNotFound() {
            // Arrange
            Long orderId = 1L;
            Long bookId = 999L;

            when(orderRepository.existsById(orderId)).thenReturn(true);
            when(bookRepository.existsById(bookId)).thenReturn(false);

            // Act & Assert
            BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
                orderService.getAmount(orderId, bookId);
            });

            assertEquals("Book ID 999 not found", exception.getMessage());
            verify(orderRepository, times(1)).existsById(orderId);
            verify(bookRepository, times(1)).existsById(bookId);
            verify(orderBookRepository, never()).sumOrderBookAmountByOrderIdAndBookId(orderId, bookId);
        }
    }

    @Nested
    @DisplayName("updateOrder 메서드 테스트")
    class UpdateOrderTests {

        @Test
        @DisplayName("성공적으로 주문을 업데이트한다")
        void updateOrder_Success() {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    60000,
                    2L,
                    2L,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            WrappingPaper wrappingPaper = WrappingPaper.of("Standard Wrapping", 3000);
            OrderStatus newOrderStatus = OrderStatus.of("Completed");

            Order order = Order.of(
                    member,
                    50000,
                    WrappingPaper.of("Premium Wrapping", 5000),
                    OrderStatus.of("Processing"),
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.of(newOrderStatus));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(true);
            when(wrappingPaperRepository.findById(dto.getWrappingPaperId())).thenReturn(Optional.of(wrappingPaper));

            // Act
            orderService.updateOrder(orderId, dto);

            // Assert
            verify(orderRepository, times(1)).findById(orderId);

            assertEquals(dto.getOrderPrice(), order.getOrderPrice());
            assertEquals(dto.getZoneCode(), order.getZoneCode());
            assertEquals(dto.getAddressDetail(), order.getAddressDetail());
            assertEquals(dto.getAddress(), order.getAddress());
            assertEquals(newOrderStatus, order.getOrderStatus());
            assertEquals(wrappingPaper, order.getWrappingPaper());

            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());
            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, times(1)).findById(dto.getWrappingPaperId());

            // 주문 업데이트 시 save를 호출하지 않으므로, verify 하지 않아도 됨
            // 만약 save 호출이 필요하다면, OrderService.updateOrder 메서드에 orderRepository.save(order)을 추가해야 함
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 주문 업데이트 시 OrderNotFoundException 발생")
        void updateOrder_OrderNotFound() {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    60000,
                    2L,
                    2L,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // Act & Assert
            OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
                orderService.updateOrder(orderId, dto);
            });

            assertEquals("1", exception.getMessage());

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderStatusRepository, never()).findById(anyLong());
            verify(wrappingPaperRepository, never()).existsById(anyLong());
            verify(wrappingPaperRepository, never()).findById(anyLong());
            // save 호출 여부 검증 필요시 추가
        }

        @Test
        @DisplayName("존재하지 않는 주문 상태 ID로 주문 업데이트 시 OrderStatusNotFoundException 발생")
        void updateOrder_OrderStatusNotFound() {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    60000,
                    2L,
                    999L, // non-existing orderStatusId
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            Order order = Order.of(
                    member,
                    50000,
                    WrappingPaper.of("Premium Wrapping", 5000),
                    OrderStatus.of("Processing"),
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.empty());

            // Act & Assert
            OrderStatusNotFoundException exception = assertThrows(OrderStatusNotFoundException.class, () -> {
                orderService.updateOrder(orderId, dto);
            });

            assertEquals("OrderStatus ID 999 is not found", exception.getMessage());

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());
            verify(wrappingPaperRepository, never()).existsById(anyLong());
            verify(wrappingPaperRepository, never()).findById(anyLong());
            // save 호출 여부 검증 필요시 추가
        }

        @Test
        @DisplayName("존재하지 않는 포장지 ID로 주문 업데이트 시 WrappingPaperNotFoundException 발생")
        void updateOrder_WrappingPaperNotFound() {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    60000,
                    999L, // non-existing wrappingPaperId
                    2L,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            OrderStatus orderStatus = OrderStatus.of("Processing");

            Order order = Order.of(
                    member,
                    50000,
                    WrappingPaper.of("Premium Wrapping", 5000),
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.of(orderStatus));

            when(wrappingPaperRepository.existsById(dto.getWrappingPaperId())).thenReturn(false);

            // Act & Assert
            WrappingPaperNotFoundException exception = assertThrows(WrappingPaperNotFoundException.class, () -> {
                orderService.updateOrder(orderId, dto);
            });

            assertEquals("WrappingPaper ID 999 not found", exception.getMessage());

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());
            verify(wrappingPaperRepository, times(1)).existsById(dto.getWrappingPaperId());
            verify(wrappingPaperRepository, never()).findById(dto.getWrappingPaperId());
            // save 호출 여부 검증 필요시 추가
        }

        @Test
        @DisplayName("주문 업데이트 시 일부 필드만 변경한다")
        void updateOrder_PartialUpdate() {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO dto = new OrderRequestDTO(
                    "member123",
                    null, // orderPrice not being updated
                    null, // wrappingPaperId not being updated
                    2L,
                    "54321",
                    null, // address not being updated
                    "Suite 202"
            );

            Member member = Member.of(
                    "member123",
                    "password123",
                    "John Doe",
                    "01012345678",
                    "john.doe@example.com",
                    LocalDate.of(1990, 1, 1),
                    Rating.builder().ratingId(1L).ratingName("Gold").build(),
                    MemberStatus.builder().statusId(1L).statusName("Active").build()
            );

            WrappingPaper currentWrappingPaper = WrappingPaper.of("Premium Wrapping", 5000);
            OrderStatus newOrderStatus = OrderStatus.of("Completed");

            Order order = Order.of(
                    member,
                    50000,
                    currentWrappingPaper,
                    OrderStatus.of("Processing"),
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderStatusRepository.findById(dto.getOrderStatusId())).thenReturn(Optional.of(newOrderStatus));

            // Act
            orderService.updateOrder(orderId, dto);

            // Assert
            verify(orderRepository, times(1)).findById(orderId);
            verify(orderStatusRepository, times(1)).findById(dto.getOrderStatusId());

            // orderPrice should remain unchanged
            assertEquals(50000, order.getOrderPrice());

            // zoneCode should be updated
            assertEquals(dto.getZoneCode(), order.getZoneCode());

            // addressDetail should be updated
            assertEquals(dto.getAddressDetail(), order.getAddressDetail());

            // address should remain unchanged
            assertEquals("123 Main St", order.getAddress());

            // orderStatus should be updated
            assertEquals(newOrderStatus, order.getOrderStatus());

            // wrappingPaper should remain unchanged
            assertEquals(currentWrappingPaper, order.getWrappingPaper());

            // wrappingPaperRepository의 existsById와 findById가 호출되지 않았는지 확인
            verify(wrappingPaperRepository, never()).existsById(anyLong());
            verify(wrappingPaperRepository, never()).findById(anyLong());

            // 만약 orderRepository.save(order)를 호출하도록 서비스 메서드가 구현되어 있다면, 검증 추가
            // 예시:
            // verify(orderRepository, times(1)).save(order);
        }
    }
}