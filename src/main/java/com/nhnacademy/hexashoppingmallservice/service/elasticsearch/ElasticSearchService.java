package com.nhnacademy.hexashoppingmallservice.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.InlineScript;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.ScriptSortType;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.nhnacademy.hexashoppingmallservice.document.Book;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public Book saveBook(Book book) {
        return elasticSearchRepository.save(book);
    }


    public List<Book> searchBooksByTitle(String title) {
        try {
            InlineScript inlineScript = new InlineScript.Builder()
                    .source("doc['bookTitle.keyword'].value.length()")
                    .lang("painless")
                    .build();

            Script script = new Script.Builder()
                    .inline(inlineScript)
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("book31")
                    .query(q -> q
                            .bool(b -> b
                                    .should(shouldQuery -> shouldQuery
                                            .match(m -> m
                                                    .field("bookTitle")
                                                    .query(title)
                                            )
                                    )
                            )
                    )
                    .sort(s -> s
                            .script(sr -> sr
                                    .type(ScriptSortType.Number)
                                    .script(script)
                                    .order(SortOrder.Asc)
                            )
                    )
                    .build();

            SearchResponse<Book> searchResponse = elasticsearchClient.search(searchRequest, Book.class);

            List<Book> books = new ArrayList<>();
            searchResponse.hits().hits().forEach(hit -> {
                books.add(hit.source());
            });

            return books;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Book> searchBooksByAuthor(String author) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("book31")
                    .query(q -> q
                            .nested(n -> n
                                    .path("authors")
                                    .query(q1 -> q1
                                            .match(m -> m
                                                    .field("authors.authorName")
                                                    .query(author)
                                            )
                                    )
                            )
                    )
                    .build();

            SearchResponse<Book> searchResponse = elasticsearchClient.search(searchRequest, Book.class);

            List<Book> books = new ArrayList<>();
            searchResponse.hits().hits().forEach(hit -> {
                books.add(hit.source());
            });

            return books;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> searchBooksByDescription(String description) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("book31")
                    .query(q -> q
                            .bool(b -> b
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("bookDescription")
                                                    .query(description)
                                            )
                                    )
                            )
                    )
                    .build();

            SearchResponse<Book> searchResponse = elasticsearchClient.search(searchRequest, Book.class);

            List<Book> books = new ArrayList<>();
            searchResponse.hits().hits().forEach(hit -> {
                books.add(hit.source());
            });

            return books;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> searchBooksByTag(String tag) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("book31")
                    .query(q -> q
                            .bool(b -> b
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("tagName")
                                                    .query(tag)
                                            )
                                    )
                            )
                    )
                    .build();

            // 검색 요청 실행
            SearchResponse<Book> searchResponse = elasticsearchClient.search(searchRequest, Book.class);

            // 검색 결과를 리스트로 변환
            List<Book> books = new ArrayList<>();
            searchResponse.hits().hits().forEach(hit -> {
                books.add(hit.source());
            });

            return books;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

