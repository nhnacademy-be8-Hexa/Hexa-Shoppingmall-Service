package com.nhnacademy.hexashoppingmallservice.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.google.common.collect.Lists;
import com.nhnacademy.hexashoppingmallservice.document.Book;
import com.nhnacademy.hexashoppingmallservice.dto.book.SearchBookDTO;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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


//    public void removeCategoriesFromBooksByCategoryName(String categoryName) {
//        try {
//            Map<String, JsonData> params = new HashMap<>();
//            params.put("categoryName", JsonData.of(categoryName));
//
//            UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest.Builder()
//                    .index("book31")
//                    .query(q -> q
//                            .term(t -> t
//                                    .field("categoryNames.keyword")
//                                    .value(v -> v.stringValue(categoryName))
//                            )
//                    )
//                    .script(s -> s
//                            .inline(i -> i
//                                    .source("""
//                                                if (ctx._source.categoryNames != null) {
//                                                    ctx._source.categoryNames.removeIf(category -> category == params.categoryName);
//                                                }
//                                            """)
//                                    .params(params)
//                            )
//                    )
//                    .build();
//
//            elasticsearchClient.updateByQuery(updateByQueryRequest);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
                                                            "tags.tagName^2"
                                                    ))
                                            )

                                    )
                                    .should(shouldQuery -> shouldQuery
                                            .term(t -> t
                                                    .field("bookIsbn")
                                                    .value(search)
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
            throw new RuntimeException(e);
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
                                                    .fields(Lists.newArrayList(
                                                            "bookTitle^10",
                                                            "authors.authorName^3",
                                                            "tags.tagName^2"
                                                    ))
                                            )
                                    )
                                    .should(shouldQuery -> shouldQuery
                                            .term(t -> t
                                                    .field("bookIsbn")
                                                    .value(search)
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
            throw new RuntimeException(e);
        }
    }


    public List<SearchBookDTO> searchBooksByTitle(String title, Pageable pageable) {
        try {
            int from = pageable.getPageNumber() * pageable.getPageSize();
            int size = pageable.getPageSize();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
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
            throw new RuntimeException(e);
        }
    }


    public List<SearchBookDTO> searchBooksByAuthor(String author, Pageable pageable) {
        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
                    .query(q -> q
                            .match(m -> m
                                    .field("authors.authorName")
                                    .query(author)
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
            throw new RuntimeException(e);
        }
    }

    public List<SearchBookDTO> searchBooksByDescription(String description, Pageable pageable) {
        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
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
            throw new RuntimeException(e);
        }
    }

    public List<SearchBookDTO> searchBooksByTag(String tag, Pageable pageable) {
        int from = pageable.getPageNumber() * pageable.getPageSize();
        int size = pageable.getPageSize();

        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
                    .query(q -> q
                            .bool(b -> b
                                    .should(s -> s
                                            .match(m -> m
                                                    .field("tags.tagName")
                                                    .query(tag)
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
            throw new RuntimeException(e);
        }
    }

    public List<SearchBookDTO> searchBooksByIsbn(String isbn) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("hexa")
                    .query(q -> q
                            .term(t -> t
                                    .field("bookIsbn")
                                    .value(isbn)
                            )
                    )
                    .build();

            SearchResponse<SearchBookDTO> searchResponse =
                    elasticsearchClient.search(searchRequest, SearchBookDTO.class);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

