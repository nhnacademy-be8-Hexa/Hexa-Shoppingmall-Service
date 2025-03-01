package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.BookRepositoryCustom;
import feign.Param;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    // 도서 목록 - 출판사
    Page<Book> findByPublisherPublisherNameIgnoreCaseContaining(String publisherName, Pageable pageable);

    // 도서 목록 - 도서명
    Page<Book> findByBookTitleContaining(@Param("bookTitle") String bookTitle, Pageable pageable);

    // 도서 목록 - 저자 이름
//    @Query("""
//                SELECT b
//                FROM Book b
//                JOIN BookAuthor ba ON ba.book = b
//                JOIN Author a ON ba.author = a
//                WHERE LOWER(a.authorName) LIKE LOWER(CONCAT('%', :authorName, '%'))
//            """)
//    Page<Book> findBooksByAuthorNameLike(@Param("authorName") String authorName, Pageable pageable);

    // 리뷰 순 기준으로 도서를 내림차순 정렬
//    @Query("SELECT b FROM Book b LEFT JOIN Review r ON b = r.book GROUP BY b ORDER BY COUNT(r) DESC")
//    Page<Book> findAllOrderByReviewCountDesc(Pageable pageable);

    // 도서 목록 - 카테고리 별
//    @Query("""
//            SELECT bc.book
//            FROM BookCategory bc
//            WHERE bc.category.categoryId IN :categoryIds
//            """)
//    Page<Book> findBooksByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    // 도서 목록 - 좋아요 (내림차순)
//    @Query("""
//                SELECT b
//                FROM Book b
//                LEFT JOIN Like l ON l.book = b
//                GROUP BY b.bookId
//                ORDER BY COUNT(l.likeId) DESC
//            """)
//    Page<Book> findBooksOrderByLikeCountDesc(Pageable pageable);

    // 도서 목록 - 태그
//    @Query("""
//                SELECT b
//                FROM Book b
//                JOIN BookTag bt ON bt.book = b
//                JOIN Tag t ON bt.tag = t
//                WHERE t.tagName = :tagName
//            """)
//    Page<Book> findBooksByTagName(@Param("tagName") String tagName, Pageable pageable);

    // ISBN 중복확인
    boolean existsByBookIsbn(@Param("bookIsbn") Long bookIsbn);

    // 특정 BookId에 대한 Author 리스트 조회
//    @Query("""
//        SELECT a
//        FROM Author a
//        JOIN BookAuthor ba ON ba.author = a
//        WHERE ba.book.bookId = :bookId
//    """)
//    List<Author> findAuthorsByBookId(@Param("bookId") Long bookId);

    long countByBookIdIn(List<Long> bookIds);

    // 도서 제목으로 검색할 때의 도서 수
    long countByBookTitleContaining(String search);

    // 다중 카테고리 ID에 속한 도서 수를 세는 메서드가 필요할 경우:
//    @Query("""
//            SELECT COUNT(DISTINCT bc.book)
//            FROM BookCategory bc
//            WHERE bc.category.categoryId IN :categoryIds
//            """)
//    long countByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    // 출판사명으로 도서 수를 세는 메서드
//    @Query("""
//            SELECT COUNT(b)
//            FROM Book b
//            WHERE b.publisher.publisherName = :publisherName
//            """)
//    long countByPublisherName(@Param("publisherName") String publisherName);

    long countByPublisherPublisherName(String publisherName);


//    /**
//     * 작가명으로 도서 수를 세는 커스텀 쿼리 메서드
//     *
//     * @param authorName 검색할 작가명
//     * @return 해당 작가가 참여한 도서의 수
//     */
//    @Query("""
//            SELECT COUNT(DISTINCT ba.book)
//            FROM BookAuthor ba
//            WHERE ba.author.authorName = :authorName
//            """)
//    long countByAuthorName(@Param("authorName") String authorName);
}
