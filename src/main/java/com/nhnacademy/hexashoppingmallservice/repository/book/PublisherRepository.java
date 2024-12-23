package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    boolean existsByPublisherName(String publisherName);

    Publisher findByPublisherName(String publisherName);
}
