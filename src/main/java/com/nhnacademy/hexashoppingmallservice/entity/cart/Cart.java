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
    private int cartId;

    @Setter
    @Column(nullable = false)
    private int cartAmount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


//    @Builder
//    private Cart(int cartId, int cartAmount) {
//        this.cartId = cartId;
//        this.cartAmount = cartAmount;
//    }
//
//    public static Cart of(int cartId, int cartAmount, Member member, Book book) {
//
//    }


}
