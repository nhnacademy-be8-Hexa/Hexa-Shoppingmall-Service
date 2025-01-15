package com.nhnacademy.hexashoppingmallservice.repository.book.querydsl;

import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepositoryCustom {
    Page<Book> findBooksByAuthorNameLike(String authorName, Pageable pageable);
    Page<Book> findAllOrderByReviewCountDesc(Pageable pageable);
    Page<Book> findBooksByCategoryIds(List<Long> categoryIds, Pageable pageable);
    Page<Book> findBooksOrderByLikeCountDesc(Pageable pageable);
    Page<Book> findBooksByTagName(String tagName, Pageable pageable);
    List<Author> findAuthorsByBookId(Long bookId);
    long countByCategoryIds(List<Long> categoryIds);
    long countByAuthorName(String authorName);
}
