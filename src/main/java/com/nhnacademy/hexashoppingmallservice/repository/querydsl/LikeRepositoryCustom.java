package com.nhnacademy.hexashoppingmallservice.repository.querydsl;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;

import java.util.List;

public interface LikeRepositoryCustom {
    List<Book> findBooksLikedByMemberId(String memberId);
}
