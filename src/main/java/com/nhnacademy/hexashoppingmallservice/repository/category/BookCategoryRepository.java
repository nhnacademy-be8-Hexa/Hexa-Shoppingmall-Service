package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
