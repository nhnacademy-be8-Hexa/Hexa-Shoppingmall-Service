package com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.impl;

import com.nhnacademy.hexashoppingmallservice.entity.book.*;
import com.nhnacademy.hexashoppingmallservice.entity.review.QReview;
import com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.BookRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookRepositoryCustomImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {

    public BookRepositoryCustomImpl() {
        super(Book.class);
    }

    @Override
    public Page<Book> findBooksByAuthorNameLike(String authorName, Pageable pageable) {
        QBook book = QBook.book;
        QBookAuthor bookAuthor = QBookAuthor.bookAuthor;
        QAuthor author = QAuthor.author;

        // 소문자 변환 후 contains 조건
        BooleanExpression predicate = author.authorName.toLowerCase().contains(authorName.toLowerCase());

        // 기본 조회 쿼리 작성 (distinct를 사용하여 중복 제거)
        JPQLQuery<Book> query = from(book)
                .join(bookAuthor).on(bookAuthor.book.eq(book))
                .join(author).on(bookAuthor.author.eq(author))
                .where(predicate)
                .distinct();

        // 페이징 적용
        JPQLQuery<Book> paginatedQuery = getQuerydsl().applyPagination(pageable, query);
        List<Book> books = paginatedQuery.fetch();

        // 총 건수 조회 (distinct한 건수를 얻어야 함)
        JPQLQuery<Long> countQuery = from(book)
                .join(bookAuthor).on(bookAuthor.book.eq(book))
                .join(author).on(bookAuthor.author.eq(author))
                .where(predicate)
                .distinct()
                .select(book.count());
        Long total = countQuery.fetchOne();

        return new PageImpl<>(books, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Book> findAllOrderByReviewCountDesc(Pageable pageable) {
        QBook book = QBook.book;
        QReview review = QReview.review;

        // 기본 쿼리: Book을 기준으로 Review와 LEFT JOIN 후 그룹화, 리뷰 수 내림차순 정렬
        JPQLQuery<Book> query = from(book)
                .leftJoin(review).on(book.eq(review.book))
                .groupBy(book)
                .orderBy(review.count().desc());

        // 페이징 적용 (QuerydslRepositorySupport의 getQuerydsl() 사용)
        JPQLQuery<Book> paginatedQuery = getQuerydsl().applyPagination(pageable, query);
        List<Book> books = paginatedQuery.fetch();

        // Group By 결과의 전체 그룹 수를 total로 산출
        // (group by 결과는 중복 없이 Book별 그룹이므로, 리스트의 크기가 전체 건수가 됩니다.)
        JPQLQuery<Book> countQuery = from(book)
                .leftJoin(review).on(book.eq(review.book))
                .groupBy(book);
        List<Book> countList = countQuery.fetch();
        long total = countList.size();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    public Page<Book> findBooksByCategoryIds(List<Long> categoryIds, Pageable pageable) {
        QBookCategory bookCategory = QBookCategory.bookCategory;

        // BookCategory 엔티티를 기준으로 Book을 선택(select)
        JPQLQuery<Book> query = from(bookCategory)
                .select(bookCategory.book)
                .where(bookCategory.category.categoryId.in(categoryIds));

        // 페이징 적용
        JPQLQuery<Book> paginatedQuery = getQuerydsl().applyPagination(pageable, query);
        List<Book> books = paginatedQuery.fetch();

        // count 쿼리: 동일 조건을 적용하여 총 건수를 조회
        JPQLQuery<Book> countQuery = from(bookCategory)
                .select(bookCategory.book)
                .where(bookCategory.category.categoryId.in(categoryIds));
        long total = countQuery.fetch().size();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    public Page<Book> findBooksOrderByLikeCountDesc(Pageable pageable) {
        QBook book = QBook.book;
        QLike like = QLike.like;

        // Book 엔티티와 Like 엔티티를 LEFT JOIN
        JPQLQuery<Book> query = from(book)
                .leftJoin(like).on(like.book.eq(book))
                .groupBy(book.bookId)
                .orderBy(like.likeId.count().desc());

        // 페이징 적용 (QuerydslRepositorySupport의 내장 메서드를 활용)
        JPQLQuery<Book> paginatedQuery = getQuerydsl().applyPagination(pageable, query);
        List<Book> books = paginatedQuery.fetch();

        // count 쿼리: 그룹별 결과 건수를 센다.
        // 단, JPQL에서 Group By가 적용된 count 쿼리는 결과 리스트의 크기가 전체 건수가 됨
        JPQLQuery<Book> countQuery = from(book)
                .leftJoin(like).on(like.book.eq(book))
                .groupBy(book.bookId);
        long total = countQuery.fetch().size();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    public Page<Book> findBooksByTagName(String tagName, Pageable pageable) {
        QBook book = QBook.book;
        QBookTag bookTag = QBookTag.bookTag;
        QTag tag = QTag.tag;

        // Book, BookTag, Tag를 조인하여 조건에 맞는 Book을 조회합니다.
        JPQLQuery<Book> query = from(book)
                .join(bookTag).on(bookTag.book.eq(book))
                .join(tag).on(bookTag.tag.eq(tag))
                .where(tag.tagName.eq(tagName));

        // 페이징 적용
        JPQLQuery<Book> paginatedQuery = getQuerydsl().applyPagination(pageable, query);
        List<Book> books = paginatedQuery.fetch();

        // 총 건수 조회 (QuerydslRepositorySupport의 fetchCount() 사용)
        long total = query.fetchCount();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    public List<Author> findAuthorsByBookId(Long bookId) {
        QBook book = QBook.book;
        QBookAuthor bookAuthor = QBookAuthor.bookAuthor;
        QAuthor author = QAuthor.author;

        // Book 엔티티를 기준으로 BookAuthor와 Author를 명시적 조인
        return from(book)
                .join(bookAuthor).on(bookAuthor.book.eq(book))
                .join(author).on(bookAuthor.author.eq(author))
                .where(book.bookId.eq(bookId))
                .select(author)
                .fetch();
    }


    @Override
    public long countByCategoryIds(List<Long> categoryIds) {
        QBook book = QBook.book;
        QBookCategory bookCategory = QBookCategory.bookCategory;

        // Book 엔티티를 기준으로 BookCategory 엔티티와 명시적 조인(on 절 사용)
        JPQLQuery<Book> query = from(book)
                .join(bookCategory).on(bookCategory.book.eq(book))
                .where(bookCategory.category.categoryId.in(categoryIds));

        // countDistinct()를 적용하여 중복 없이 Book의 개수를 구함
        long count = query.select(book.countDistinct()).fetchOne();
        return count;
    }

    @Override
    public long countByAuthorName(String authorName) {
        QBook book = QBook.book;
        QBookAuthor bookAuthor = QBookAuthor.bookAuthor;

        // Book을 기준으로 BookAuthor와 명시적 조인
        JPQLQuery<Book> query = from(book)
                .join(bookAuthor).on(bookAuthor.book.eq(book))
                .where(bookAuthor.author.authorName.eq(authorName));

        // Book 기준으로 distinct count를 수행
        Long count = query.select(book.countDistinct()).fetchOne();
        return count != null ? count : 0L;
    }
}
