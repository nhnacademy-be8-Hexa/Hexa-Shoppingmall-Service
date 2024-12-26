package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookCategory;
import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookCategoryRepositoryTest {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category category;
    private Book book;
    private BookCategory bookCategory;

    @BeforeEach
    void setUp() {
        // Step 1: Create and persist a Category
        category = Category.of("Fiction", null);
        entityManager.persist(category);

        // Step 2: Create and persist a Publisher
        Publisher publisher = Publisher.of("Awesome Publisher");
        entityManager.persist(publisher);

        // Step 3: Create and persist a BookStatus
        BookStatus bookStatus = BookStatus.of("Available");
        entityManager.persist(bookStatus);

        // Step 4: Create and persist a Book
        book = Book.of(
                "Sample Book",
                "A great book for everyone.",
                LocalDate.now(),
                9781234567897L,
                10000,
                15000,
                publisher,
                bookStatus
        );
        entityManager.persist(book);

        // Step 5: Create and persist a BookCategory
        bookCategory = BookCategory.of(category, book);
        entityManager.persist(bookCategory);

        entityManager.flush();
    }

    @Test
    @DisplayName("BookCategory 저장 및 조회 테스트")
    void saveAndFindById() {
        // 저장된 BookCategory 조회
        Optional<BookCategory> found = bookCategoryRepository.findById(bookCategory.getBookCategoryId());

        // 검증
        assertTrue(found.isPresent(), "BookCategory should exist");
        assertEquals(bookCategory.getBookCategoryId(), found.get().getBookCategoryId(), "BookCategory ID should match");
        assertEquals(book.getBookId(), found.get().getBook().getBookId(), "Book ID should match");
        assertEquals(category.getCategoryId(), found.get().getCategory().getCategoryId(), "Category ID should match");
    }

    @Test
    @DisplayName("모든 BookCategory 조회 테스트")
    void findAll() {
        // 모든 BookCategory 조회
        List<BookCategory> bookCategories = bookCategoryRepository.findAll();

        // 검증
        assertNotNull(bookCategories, "BookCategories should not be null");
        assertEquals(1, bookCategories.size(), "There should be exactly 1 BookCategory");
        assertEquals(category.getCategoryName(), bookCategories.get(0).getCategory().getCategoryName(), "Category name should match");
    }

    @Test
    @DisplayName("BookCategory 삭제 테스트")
    void deleteById() {
        // BookCategory 삭제
        bookCategoryRepository.deleteById(bookCategory.getBookCategoryId());
        entityManager.flush();

        // 삭제 후 조회
        Optional<BookCategory> deleted = bookCategoryRepository.findById(bookCategory.getBookCategoryId());
        assertFalse(deleted.isPresent(), "BookCategory should be deleted");
    }
}
