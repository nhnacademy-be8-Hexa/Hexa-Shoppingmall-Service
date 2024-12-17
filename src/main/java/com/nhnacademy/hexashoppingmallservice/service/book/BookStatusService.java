package com.nhnacademy.hexashoppingmallservice.service.book;

import com.netflix.discovery.provider.Serializer;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.io.Reader;
import java.util.List;
import java.util.Objects;

@Serializer
@RequiredArgsConstructor
public class BookStatusService {
    private final BookStatusRepository bookStatusRepository;

    @Transactional
    public BookStatus createBookStatus(BookStatus bookStatus) {
        return bookStatusRepository.save(bookStatus);
    }

    @Transactional(readOnly = true)
    public List<BookStatus> getAllBookStatus(){
        return bookStatusRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BookStatus getBookStatus(Long bookStatusId){
        return bookStatusRepository.findById(bookStatusId).orElse(null);
    }

    @Transactional
    public void deleteBookStatus(Long bookStatusId){
        bookStatusRepository.deleteById(bookStatusId);
    }

    @Transactional
    public BookStatus updateBookStatus(Long bookStatusId, BookStatusRequestDTO bookStatusRequestDTO){
        BookStatus bookStatus = bookStatusRepository.findById(bookStatusId).orElse(null);
        if(Objects.isNull(bookStatus)){
            throw new RuntimeException("id cannot found: "+bookStatusId);
        }

        bookStatus.setBookStatus(bookStatusRequestDTO.getBookStatus());
        return bookStatus;
    }
}
