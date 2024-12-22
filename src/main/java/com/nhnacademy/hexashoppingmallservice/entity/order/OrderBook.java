package com.nhnacademy.hexashoppingmallservice.entity.order;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class OrderBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long orderBookId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private Integer orderBookAmount;

    @Column(columnDefinition = "bigint")
    private Long couponId;

    @Builder
    private OrderBook(Order order, Book book, Integer orderBookAmount, Long couponId) {
        this.order=order;
        this.book=book;
        this.orderBookAmount=orderBookAmount;
        this.couponId=couponId;
    }

    public static OrderBook of(Order order, Book book, Integer orderBookAmount, Long couponId) {
        return builder()
                .order(order)
                .book(book)
                .orderBookAmount(orderBookAmount)
                .couponId(couponId)
                .build();
    }
}
