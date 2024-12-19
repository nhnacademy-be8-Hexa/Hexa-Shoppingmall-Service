package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.PublisherRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    @Transactional
    public Publisher createPublisher(Publisher publisher){
        return publisherRepository.save(publisher);
    }

    @Transactional(readOnly = true)
    public List<Publisher> getAllPublisher(){
        return publisherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Publisher getPublisher(Long publisherId){
        return publisherRepository.findById(publisherId).orElse(null);
    }

    @Transactional
    public void deletePublisher(Long publisherId){
        publisherRepository.deleteById(publisherId);
    }

    @Transactional
    public Publisher updatePublisher(Long publisherId, PublisherRequestDTO publisherRequestDTO){
        Publisher publisher = publisherRepository.findById(publisherId).orElse(null);
        if(Objects.isNull(publisher)){
            throw new RuntimeException("publisher cannot found: "+publisherId);
        }
        publisher.setPublisherName(publisherRequestDTO.getPublisherName());
        return publisher;
    }


}
