package com.nhnacademy.hexashoppingmallservice.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class BookStatusServiceTest {

    @Mock
    private BookStatusRepository bookStatusRepository;

    @InjectMocks
    private BookStatusService bookStatusService;

    private BookStatus bookStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookStatus = BookStatus.of("판매중");
    }

    @Test
    void testCreateBookStatus() {
        when(bookStatusRepository.save(any(BookStatus.class))).thenReturn(bookStatus);

        BookStatus savedStatus = bookStatusService.createBookStatus(bookStatus);

        assertThat(savedStatus.getBookStatus()).isEqualTo("판매중");
        verify(bookStatusRepository, times(1)).save(any(BookStatus.class));
    }

    @Test
    void testGetAllBookStatus() {
        when(bookStatusRepository.findAll()).thenReturn(Arrays.asList(bookStatus));

        List<BookStatus> bookStatuses = bookStatusService.getAllBookStatus();

        assertThat(bookStatuses).hasSize(1);
        assertThat(bookStatuses.getFirst().getBookStatus()).isEqualTo("판매중");
        verify(bookStatusRepository, times(1)).findAll();
    }

    @Test
    void testGetBookStatus_Success() {
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.of(bookStatus));

        BookStatus foundStatus = bookStatusService.getBookStatus(1L);

        assertThat(foundStatus).isNotNull();
        assertThat(foundStatus.getBookStatus()).isEqualTo("판매중");
        verify(bookStatusRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookStatus_NotFound() {
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.empty());

        BookStatus foundStatus = bookStatusService.getBookStatus(1L);
        assertThat(foundStatus).isNull();

        verify(bookStatusRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteBookStatus() {
        bookStatusService.deleteBookStatus(1L);

        verify(bookStatusRepository, times(1)).deleteById(1L);
    }

    // 수정
    @Test
    void testUpdateBookStatus_Success() {
        BookStatusRequestDTO requestDTO = new BookStatusRequestDTO("판매종료");

        when(bookStatusRepository.findById(1L)).thenReturn(Optional.of(bookStatus));

        BookStatus updatedStatus = bookStatusService.updateBookStatus(1L, requestDTO);

        assertThat(updatedStatus.getBookStatus()).isEqualTo("판매종료");
        verify(bookStatusRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateBookStatus_NotFound() {
        BookStatusRequestDTO requestDTO = new BookStatusRequestDTO("판매종료");

        when(bookStatusRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookStatusService.updateBookStatus(1L, requestDTO));
        assertThat(exception.getMessage()).isEqualTo("id cannot found: 1");

        verify(bookStatusRepository, times(1)).findById(1L);
        verify(bookStatusRepository, never()).save(any(BookStatus.class));
    }
}