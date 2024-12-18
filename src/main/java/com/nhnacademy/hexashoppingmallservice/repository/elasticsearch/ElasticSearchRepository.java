package com.nhnacademy.hexashoppingmallservice.repository.elasticsearch;

import com.nhnacademy.hexashoppingmallservice.document.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<Book, Long> {
    List<Book> findAllByOrderByBookSellCountDesc(Pageable pageable);
}
