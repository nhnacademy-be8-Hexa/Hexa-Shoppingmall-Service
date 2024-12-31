package com.nhnacademy.hexashoppingmallservice.aop;


import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookAuthor;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class BookSyncToElasticsearch {

    private final ElasticSearchRepository elasticsearchRepository;
    private final TagRepository tagRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookSyncToElasticsearch(ElasticSearchRepository elasticsearchRepository,
                                   TagRepository tagRepository,
                                   AuthorRepository authorRepository) {
        this.elasticsearchRepository = elasticsearchRepository;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
    }


    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository.save(..))")
    public void syncBookToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Book book) {
            com.nhnacademy.hexashoppingmallservice.document.Book existingDocument =
                    elasticsearchRepository.findById(book.getBookId()).orElse(null);
            if (Objects.isNull(existingDocument)) {
                com.nhnacademy.hexashoppingmallservice.document.Book document =
                        com.nhnacademy.hexashoppingmallservice.document.Book.of(book);
                elasticsearchRepository.save(document);
            } else {
                existingDocument.setBookTitle(book.getBookTitle());
                existingDocument.setBookDescription(book.getBookDescription());
                existingDocument.setBookPrice(book.getBookPrice());
                existingDocument.setBookWrappable(book.getBookWrappable());
                existingDocument.setBookView(book.getBookView());
                existingDocument.setBookSellCount(book.getBookSellCount());
                existingDocument.setBookAmount(book.getBookAmount());
                existingDocument.setBookStatus(book.getBookStatus().getBookStatus());
                elasticsearchRepository.save(existingDocument);
            }
        }
    }

    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository.deleteById(..))")
    public void syncBookToElasticsearchAfterDelete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            if (args[0] instanceof Long bookId) {
                com.nhnacademy.hexashoppingmallservice.document.Book existingDocument =
                        elasticsearchRepository.findById(bookId).orElse(null);

                if (Objects.nonNull(existingDocument)) {
                    elasticsearchRepository.deleteById(bookId);
                }
            }
        }
    }


    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository.save(..))")
    public void snycTagToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof BookTag bookTag) {
            Tag tag = tagRepository.findById(bookTag.getBookTagId()).orElseThrow();

            com.nhnacademy.hexashoppingmallservice.document.Book book =
                    elasticsearchRepository.findById(bookTag.getBook().getBookId()).orElseThrow();

            List<String> currentTags = book.getTagsName();

            if (Objects.isNull(currentTags)) {
                currentTags = new ArrayList<>();
            }

            if (currentTags.stream()
                    .noneMatch(existingTag -> existingTag.equals(tag.getTagName()))) {
                currentTags.add(tag.getTagName());
            }

            book.setTagsName(currentTags);
            elasticsearchRepository.save(book);
        }
    }

    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository.save(..))")
    public void syncAuthorToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof BookAuthor bookAuthor) {
            Author author = authorRepository.findById(bookAuthor.getAuthor().getAuthorId()).orElseThrow();

            com.nhnacademy.hexashoppingmallservice.document.Book book =
                    elasticsearchRepository.findById(bookAuthor.getBook().getBookId()).orElseThrow();
            List<String> currentAuthors = book.getAuthorsName();

            if (Objects.isNull(currentAuthors)) {
                currentAuthors = new ArrayList<>();
            }
            
            if (currentAuthors.stream()
                    .noneMatch(
                            existingAuthor -> existingAuthor.equals(author.getAuthorName()))) {
                currentAuthors.add(author.getAuthorName());
            }

            book.setAuthorsName(currentAuthors);
            elasticsearchRepository.save(book);
        }
    }

}

