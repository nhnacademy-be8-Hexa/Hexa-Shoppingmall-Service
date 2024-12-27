package com.nhnacademy.hexashoppingmallservice.repository.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookTagRepositoryTest {

    @Autowired
    private BookTagRepository bookTagRepository;

    @Autowired
    private TestEntityManager entityManager;


    private Book book;
    private Tag tag;

    @BeforeEach
    void setUp() {


        Publisher publisher = Publisher.of("Test Publisher");
        entityManager.persist(publisher);

        BookStatus bookStatus = BookStatus.of("Available");
        entityManager.persist(bookStatus);

        // Book 생성
        book = Book.of(
                "Test Book",
                "A book for testing",
                LocalDate.now(),
                1234567890123L,
                10000,
                9000,
                publisher,
                bookStatus
        );
        // Book 영속화
        entityManager.persist(book);

        // Tag 생성
        tag = Tag.of("Test Tag");
        // Tag 영속화
        entityManager.persist(tag);
        // BookTag 관계 생성 및 영속화
        BookTag bookTag = BookTag.of(book, tag);
        entityManager.persist(bookTag);
        // 플러시를 통해 즉시 DB에 반영
        entityManager.flush();
    }


    @Test
    @DisplayName("책 ID로 태그 리스트 조회")
    void findTagsByBookId() {
        List<Tag> tags = bookTagRepository.findTagsByBookId(book.getBookId());
        assertThat(tags).isNotEmpty();
        assertThat(tags.get(0).getTagName()).isEqualTo("Test Tag");
    }

    @Test
    @DisplayName("태그 ID로 책 리스트 조회")
    void findBooksByTagId() {
        Page<Book> books = bookTagRepository.findBooksByTagId(tag.getTagId(), PageRequest.of(0, 10));
        assertThat(books).isNotEmpty();
        assertThat(books.getContent().get(0).getBookTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("태그 ID로 BookTag 리스트 조회")
    void findByTag_TagId() {
        List<BookTag> bookTags = bookTagRepository.findByTag_TagId(tag.getTagId());
        assertThat(bookTags).isNotEmpty();
        assertThat(bookTags.get(0).getTag().getTagName()).isEqualTo("Test Tag");
    }

    @Test
    @DisplayName("책 ID와 태그 ID로 존재 여부 확인")
    void existsByBook_BookIdAndTag_TagId() {
        boolean exists = bookTagRepository.existsByBook_BookIdAndTag_TagId(book.getBookId(), tag.getTagId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("책 ID와 태그 ID로 관계 삭제")
    void deleteByBook_BookIdAndTag_TagId() {
        bookTagRepository.deleteByBook_BookIdAndTag_TagId(book.getBookId(), tag.getTagId());
        boolean exists = bookTagRepository.existsByBook_BookIdAndTag_TagId(book.getBookId(), tag.getTagId());
        assertThat(exists).isFalse();
    }
}
