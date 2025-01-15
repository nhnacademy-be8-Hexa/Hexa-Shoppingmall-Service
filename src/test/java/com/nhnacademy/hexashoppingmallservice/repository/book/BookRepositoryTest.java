package com.nhnacademy.hexashoppingmallservice.repository.book;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookCategory;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

@Slf4j
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Long categoryId1;
    private Long categoryId2;

    @BeforeEach
    void setUp() {

        Publisher publisher = Publisher.of("Test Publisher");
        entityManager.persist(publisher);

        BookStatus bookStatus = BookStatus.of("Available");
        entityManager.persist(bookStatus);

        Book book = Book.of(
                "Test Book",
                "Test Description",
                LocalDate.now(),
                1234567890123L,
                20000,
                18000,
                publisher,
                bookStatus
        );

        entityManager.persist(book);

        Tag tag = Tag.of("Test Tag");
        entityManager.persist(tag);

        BookTag bookTag = BookTag.of(book, tag);
        entityManager.persist(bookTag);

        Category category1 = Category.of("Category1", null);
        Category category2 = Category.of("Category2", category1);
        entityManager.persist(category1);
        entityManager.persist(category2);

        categoryId1 = category1.getCategoryId();
        categoryId2 = category2.getCategoryId();

        BookCategory bookCategory1 = BookCategory.of(category1, book);
        BookCategory bookCategory2 = BookCategory.of(category2, book);
        entityManager.persist(bookCategory1);
        entityManager.persist(bookCategory2);

        entityManager.flush();

    }

    @Test
    void testFindBooksByPublisherName() {
        var page = bookRepository.findByPublisherPublisherNameIgnoreCaseContaining("Test Publisher", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
    }

    @Test
    void testFindByBookTitleContaining() {
        var page = bookRepository.findByBookTitleContaining("Test", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
    }

//    @Test
//    void testFindBooksOrderByLikeCountDesc() {
//        var page = bookRepository.findBooksOrderByLikeCountDesc(PageRequest.of(0, 10));
//
//        assertThat(page.getContent()).hasSize(1);
//        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
//    }

//    @Test
//    void testFindBooksByCategoryIds() {
//
//        var page = bookRepository.findBooksByCategoryIds(List.of(categoryId1, categoryId2), PageRequest.of(0, 10));
//        System.out.println(categoryId1);
//        System.out.println(categoryId2);
//
//        assertThat(page.getContent()).hasSize(1);
//        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
//    }

//    @Test
//    void testFindBooksByTagName() {
//        var page = bookRepository.findBooksByTagName("Test Tag", PageRequest.of(0, 10));
//
//        assertThat(page.getContent()).hasSize(1);
//        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
//    }

    @Test
    void testFindAllByOrderByBookPubDateDesc() {
        var page = bookRepository.findAllByOrderByBookPubDateDesc(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getBookTitle()).isEqualTo("Test Book");
    }

    @Test
    void testExistsByBookIsbn() {
        // ISBN이 1234567890123인 책이 있는지 확인
        boolean exists = bookRepository.existsByBookIsbn(1234567890123L);
        assertThat(exists).isTrue();
    }
}