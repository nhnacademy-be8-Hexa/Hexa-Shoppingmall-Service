package com.nhnacademy.hexashoppingmallservice.entity.order;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
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

    public static OrderBook of(Order order, Book book, Integer orderBookAmount, Long couponId) {
        return OrderBook.builder().book(book).order(order).orderBookAmount(orderBookAmount).couponId(couponId).build();
    }
}
