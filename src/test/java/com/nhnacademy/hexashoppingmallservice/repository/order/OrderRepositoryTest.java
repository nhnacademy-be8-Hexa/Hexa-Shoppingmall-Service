package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WrappingPaperRepository wrappingPaperRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderBookRepository orderBookRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Helper method to create and persist a Member entity.
     */
    private Member createMember(String memberId) {
        Member member = Member.of(
                memberId,
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                Rating.builder().ratingId(1L).ratingName("Gold").build(),
                MemberStatus.builder().statusId(1L).statusName("Active").build()
        );
        return entityManager.persistFlushFind(member);
    }

    /**
     * Helper method to create and persist a WrappingPaper entity.
     */
    private WrappingPaper createWrappingPaper(String name, int price) {
        WrappingPaper wrappingPaper = WrappingPaper.of(name, price);
        return entityManager.persistFlushFind(wrappingPaper);
    }

    /**
     * Helper method to create and persist an OrderStatus entity.
     */
    private OrderStatus createOrderStatus(String statusName) {
        OrderStatus orderStatus = OrderStatus.of(statusName);
        return entityManager.persistFlushFind(orderStatus);
    }

    /**
     * Helper method to create and persist a Book entity.
     */
    private Book createBook(Long bookId, String title) {
        Book book = Book.of(
                title,
                "test",
                LocalDate.now()
        );
        return entityManager.persistFlushFind(book);
    }

    /**
     * Helper method to create and persist an Order entity.
     */
    private Order createOrder(Member member, WrappingPaper wrappingPaper, OrderStatus orderStatus,
                              String zoneCode, String address, String addressDetail) {
        Order order = Order.of(
                member,
                50000,
                wrappingPaper,
                orderStatus,
                zoneCode,
                address,
                addressDetail
        );
        return entityManager.persistFlushFind(order);
    }

    /**
     * Helper method to create and persist an OrderBook entity.
     */
    private OrderBook createOrderBook(Order order, Book book, int amount, Long couponId) {
        OrderBook orderBook = OrderBook.of(order, book, amount, couponId);
        return entityManager.persistFlushFind(orderBook);
    }

    @Nested
    @DisplayName("OrderRepository CRUD Operations")
    class OrderCrudTests {

        @Test
        @DisplayName("Save Order")
        void saveOrder() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Premium Wrapping", 5000);
            OrderStatus orderStatus = createOrderStatus("Processing");

            Order order = Order.of(
                    member,
                    50000,
                    wrappingPaper,
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            // Act
            Order savedOrder = orderRepository.save(order);
            entityManager.flush();

            // Assert
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getOrderId()).isNotNull();
            assertThat(savedOrder.getMember()).isEqualTo(member);
            assertThat(savedOrder.getWrappingPaper()).isEqualTo(wrappingPaper);
            assertThat(savedOrder.getOrderStatus()).isEqualTo(orderStatus);
            assertThat(savedOrder.getZoneCode()).isEqualTo("12345");
            assertThat(savedOrder.getAddress()).isEqualTo("123 Main St");
            assertThat(savedOrder.getAddressDetail()).isEqualTo("Apt 101");
        }

        @Test
        @DisplayName("Find Order by ID")
        void findOrderById() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Premium Wrapping", 5000);
            OrderStatus orderStatus = createOrderStatus("Processing");

            Order order = createOrder(
                    member,
                    wrappingPaper,
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            // Act
            Optional<Order> foundOrderOpt = orderRepository.findById(order.getOrderId());

            // Assert
            assertThat(foundOrderOpt).isPresent();
            Order foundOrder = foundOrderOpt.get();
            assertThat(foundOrder.getOrderId()).isEqualTo(order.getOrderId());
            assertThat(foundOrder.getMember()).isEqualTo(member);
            assertThat(foundOrder.getWrappingPaper()).isEqualTo(wrappingPaper);
            assertThat(foundOrder.getOrderStatus()).isEqualTo(orderStatus);
            assertThat(foundOrder.getZoneCode()).isEqualTo("12345");
            assertThat(foundOrder.getAddress()).isEqualTo("123 Main St");
            assertThat(foundOrder.getAddressDetail()).isEqualTo("Apt 101");
        }

        @Test
        @DisplayName("Check if Order Exists by ID")
        void existsOrderById() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Premium Wrapping", 5000);
            OrderStatus orderStatus = createOrderStatus("Processing");

            Order order = createOrder(
                    member,
                    wrappingPaper,
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            // Act & Assert
            assertThat(orderRepository.existsById(order.getOrderId())).isTrue();
            assertThat(orderRepository.existsById(999L)).isFalse();
        }

        @Test
        @DisplayName("Find All Orders with Pagination")
        void findAllOrdersWithPagination() {
            // Arrange
            Member member1 = createMember("member1");
            Member member2 = createMember("member2");
            WrappingPaper wrappingPaper = createWrappingPaper("Standard Wrapping", 3000);
            OrderStatus orderStatus = createOrderStatus("Completed");

            Order order1 = createOrder(
                    member1,
                    wrappingPaper,
                    orderStatus,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            Order order2 = createOrder(
                    member2,
                    wrappingPaper,
                    orderStatus,
                    "67890",
                    "789 Pine St",
                    "Floor 3"
            );

            Pageable pageable = PageRequest.of(0, 10, Sort.by("orderId").ascending());

            // Act
            Page<Order> ordersPage = orderRepository.findAll(pageable);

            // Assert
            assertThat(ordersPage.getTotalElements()).isEqualTo(2);
            List<Order> orders = ordersPage.getContent();
            assertThat(orders).containsExactly(order1, order2);
        }

        @Test
        @DisplayName("Find Orders by Member ID with Pagination")
        void findOrdersByMemberIdWithPagination() {
            // Arrange
            Member member1 = createMember("member1");
            Member member2 = createMember("member2");
            WrappingPaper wrappingPaper = createWrappingPaper("Standard Wrapping", 3000);
            OrderStatus orderStatus = createOrderStatus("Completed");

            Order order1 = createOrder(
                    member1,
                    wrappingPaper,
                    orderStatus,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            Order order2 = createOrder(
                    member1,
                    wrappingPaper,
                    orderStatus,
                    "67890",
                    "789 Pine St",
                    "Floor 3"
            );

            Order order3 = createOrder(
                    member2,
                    wrappingPaper,
                    orderStatus,
                    "11111",
                    "222 Maple St",
                    "Apt 5"
            );

            Pageable pageable = PageRequest.of(0, 10, Sort.by("orderId").ascending());

            // Act
            Page<Order> member1OrdersPage = orderRepository.findByMember_MemberId("member1", pageable);
            Page<Order> member2OrdersPage = orderRepository.findByMember_MemberId("member2", pageable);
            Page<Order> nonExistingMemberOrdersPage = orderRepository.findByMember_MemberId("nonexistent", pageable);

            // Assert
            assertThat(member1OrdersPage.getTotalElements()).isEqualTo(2);
            List<Order> member1Orders = member1OrdersPage.getContent();
            assertThat(member1Orders).containsExactly(order1, order2);

            assertThat(member2OrdersPage.getTotalElements()).isEqualTo(1);
            List<Order> member2Orders = member2OrdersPage.getContent();
            assertThat(member2Orders).containsExactly(order3);

            assertThat(nonExistingMemberOrdersPage.getTotalElements()).isEqualTo(0);
            List<Order> nonexistentOrders = nonExistingMemberOrdersPage.getContent();
            assertThat(nonexistentOrders).isEmpty();
        }

        @Test
        @DisplayName("Find Order by Order ID")
        void findOrderByOrderId() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Premium Wrapping", 5000);
            OrderStatus orderStatus = createOrderStatus("Processing");

            Order order = createOrder(
                    member,
                    wrappingPaper,
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            // Act
            Optional<OrderProjection> projectionOpt = orderRepository.findByOrderId(order.getOrderId());

            // Assert
            assertThat(projectionOpt).isPresent();
            OrderProjection projection = projectionOpt.get();
            assertThat(projection.getOrderId()).isEqualTo(order.getOrderId());
            assertThat(projection.getMemberId()).isEqualTo("member123");
            assertThat(projection.getOrderPrice()).isEqualTo(50000);
            assertThat(projection.getWrappingPaperName()).isEqualTo("Premium Wrapping");
            assertThat(projection.getOrderStatusName()).isEqualTo("Processing");
            assertThat(projection.getZoneCode()).isEqualTo("12345");
            assertThat(projection.getAddress()).isEqualTo("123 Main St");
            assertThat(projection.getAddressDetail()).isEqualTo("Apt 101");
        }

        @Test
        @DisplayName("Find Order by Order ID - Not Found")
        void findOrderByOrderId_NotFound() {
            // Arrange
            Long nonExistingOrderId = 999L;

            // Act
            Optional<OrderProjection> projectionOpt = orderRepository.findByOrderId(nonExistingOrderId);

            // Assert
            assertThat(projectionOpt).isNotPresent();
        }
    }

    @Nested
    @DisplayName("OrderRepository Custom Query Methods")
    class OrderRepositoryCustomQueryTests {

        @Test
        @DisplayName("Find Order by Order ID using Custom Query")
        void findByOrderId_CustomQuery() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Standard Wrapping", 3000);
            OrderStatus orderStatus = createOrderStatus("Completed");

            Order order = createOrder(
                    member,
                    wrappingPaper,
                    orderStatus,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            // Act
            Optional<OrderProjection> projectionOpt = orderRepository.findByOrderId(order.getOrderId());

            // Assert
            assertThat(projectionOpt).isPresent();
            OrderProjection projection = projectionOpt.get();
            assertThat(projection.getOrderId()).isEqualTo(order.getOrderId());
            assertThat(projection.getMemberId()).isEqualTo("member123");
            assertThat(projection.getOrderPrice()).isEqualTo(50000);
            assertThat(projection.getWrappingPaperName()).isEqualTo("Standard Wrapping");
            assertThat(projection.getOrderStatusName()).isEqualTo("Completed");
            assertThat(projection.getZoneCode()).isEqualTo("54321");
            assertThat(projection.getAddress()).isEqualTo("456 Elm St");
            assertThat(projection.getAddressDetail()).isEqualTo("Suite 202");
        }

        @Test
        @DisplayName("Find Order by Order ID using Custom Query - Not Found")
        void findByOrderId_CustomQuery_NotFound() {
            // Arrange
            Long nonExistingOrderId = 999L;

            // Act
            Optional<OrderProjection> projectionOpt = orderRepository.findByOrderId(nonExistingOrderId);

            // Assert
            assertThat(projectionOpt).isNotPresent();
        }
    }

    @Nested
    @DisplayName("OrderRepository Relationships and Constraints")
    class OrderRepositoryRelationshipTests {

        @Test
        @DisplayName("Cascade Save OrderBooks when Saving Order")
        @Transactional
        @Rollback
        void cascadeSaveOrderBooks() {
            // Arrange
            Member member = createMember("member123");
            WrappingPaper wrappingPaper = createWrappingPaper("Premium Wrapping", 5000);
            OrderStatus orderStatus = createOrderStatus("Processing");

            Order order = createOrder(
                    member,
                    wrappingPaper,
                    orderStatus,
                    "12345",
                    "123 Main St",
                    "Apt 101"
            );

            Book book1 = createBook(1L, "Book One");
            Book book2 = createBook(2L, "Book Two");

            OrderBook orderBook1 = createOrderBook(order, book1, 2, 1L);
            OrderBook orderBook2 = createOrderBook(order, book2, 3, 1L);

            // Act
            Optional<Order> foundOrderOpt = orderRepository.findById(order.getOrderId());

            // Assert
            assertThat(foundOrderOpt).isPresent();
            Order foundOrder = foundOrderOpt.get();

            List<OrderBook> orderBooks = foundOrder.getOrderBooks();
            assertThat(orderBooks).hasSize(2).extracting(OrderBook::getBook).containsExactlyInAnyOrder(book1, book2);
        }
    }
}