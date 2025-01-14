package com.nhnacademy.hexashoppingmallservice.service.tag;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookTagService {
    private final BookTagRepository bookTagRepository;
    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    // 책 아이디로 태그 리스트 조회
    public List<Tag> getTagsByBookId(Long bookId) {
        if(!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book: %d not found.".formatted(bookId));
        }
        return bookTagRepository.findTagsByBookId(bookId);
    }

    // 태그 아이디로 책 리스트 조회
    public List<Book> getBooksByTagId(Long tagId, Pageable pageable) {
        if(!tagRepository.existsById(tagId)) {
            throw new TagNotFoundException("Tag: %d not found.".formatted(tagId));
        }
        return bookTagRepository.findBooksByTagId(tagId, pageable).getContent();
    }

    // 태그 아이디로 책 갯수 카운트
    public int countBooksByTagId(Long tagId) {
        if(!tagRepository.existsById(tagId)) {
            throw new TagNotFoundException("Tag: %d not found.".formatted(tagId));
        }
        return bookTagRepository.countByTag_TagId(tagId);
    }

    // 생성
    @Transactional
    public void create(Long bookId, Long tagId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()->new BookNotFoundException("Book: %d not found.".formatted(bookId))
        );

        Tag tag = tagRepository.findById(tagId).orElseThrow(
                ()->new TagNotFoundException("Tag: %d not found.".formatted(tagId))
        );

        if(bookTagRepository.existsByBook_BookIdAndTag_TagId(bookId, tagId)) {
            throw new AlreadyExistsBookTagException("Book: %d, Tag: %d already exists".formatted(bookId, tagId));
        }

        BookTag bookTag = BookTag.of(book, tag);
        bookTagRepository.save(bookTag);
    }

    // 삭제
    @Transactional
    public void delete(Long bookId, Long tagId) {
        bookTagRepository.deleteByBook_BookIdAndTag_TagId(bookId, tagId);
    }

}
