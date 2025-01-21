package com.nhnacademy.hexashoppingmallservice.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.common.collect.Lists;
import com.nhnacademy.hexashoppingmallservice.dto.book.SearchBookDTO;
import com.nhnacademy.hexashoppingmallservice.exception.elasticsearch.ElasticsearchException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public List<SearchBookDTO> searchBooks(String search, Pageable pageable) {
        try {
            int from = pageable.getPageNumber() * pageable.getPageSize();
            int size = pageable.getPageSize();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
                    .query(query -> query
                            .bool(boolQuery -> boolQuery
                                    .should(shouldQuery -> shouldQuery
                                            .multiMatch(mm -> mm
                                                    .query(search)
                                                    .operator(Operator.And)
                                                    .fields(Lists.newArrayList(
                                                            "bookTitle^10",
                                                            "authors.authorName^3",
                                                            "bookTags.tagName^2"
                                                    ))
                                            )

                                    )
                                    .should(shouldQuery -> shouldQuery
                                            .term(t -> t
                                                    .field("bookIsbn")
                                                    .value(search)
                                            )
                                    )
                                    .mustNot(mustNotQuery -> mustNotQuery
                                            .term(t -> t
                                                    .field("bookStatus.bookStatus")
                                                    .value("판매종료")
                                            )
                                    )
                            )
                    )
                    .from(from)
                    .size(size)
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("_score")
                                    .order(SortOrder.Desc)
                            )
                    )
                    .build();

            SearchResponse<SearchBookDTO> searchResponse =
                    elasticsearchClient.search(searchRequest, SearchBookDTO.class);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new ElasticsearchException(
                    String.format("Error occurred while searching books for query '%s'", search), e);
        }
    }

    public long getTotal(String search) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
                    .query(query -> query
                            .bool(boolQuery -> boolQuery
                                    .should(shouldQuery -> shouldQuery
                                            .multiMatch(mm -> mm
                                                    .query(search)
                                                    .operator(Operator.And)
                                                    .fields(Lists.newArrayList(
                                                            "bookTitle^10",
                                                            "authors.authorName^3",
                                                            "bookTags.tagName^2"
                                                    ))
                                            )
                                    )
                                    .should(shouldQuery -> shouldQuery
                                            .term(t -> t
                                                    .field("bookIsbn")
                                                    .value(search)
                                            )
                                    )
                                    .mustNot(mustNotQuery -> mustNotQuery
                                            .term(t -> t
                                                    .field("bookStatus.bookStatus")
                                                    .value("판매종료")
                                            )
                                    )
                            )
                    )
                    .size(0)
                    .build();

            SearchResponse<SearchBookDTO> searchResponse =
                    elasticsearchClient.search(searchRequest, SearchBookDTO.class);

            return searchResponse.hits().total() != null ? searchResponse.hits().total().value() : 0;
        } catch (IOException e) {
            throw new ElasticsearchException(
                    String.format("Error occurred while retrieving total count for query: '%s'", search), e
            );
        }
    }

}

