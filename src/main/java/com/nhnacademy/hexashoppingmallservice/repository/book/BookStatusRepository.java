package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookStatusRepository extends JpaRepository<BookStatus,Long> {
}
