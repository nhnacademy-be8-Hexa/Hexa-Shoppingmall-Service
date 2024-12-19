package com.nhnacademy.hexashoppingmallservice.service.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.BookTagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.tag.AlreadyExistsBookTagException;
import com.nhnacademy.hexashoppingmallservice.exception.tag.TagNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookTagService {
    private final BookTagRepository bookTagRepository;
    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    // 책 아이디로 태그 리스트 조회
    public List<Tag> getTagsByBookId(Long bookId) {
        if(!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book: %d not found.".formatted(bookId));
        }
        return bookTagRepository.findTagsByBook_BookId(bookId);
    }

    // 태그 아이디로 책 리스트 조회
    public List<Book> getBooksByTagId(Long tagId, Pageable pageable) {
        if(!tagRepository.existsById(tagId)) {
            throw new TagNotFoundException("Tag: %d not found.".formatted(tagId));
        }
        return bookTagRepository.findBooksByTag_TagId(tagId, pageable).getContent();
    }

    public void create(BookTagRequestDTO requestDTO) {
        Book book = bookRepository.findById(requestDTO.bookId()).orElseThrow(
                ()->new BookNotFoundException("Book: %d not found.".formatted(requestDTO.bookId()))
        );

        Tag tag = tagRepository.findById(requestDTO.tagId()).orElseThrow(
                ()->new TagNotFoundException("Tag: %d not found.".formatted(requestDTO.tagId()))
        );

        if(bookTagRepository.existsByBook_BookIdAndTag_TagId(requestDTO.bookId(), requestDTO.tagId())) {
            throw new AlreadyExistsBookTagException("Book: %d, Tag: %d already exists".formatted(requestDTO.bookId(), requestDTO.tagId()));
        }

        BookTag bookTag = BookTag.of(book, tag);
        bookTagRepository.save(bookTag);
    }

    public void delete(Long BookTagId) {
        bookTagRepository.deleteById(BookTagId);
    }

}
