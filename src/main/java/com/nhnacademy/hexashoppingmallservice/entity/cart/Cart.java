package com.nhnacademy.hexashoppingmallservice.entity.cart;

import com.nhnacademy.hexashoppingmallservice.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.awt.print.Book;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    @Setter
    @Column(nullable = false)
    private Integer cartAmount;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;



}
