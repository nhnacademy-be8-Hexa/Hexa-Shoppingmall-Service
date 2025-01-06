package com.nhnacademy.hexashoppingmallservice.repository.elasticsearch;

import com.nhnacademy.hexashoppingmallservice.document.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<Book, Long> {

}
