package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    // 도서 목록 - 출판사
    @Query("""
        SELECT b
        FROM Book b
        JOIN b.publisher p
        WHERE LOWER(p.publisherName) LIKE LOWER(CONCAT('%', :publisherName, '%'))
    """)
    Page<Book> findBooksByPublisherName(@Param("publisherName") String publisherName, Pageable pageable);

    // 도서 목록 - 도서명
    Page<Book> findByBookTitleLike(@Param("bookTitle")String bookTitle,Pageable pageable);

    // 도서 목록 - 저자 이름
    @Query("""
        SELECT b
        FROM Book b
        JOIN BookAuthor ba ON ba.book = b
        JOIN Author a ON ba.author = a
        WHERE LOWER(a.authorName) LIKE LOWER(CONCAT('%', :authorName, '%'))
    """)
    Page<Book> findBooksByAuthorNameLike(@Param("authorName") String authorName, Pageable pageable);


    // 도서 목록 - 조회수 (내림차순)
    Page<Book> findByOrderByBookViewDesc(Pageable pageable);

    // 도서 목록 - 베스트셀러 (내림차순)
    Page<Book> findByOrderByBookSellCount(Pageable pageable);

    // 도서 목록 - 카테고리 별
    @Query("""
            SELECT bc.book
            FROM BookCategory bc
            WHERE bc.category.categoryId IN :categoryIds
            """)
    Page<Book> findBooksByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    // 도서 목록 - 좋아요 (내림차순)
    @Query("""
        SELECT b
        FROM Book b
        LEFT JOIN Like l ON l.book = b
        GROUP BY b.bookId
        ORDER BY COUNT(l.likeId) DESC
    """)
    Page<Book> findBooksOrderByLikeCountDesc(Pageable pageable);

    // 도서 목록 - 태그
    @Query("""
        SELECT b
        FROM Book b
        JOIN BookTag bt ON bt.book = b
        JOIN Tag t ON bt.tag = t
        WHERE t.tagName = :tagName
    """)
    Page<Book> findBooksByTagName(@Param("tagName") String tagName, Pageable pageable);

    // 도서 목록 - 최신순(출간일 기준)
    Page<Book> findBooksByBookPubDate(Pageable pageable);

    // ISBN 중복확인
    boolean existsByBookIsbn(@Param("bookIsbn") Long bookIsbn);
}
