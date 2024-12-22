package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderbook")
@RequiredArgsConstructor
public class OrderBookController {

    private final OrderBookService orderBookService;

    /**
     * [POST] /api/orderbook/orders/{orderId}/books/{bookId}
     * - 특정 주문(orderId)에 특정 책(bookId)을 연결하여 OrderBook을 생성
     *
     * @param orderId 주문 ID
     * @param bookId  책 ID
     * @param dto     생성할 OrderBook의 DTO
     * @return HTTP 201 Created
     */
    @PostMapping("/orders/{orderId}/books/{bookId}")
    public ResponseEntity<Void> createOrderBook(
            @PathVariable Long orderId,
            @PathVariable Long bookId,
            @Valid @RequestBody OrderBookRequestDTO dto
    ) {
        orderBookService.createOrderBookbyOrderId(orderId, bookId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * [GET] /api/orderbook
     * - 모든 OrderBook을 페이징/정렬하여 조회
     *
     * @param pageable 페이징 및 정렬 정보
     * @return OrderBookProjection 리스트
     */
    @GetMapping
    public ResponseEntity<List<OrderBookProjection>> getAllOrderBooks(Pageable pageable) {
        List<OrderBookProjection> orderBooks = orderBookService.getAllOrderBooks(pageable);
        return ResponseEntity.ok(orderBooks);
    }

    /**
     * [GET] /api/orderbook/orders/{orderId}
     * - 특정 주문(orderId)에 연결된 OrderBook들을 페이징/정렬하여 조회
     *
     * @param orderId  주문 ID
     * @param pageable 페이징 및 정렬 정보
     * @return OrderBookProjection 리스트
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderBookProjection>> getOrderBooksByOrderId(
            @PathVariable Long orderId,
            Pageable pageable
    ) {
        List<OrderBookProjection> orderBooks = orderBookService.getOrderBooksByOrderId(orderId, pageable);
        return ResponseEntity.ok(orderBooks);
    }

    /**
     * [GET] /api/orderbook/books/{bookId}
     * - 특정 책(bookId)에 연결된 OrderBook들을 페이징/정렬하여 조회
     *
     * @param bookId   책 ID
     * @param pageable 페이징 및 정렬 정보
     * @return OrderBookProjection 리스트
     */
    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<OrderBookProjection>> getOrderBooksByBookId(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        List<OrderBookProjection> orderBooks = orderBookService.getOrderBooksByBookId(bookId, pageable);
        return ResponseEntity.ok(orderBooks);
    }

    /**
     * [PUT] /api/orderbook/{orderBookId}
     * - 특정 OrderBook(orderBookId)을 업데이트
     *
     * @param orderBookId 특정 OrderBook ID
     * @param dto         업데이트할 OrderBookRequestDTO
     * @return HTTP 204 No Content
     */
    @PutMapping("/{orderBookId}")
    public ResponseEntity<Void> updateOrderBook(
            @PathVariable Long orderBookId,
            @Valid @RequestBody OrderBookRequestDTO dto
    ) {
        orderBookService.updateOrderBook(dto, orderBookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [DELETE] /api/orderbook/{orderBookId}
     * - 특정 OrderBook(orderBookId)을 삭제
     *
     * @param orderBookId 특정 OrderBook ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{orderBookId}")
    public ResponseEntity<Void> deleteOrderBook(
            @PathVariable Long orderBookId
    ) {
        orderBookService.deleteOrderBook(orderBookId);
        return ResponseEntity.noContent().build();
    }
}
