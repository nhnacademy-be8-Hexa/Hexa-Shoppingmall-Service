package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
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
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final WrappingPaperRepository wrappingPaperRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderBookRepository orderBookRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long createOrder(OrderRequestDTO orderRequestDTO, List<Long> bookIds, List<Integer> amounts, Long couponId) {
        String memberId = orderRequestDTO.getMemberId();
        Member member = null;

        if (Objects.nonNull(memberId)) {
            if (!memberRepository.existsById(memberId) && !memberId.isEmpty()) {
                throw new MemberNotFoundException(
                        "Member ID %s not found".formatted(memberId));
            }
            member = memberRepository.findById(memberId).orElseThrow();
        }

        Long wrappingPaperId = orderRequestDTO.getWrappingPaperId();
        WrappingPaper wrappingPaper = null;

        if (Objects.nonNull(wrappingPaperId)) {
            if (!wrappingPaperRepository.existsById(wrappingPaperId)) {
                throw new WrappingPaperNotFoundException(
                        "WrappingPaper ID %d not found".formatted(wrappingPaperId));
            }
            wrappingPaper = wrappingPaperRepository.findById(wrappingPaperId).orElseThrow();
        }

        Long orderStatusId = orderRequestDTO.getOrderStatusId();
        if (!orderStatusRepository.existsById(orderStatusId)) {
            throw new OrderStatusNotFoundException(
                    "OrderStatus ID %d is not found".formatted(orderStatusId));
        }

        if (bookRepository.countByBookIdIn(bookIds) == 0) {
            throw new BookNotFoundException("Book IDs aren't found");
        }

        OrderStatus orderStatus = orderStatusRepository.findById(orderStatusId).orElseThrow();

        Order order = Order.of(
                member,
                orderRequestDTO.getOrderPrice(),
                wrappingPaper,
                orderStatus,
                orderRequestDTO.getZoneCode(),
                orderRequestDTO.getAddress(),
                orderRequestDTO.getAddressDetail()
        );

        Long savedId = orderRepository.save(order).getOrderId();

        if (bookIds.size() != amounts.size()) {
            throw new ParameterNotEnouthException("Parameters not enough");
        }

        int index = 0;

        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId).orElseThrow(
                    () -> new BookNotFoundException("Book ID %d not found".formatted(bookId))
            );

            OrderBook orderBook = OrderBook.of(order, book, amounts.get(index), couponId);
            orderBookRepository.save(orderBook);
            index++;
        }

        return savedId;
    }

    @Transactional(readOnly = true)
    public List<OrderProjection> getAllOrders(Pageable pageable) {
        return orderRepository.findAllBy(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<OrderProjection> getOrdersByMemberId(String memberId, Pageable pageable) {
        return orderRepository.findByMember_MemberId(memberId, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public OrderProjection getOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order ID %d not found".formatted(orderId));
        }
        return orderRepository.findByOrderId(orderId).get();
    }

    @Transactional(readOnly = true)
    public Long getAmount(Long orderId, Long bookId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order ID %d not found".formatted(orderId));
        }
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d not found".formatted(bookId));
        }
        return orderBookRepository.sumOrderBookAmountByOrderIdAndBookId(orderId, bookId);
    }

    @Transactional
    public void updateOrder(Long orderId, OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("%s", orderId))
        );
        
        updateIfNotNull(orderRequestDTO.getOrderPrice(), order::setOrderPrice);
        updateIfNotNull(orderRequestDTO.getZoneCode(), order::setZoneCode);
        updateIfNotNull(orderRequestDTO.getAddressDetail(), order::setAddressDetail);

        updateIfNotNull(orderRequestDTO.getAddress(), order::setAddress);

        OrderStatus orderStatus = orderStatusRepository.findById(orderRequestDTO.getOrderStatusId()).orElse(null);
        if (Objects.isNull(orderStatus)) {
            throw new OrderStatusNotFoundException(
                    "OrderStatus ID %s is not found".formatted(orderRequestDTO.getOrderStatusId()));
        }
        order.setOrderStatus(orderStatus);


        Long wrappingPaperId = orderRequestDTO.getWrappingPaperId();
        WrappingPaper wrappingPaper = null;

        if (Objects.nonNull(wrappingPaperId)) {
            if (!wrappingPaperRepository.existsById(wrappingPaperId)) {
                throw new WrappingPaperNotFoundException(
                        "WrappingPaper ID %s not found".formatted(wrappingPaperId));
            }
            wrappingPaper = wrappingPaperRepository.findById(wrappingPaperId).orElseThrow();
            order.setWrappingPaper(wrappingPaper);
        }
    }

    // 특정 도서에 대해 주문이 존재하는지 조회
    @Transactional(readOnly = true)
    public Boolean checkOrderBook(String memberId, Long bookId) {
        return orderBookRepository.existsByOrder_Member_MemberIdAndBook_BookId(memberId, bookId);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }


    public Boolean existsOrderIdAndMember_MemberId(Long orderId, String memberMemberId){
        return orderRepository.existsByOrderIdAndMember_MemberId(orderId,memberMemberId);
    }

    public Long countAllByMember_MemberId(String memberId) {
        return orderRepository.countAllByMember_MemberId(memberId);
    }
}
