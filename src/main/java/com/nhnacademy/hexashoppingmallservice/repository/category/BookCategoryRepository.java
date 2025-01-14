package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.BookCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    // bookId를 이용해 해당 책에 연결된 카테고리를 가져오는 메소드
    List<BookCategory> findByBook_BookId(Long bookId);

    List<BookCategory> findByCategory_CategoryId(Long categoryId);

    void deleteByCategory_CategoryIdAndBook_BookId(Long categoryId, Long bookId);

}
