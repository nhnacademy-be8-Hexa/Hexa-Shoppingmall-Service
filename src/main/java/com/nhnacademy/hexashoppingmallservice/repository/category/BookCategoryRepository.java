package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    // bookId를 이용해 해당 책에 연결된 카테고리를 가져오는 메소드
    List<BookCategory> findByBook_BookId(Long bookId);

}
