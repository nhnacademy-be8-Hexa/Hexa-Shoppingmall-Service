package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderBookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;



    public OrderBookService(OrderBookRepository orderBookRepository, OrderRepository orderRepository, BookRepository bookRepository) {
        this.orderBookRepository = orderBookRepository;
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void createOrderBookbyOrderId(Long orderId, Long bookId, OrderBookRequestDTO orderBookRequestDTO) {

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order ID %s is not Found!".formatted(orderId));
        }
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }


        Order order = orderRepository.findById(orderId).get();
        Book book = bookRepository.findById(bookId).get();


        OrderBook orderBook = OrderBook.of(
                order,
                book,
                orderBookRequestDTO.getOrderBookAmount(),
                orderBookRequestDTO.getCouponId()
        );
        orderBookRepository.save(orderBook);
    }



    //OrderBookId 기준 Read
    @Transactional
    public List<OrderBookProjection> getAllOrderBooks(Pageable pageable) {

        Page<OrderBookProjection> page = orderBookRepository.findAllProjectedBy(pageable);
        return page.getContent();
    }

    //OrderId 기준 Read
    @Transactional
    public List<OrderBookProjection> getOrderBooksByOrderId(Long orderId, Pageable pageable) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order ID %s is not Found!".formatted(orderId));
        }

        Page<OrderBookProjection> page = orderBookRepository.findByOrderOrderId(orderId, pageable);
        return page.getContent();
    }

    // BookId 기준 Read
    @Transactional
    public List<OrderBookProjection> getOrderBooksByBookId(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }

        Page<OrderBookProjection> page = orderBookRepository.findByBookBookId(bookId, pageable);
        return page.getContent();
    }

    @Transactional
    public void updateOrderBook(OrderBookRequestDTO orderBookRequestDTO, Long orderBookId) {
        if (!orderBookRepository.existsById(orderBookId)) {
            throw new OrderBookNotFoundException("OrderBook ID %d is not Found!".formatted(orderBookId));
        }

        OrderBook orderBook = orderBookRepository.findById(orderBookId).get();

        orderBook.setOrderBookAmount(orderBookRequestDTO.getOrderBookAmount());
        orderBook.setCouponId(orderBookRequestDTO.getCouponId());


    }

    @Transactional
    public void deleteOrderBook(Long orderBookId) {
        if (!orderBookRepository.existsById(orderBookId)) {
            throw new OrderBookNotFoundException("OrderBook ID %d is not Found!".formatted(orderBookId));
        }
        orderBookRepository.deleteById(orderBookId);
    }
}