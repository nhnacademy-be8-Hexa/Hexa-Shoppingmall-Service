package com.nhnacademy.hexashoppingmallservice.repository.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    // 책 id 로 태그 리스트 조회
    List<Tag> findTagsByBook_BookId(Long bookId);

    // 태그 id 로 책 리스트 조회
    Page<Book> findBooksByTag_TagId(Long tagId, Pageable pageable);

    // 이미 존재하는 책,태그 짝 검사
    boolean existsByBook_BookIdAndTag_TagId(Long bookId, Long tagId);

    List<BookTag> findByTag_TagId(Long tagId);
}
