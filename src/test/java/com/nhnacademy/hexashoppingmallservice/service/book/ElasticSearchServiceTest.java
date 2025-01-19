package com.nhnacademy.hexashoppingmallservice.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.nhnacademy.hexashoppingmallservice.document.BookStatus;
import com.nhnacademy.hexashoppingmallservice.document.Publisher;
import com.nhnacademy.hexashoppingmallservice.dto.book.SearchBookDTO;
import com.nhnacademy.hexashoppingmallservice.exception.elasticsearch.ElasticsearchException;
import com.nhnacademy.hexashoppingmallservice.service.elasticsearch.ElasticSearchService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchServiceTest {
    @Mock
    private ElasticsearchClient elasticsearchClient;
    
    @InjectMocks
    private ElasticSearchService elasticSearchService;

    private Pageable pageable;
    private SearchBookDTO searchBookDTO;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);
        Publisher publisher = Publisher.of(1L, "Test Publisher");
        BookStatus bookStatus = BookStatus.of(1L, "Test Book Status");
        searchBookDTO = new SearchBookDTO(1L, "Test Book", "Test Description", publisher, bookStatus, 1234567890L,
                "2022-01-01", 2000, 1500, true, 100, 10, 50L);
    }

    @Test
    public void testSearchBooks() throws IOException {
        Hit<SearchBookDTO> mockHit = mock(Hit.class);
        when(mockHit.source()).thenReturn(searchBookDTO);

        HitsMetadata<SearchBookDTO> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(Arrays.asList(mockHit));

        SearchResponse<SearchBookDTO> searchResponse = mock(SearchResponse.class);
        when(searchResponse.hits()).thenReturn(hitsMetadata);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(SearchBookDTO.class)))
                .thenReturn(searchResponse);

        List<SearchBookDTO> result = elasticSearchService.searchBooks("Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getBookTitle());
    }

    @Test
    public void testGetTotal() throws IOException {
        SearchResponse<SearchBookDTO> searchResponse = mock(SearchResponse.class);

        HitsMetadata<SearchBookDTO> hitsMetadata = mock(HitsMetadata.class);
        when(searchResponse.hits()).thenReturn(hitsMetadata);

        TotalHits totalHits = mock(TotalHits.class);
        when(totalHits.value()).thenReturn(100L);
        when(hitsMetadata.total()).thenReturn(totalHits);

        when(elasticsearchClient.search(any(SearchRequest.class), eq(SearchBookDTO.class)))
                .thenReturn(searchResponse);

        long total = elasticSearchService.getTotal("Test");

        assertEquals(100L, total);
    }

    @Test
    public void testSearchBooksThrowsException() throws IOException {
        when(elasticsearchClient.search(any(SearchRequest.class), any()))
                .thenThrow(new IOException("Test Exception"));

        ElasticsearchException thrownException = assertThrows(ElasticsearchException.class, () ->
                elasticSearchService.searchBooks("Test", pageable));

        assertTrue(thrownException.getMessage().contains("Error occurred while searching books"));
    }

    @Test
    public void testGetTotalThrowsException() throws IOException {
        when(elasticsearchClient.search(any(SearchRequest.class), any()))
                .thenThrow(new IOException("Test Exception"));

        ElasticsearchException thrownException = assertThrows(ElasticsearchException.class, () ->
                elasticSearchService.getTotal("Test"));

        assertTrue(thrownException.getMessage().contains("Error occurred while retrieving total count"));
    }

}
