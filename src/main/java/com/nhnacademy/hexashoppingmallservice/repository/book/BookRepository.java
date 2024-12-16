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
    //todo 주문 수는 주문이랑 조인해서 카운트

    // 초기 페이지 도서 목록 - 페이지 뷰 기준 정렬
    Page<Book> findAllByOrderByBookViewDesc(Pageable pageable);

    // 초기 페이지 도서 목록 - 베스트셀러 (판매량 기준 정렬)
    Page<Book> findAllByOrderByBookSellCountDesc(Pageable pageable);

    // 초기 페이지 도서 목록 - 좋아요 수
    @Query("""
            SELECT b
            FROM Book b
            JOIN Like l
            ON l.book = b
            GROUP BY b.bookId
            ORDER BY COUNT(l.likeId)
            DESC
            """)
    List<Book> findTopLikedBooks();
    //todo book_view는 조회수임. limit나 페이징 없이 리스트로 받으면 모든책이 넘어와서 큰일 날것임.
    // 좋아요 수는 멤버와 다대다인 좋아요 테이블 조인해서 카운트를 한것을 기준으로 정렬 해야함 (조금 복잡)

    // 특정 카테고리와 하위 카테고리를 포함하여 도서 목록 조회
    @Query("""
            SELECT b
            FROM Book b
            JOIN BookCategory bc ON bc.book = b
            JOIN Category  c ON bc.category = c
            WHERE c.categoryId = :categoryId OR c.parentCategory.categoryId = :categoryId
            """)
    Page<Book> findBooksByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    //todo bookCategories 리스트 이제 없어질테니 수정해야함.

    // 도서명 검색
    @Query("""
           SELECT b
           FROM Book b
           WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%s', :bookTitle, '%s'))
           """)
    Page<Book> findAllByBookTitleContaining(String bookTitle,Pageable pageable);
    // todo 이렇게 하면 정확히 일치하는 제목만 찾음. contain 등을 써봐야 할듯

    // 도서 설명 검색
    @Query("""
            SELECT b
            FROM Book b
            WHERE LOWER(b.bookDescription) LIKE LOWER(CONCAT('%s', :bookDescription, '%s'))
            """)
    Page<Book> findAllByBookDescriptionContaining(String bookDescription, Pageable pageable);
    // todo 이거도

    // 도서 ISBN 검색
    Optional<Book> findByBookISBN(Long isbn);

    // 저자 이름으로 도서 검색
    @Query("""
        SELECT b
        FROM Book b
        JOIN BookAuthor ba ON ba.book = b
        JOIN Author a ON ba.author = a
        WHERE lower(a.authorName) LIKE LOWER(concat('%s', :authorName, '%s'))
        """)
    Page<Book> findBooksByAuthorName(@Param("authorName") String authorName, Pageable pageable);


    // 수정 쿼리

    // 10. ISBN 중복 확인
    @Query("""
        SELECT COUNT(b) > 0
        FROM Book b
        WHERE b.bookISBN = :bookISBN AND b.bookId <> :bookId
    """)
    boolean existsByBookISBNAndNotBookId(@Param("bookISBN") Long bookISBN, @Param("bookId") Long bookId);

    // 11. ISBN 수정
    @Transactional
    @Modifying
    @Query("""
        UPDATE Book b
        SET b.bookISBN = :bookISBN
        WHERE b.bookId = :bookId
    """)
    void updateBookIsbn(@Param("bookId") Long bookId, @Param("bookISBN") Long bookISBN);

    // 12. 도서 제목 수정
    @Transactional
    @Modifying
    @Query("""
        UPDATE Book b
        SET b.bookTitle = :bookTitle
        WHERE b.bookId = :bookId
    """)
    void updateBookTitle(@Param("bookId") Long bookId, @Param("bookTitle") String bookTitle);

    // 13. 도서 가격 수정
    @Transactional
    @Modifying
    @Query("""
        UPDATE Book b
        SET b.bookPrice = :bookPrice
        WHERE b.bookId = :bookId
    """)
    void updateBookPrice(@Param("bookId") Long bookId, @Param("bookPrice") int bookPrice);


    // 상세페이지에서 도서에 포함되는 정보 가져오기(저자, 출판사, 책 설명, 판매가 등)
}
