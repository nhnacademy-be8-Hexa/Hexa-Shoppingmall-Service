package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Like(Book book, Member member) {
        this.book = book;
        this.member = member;
    }

    public static Like of(Book book, Member member) {
        return Like.builder()
                .book(book)
                .member(member)
                .build();
    }
}
