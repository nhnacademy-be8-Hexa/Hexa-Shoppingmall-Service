package com.nhnacademy.hexashoppingmallservice.entity.cart;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @NotNull
    @Setter
    @Column(nullable = false)
    private Integer cartAmount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Builder
    private Cart(Integer cartAmount, Member member, Book book) {
        this.cartAmount = cartAmount;
        this.member = member;
        this.book = book;
    }

    public static Cart of(Integer cartAmount, Member member, Book book) {
        return builder()
                .cartAmount(cartAmount)
                .member(member)
                .book(book)
                .build();
    }





}
