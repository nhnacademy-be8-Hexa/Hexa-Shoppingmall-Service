package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.PublisherRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        when(publisherRepository.findAll()).thenReturn(Arrays.asList(publisher));

        List<Publisher> publishers = publisherService.getAllPublisher();

        assertThat(publishers).hasSize(1);
        assertThat(publishers.get(0).getPublisherName()).isEqualTo("Test Publisher");
        verify(publisherRepository, times(1)).findAll();
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

    @Test
    void testUpdatePublisher_Success() {
        PublisherRequestDTO requestDTO = new PublisherRequestDTO("Updated Publisher");

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(publisherRepository.save(any(Publisher.class))).thenReturn(Publisher.of("Updated Publisher"));

        Publisher updatedPublisher = publisherService.updatePublisher(1L, requestDTO);

        assertThat(updatedPublisher.getPublisherName()).isEqualTo("Updated Publisher");
        verify(publisherRepository, times(1)).findById(1L);
        verify(publisherRepository, times(1)).save(any(Publisher.class));
    }

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