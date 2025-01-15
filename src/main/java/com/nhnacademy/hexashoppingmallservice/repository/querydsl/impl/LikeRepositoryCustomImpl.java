package com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl;


import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import com.nhnacademy.hexashoppingmallservice.entity.book.QBook;
import com.nhnacademy.hexashoppingmallservice.entity.book.QLike;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.LikeRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikeRepositoryCustomImpl extends QuerydslRepositorySupport implements LikeRepositoryCustom {

    public LikeRepositoryCustomImpl() {
        super(Like.class);
    }

    @Override
    public List<Book> findBooksLikedByMemberId(String memberId) {
        QLike like = QLike.like;
        QBook book = QBook.book;

        return from(like)
                .join(book).on(like.book.eq(book))
                .where(like.member.memberId.eq(memberId))
                .select(book)
                .fetch();
    }
}
