package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.PublisherRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    private Publisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publisher = Publisher.of("Test Publisher");
    }

    @Test
    void testCreatePublisher() {
        when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

        Publisher savedPublisher = publisherService.createPublisher(publisher);

        assertThat(savedPublisher.getPublisherName()).isEqualTo("Test Publisher");
        verify(publisherRepository, times(1)).save(any(Publisher.class));
    }

    @Test
    void testGetAllPublisher() {
        // Publisher 객체 생성 및 설정
        Publisher publisher = new Publisher();
        publisher.setPublisherName("Test Publisher");
        // 필요한 다른 필드들도 설정

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);

        // Page<Publisher> 객체 생성
        Page<Publisher> publisherPage = new PageImpl<>(Arrays.asList(publisher), pageable, 1);

        // publisherRepository.findAllBy(pageable) 메서드 모킹
        when(publisherRepository.findAllBy(pageable)).thenReturn(publisherPage);

        // service 메서드 호출
        List<Publisher> publishers = publisherService.getAllPublisher(pageable);

        // Assertions
        assertThat(publishers).hasSize(1);
        assertThat(publishers.get(0).getPublisherName()).isEqualTo("Test Publisher");

        // verify 호출된 메서드 확인
        verify(publisherRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void testGetPublisher_Success() {
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        Publisher foundPublisher = publisherService.getPublisher(1L);

        assertThat(foundPublisher).isNotNull();
        assertThat(foundPublisher.getPublisherName()).isEqualTo("Test Publisher");
        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPublisher_NotFound() {
        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        Publisher foundPublisher = publisherService.getPublisher(1L);

        assertThat(foundPublisher).isNull();
        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    void testDeletePublisher() {
        publisherService.deletePublisher(1L);

        verify(publisherRepository, times(1)).deleteById(1L);
    }

//    @Test
//    void testUpdatePublisher_Success() {
//        PublisherRequestDTO requestDTO = new PublisherRequestDTO("Updated Publisher");
//
//        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
//        when(publisherRepository.save(any(Publisher.class))).thenReturn(Publisher.of("Updated Publisher"));
//
//        Publisher updatedPublisher = publisherService.updatePublisher(1L, requestDTO);
//
//        assertThat(updatedPublisher.getPublisherName()).isEqualTo("Updated Publisher");
//        verify(publisherRepository, times(1)).findById(1L);
//        verify(publisherRepository, times(1)).save(any(Publisher.class));
//    }

    @Test
    void testUpdatePublisher_NotFound() {
        PublisherRequestDTO requestDTO = new PublisherRequestDTO("Updated Publisher");

        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisherService.updatePublisher(1L, requestDTO));

        assertThat(exception.getMessage()).isEqualTo("publisher cannot found: 1");

        verify(publisherRepository, times(1)).findById(1L);
        verify(publisherRepository, never()).save(any(Publisher.class));
    }
}