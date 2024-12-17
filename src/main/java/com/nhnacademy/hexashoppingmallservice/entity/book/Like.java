package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    private Member member;

    @Builder
    private Like(Book book, Member member) {
        this.book = book;
        this.member = member;
    }

    public static Like of(Book book, Member member){
        return Like.builder()
                .book(book)
                .member(member)
                .build();
    }

}
