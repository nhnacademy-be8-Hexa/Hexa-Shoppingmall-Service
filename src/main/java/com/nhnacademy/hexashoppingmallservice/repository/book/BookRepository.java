package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 초기 페이지 도서 목록 - 주문 수 기준 정렬
    Page<Book> findAllByOrderByBookAmountDesc(Pageable pageable);

    // 초기 페이지 도서 목록 - 페이지 뷰 기준 정렬
    Page<Book> findAllByOrderByBookViewDesc(Pageable pageable);

    // 초기 페이지 도서 목록 - 베스트셀러 (판매량 기준 정렬)
    Page<Book> findAllByOrderByBookSellCountDesc(Pageable pageable);

    // 초기 페이지 도서 목록 - 좋아요 수
    @Query("SELECT b FROM Book b ORDER BY b.bookView DESC")
    List<Book> findTopLikedBooks();


    // 특정 카테고리와 하위 카테고리를 포함하여 도서 목록 조회
    @Query("""
        SELECT b
        FROM Book b
        JOIN b.bookCategories bc
        JOIN bc.category c
        WHERE c.categoryId = :categoryId OR c.parentCategory.categoryId = :categoryId
    """)
    Page<Book> findBooksByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    // 도서명 검색
    Page<Book> findAllByBookTitle(String bookTitle,Pageable pageable);

    // 도서 설명 검색
    Page<Book> findAllByBookDescription(String bookDescription, Pageable pageable);

    // 도서 ISBN 검색
    Optional<Book> findByBookISBN(Long isbn);

    // 저자 이름으로 도서 검색
    @Query("""
        SELECT b
        FROM Book b
        JOIN b.bookAuthors ba
        JOIN ba.author a
        WHERE LOWER(a.authorName) LIKE LOWER(CONCAT('%', :authorName, '%'))
    """)
    Page<Book> findBooksByAuthorName(@Param("authorName") String authorName, Pageable pageable);


    // 수정

    // ISBN 중복 확인
    @Query("SELECT COUNT(b) > 0 FROM Book b WHERE b.bookISBN = :isbn AND b.bookId <> :bookId")
    boolean existsByBookISBNAndNotAndBookId(@Param("bookISBN") Long bookISBN, @Param("bookId") Long bookId);

    // ISBN 수정
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.bookISBN = :bookISBN WHERE b.bookId = :bookId")
    void updateBookIsbn(@Param("bookId") Long bookId, @Param("bookISBN") Long bookISBN);

    // 도서 제목 수정
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.bookTitle = :bookTitle WHERE b.bookId = :bookId")
    void updateBookTitle(@Param("bookId") Long bookId, @Param("bookTitle") String bookTitle);

    // 도서 가격 수정
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.bookPrice = :bookPrice WHERE b.bookId = :bookId")
    void updateBookPrice(@Param("bookId") Long bookId, @Param("bookPrice") int bookPrice);


    // 상세페이지에서 도서에 포함되는 정보 가져오기(저자, 출판사, 책 설명, 판매가 등)
}