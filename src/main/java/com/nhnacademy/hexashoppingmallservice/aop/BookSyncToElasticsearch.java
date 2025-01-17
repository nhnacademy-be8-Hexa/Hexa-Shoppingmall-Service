package com.nhnacademy.hexashoppingmallservice.aop;


import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookAuthor;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
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
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;

    @Autowired
    public BookSyncToElasticsearch(ElasticSearchRepository elasticsearchRepository,
                                   TagRepository tagRepository,
                                   AuthorRepository authorRepository,
                                   PublisherRepository publisherRepository,
                                   BookStatusRepository bookStatusRepository) {
        this.elasticsearchRepository = elasticsearchRepository;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookStatusRepository = bookStatusRepository;
    }


    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository.save(..))")
    public void syncBookToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Book book) {
            com.nhnacademy.hexashoppingmallservice.document.Book existingDocument =
                    elasticsearchRepository.findById(book.getBookId()).orElse(null);
            if (Objects.isNull(existingDocument)) {
                Publisher publisher = publisherRepository.findById(book.getPublisher().getPublisherId()).orElseThrow();
                BookStatus bookStatus =
                        bookStatusRepository.findById(book.getBookStatus().getBookStatusId()).orElseThrow();

                com.nhnacademy.hexashoppingmallservice.document.Publisher documentPublisher =
                        com.nhnacademy.hexashoppingmallservice.document.Publisher.of(
                                publisher.getPublisherId(), publisher.getPublisherName()
                        );

                com.nhnacademy.hexashoppingmallservice.document.BookStatus documentBookStatus =
                        com.nhnacademy.hexashoppingmallservice.document.BookStatus.of(
                                bookStatus.getBookStatusId(), bookStatus.getBookStatus()
                        );

                com.nhnacademy.hexashoppingmallservice.document.Book document =
                        com.nhnacademy.hexashoppingmallservice.document.Book.of(book, documentBookStatus,
                                documentPublisher);
                elasticsearchRepository.save(document);

            } else {
                existingDocument.setBookTitle(book.getBookTitle());
                existingDocument.setBookDescription(book.getBookDescription());
                existingDocument.setBookPrice(book.getBookPrice());
                existingDocument.setBookWrappable(book.getBookWrappable());
                existingDocument.setBookView(book.getBookView());
                existingDocument.setBookSellCount(book.getBookSellCount());
                existingDocument.setBookAmount(book.getBookAmount());

                BookStatus bookStatus =
                        bookStatusRepository.findById(book.getBookStatus().getBookStatusId()).orElseThrow();
                com.nhnacademy.hexashoppingmallservice.document.BookStatus documentBookStatus =
                        com.nhnacademy.hexashoppingmallservice.document.BookStatus.of(
                                bookStatus.getBookStatusId(), bookStatus.getBookStatus()
                        );
                existingDocument.setBookStatus(documentBookStatus);
                elasticsearchRepository.save(existingDocument);
            }
        }
    }


//    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository.deleteById(..))")
//    public void syncBookToElasticsearchAfterDelete(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        if (args.length > 0) {
//            if (args[0] instanceof Long bookId) {
//                com.nhnacademy.hexashoppingmallservice.document.Book existingDocument =
//                        elasticsearchRepository.findById(bookId).orElse(null);
//
//                if (Objects.nonNull(existingDocument)) {
//                    elasticsearchRepository.deleteById(bookId);
//                }
//            }
//        }
//    }


    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository.save(..))")
    public void snycTagToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof BookTag bookTag) {
            Tag tag = tagRepository.findById(bookTag.getTag().getTagId()).orElseThrow();

            com.nhnacademy.hexashoppingmallservice.document.Book book =
                    elasticsearchRepository.findById(bookTag.getBook().getBookId()).orElseThrow();

            List<com.nhnacademy.hexashoppingmallservice.document.Tag> currentTags = book.getBookTags();

            if (Objects.isNull(currentTags)) {
                currentTags = new ArrayList<>();
            }

            boolean tagExists = currentTags.stream()
                    .anyMatch(existingTag -> existingTag.getTagId().equals(tag.getTagId()));

            if (!tagExists) {
                com.nhnacademy.hexashoppingmallservice.document.Tag documentTag =
                        com.nhnacademy.hexashoppingmallservice.document.Tag.of(tag.getTagId(), tag.getTagName());
                currentTags.add(documentTag);
            }

            book.setBookTags(currentTags);
            elasticsearchRepository.save(book);
        }
    }

    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository.deleteById(..)) && args(tagId)")
    public void deleteTagFromBooksAfterDelete(Long tagId) {
        Iterable<com.nhnacademy.hexashoppingmallservice.document.Book> booksIterable =
                elasticsearchRepository.findAll();

        List<com.nhnacademy.hexashoppingmallservice.document.Book> books =
                StreamSupport.stream(booksIterable.spliterator(), false)
                        .toList();
        
        for (com.nhnacademy.hexashoppingmallservice.document.Book book : books) {
            List<com.nhnacademy.hexashoppingmallservice.document.Tag> currentTags = book.getBookTags();

            if (Objects.nonNull(currentTags)) {
                boolean tagRemoved = currentTags.removeIf(existingTag -> existingTag.getTagId().equals(tagId));
                if (tagRemoved) {
                    book.setBookTags(currentTags);
                    elasticsearchRepository.save(book);
                }
            }
        }
    }

    @After(value = "execution(* com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository.deleteByBook_BookIdAndTag_TagId(..)) && args(bookId, tagId)",
            argNames = "bookId,tagId")
    public void deleteTagFromBooksAfterTagRelationDelete(Long bookId, Long tagId) {
        Iterable<com.nhnacademy.hexashoppingmallservice.document.Book> booksIterable =
                elasticsearchRepository.findAll();

        List<com.nhnacademy.hexashoppingmallservice.document.Book> books =
                StreamSupport.stream(booksIterable.spliterator(), false)
                        .toList();

        for (com.nhnacademy.hexashoppingmallservice.document.Book book : books) {
            if (book.getBookId().equals(bookId)) {
                List<com.nhnacademy.hexashoppingmallservice.document.Tag> currentTags = book.getBookTags();

                if (Objects.nonNull(currentTags)) {
                    boolean tagRemoved = currentTags.removeIf(existingTag -> existingTag.getTagId().equals(tagId));
                    if (tagRemoved) {
                        book.setBookTags(currentTags);
                        elasticsearchRepository.save(book);
                    }
                }
            }
        }
    }

    @After("execution(* com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository.save(..))")
    public void syncAuthorToElasticsearchAfterSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof BookAuthor bookAuthor) {
            Author author = authorRepository.findById(bookAuthor.getAuthor().getAuthorId()).orElseThrow();

            com.nhnacademy.hexashoppingmallservice.document.Book book =
                    elasticsearchRepository.findById(bookAuthor.getBook().getBookId()).orElseThrow();
            List<com.nhnacademy.hexashoppingmallservice.document.Author> currentAuthors = book.getAuthors();

            if (Objects.isNull(currentAuthors)) {
                currentAuthors = new ArrayList<>();
            }

            boolean authorExists = currentAuthors.stream()
                    .anyMatch(existingTag -> existingTag.getAuthorId().equals(author.getAuthorId()));

            if (!authorExists) {
                com.nhnacademy.hexashoppingmallservice.document.Author documentAuthor =
                        com.nhnacademy.hexashoppingmallservice.document.Author.of(author.getAuthorId(),
                                author.getAuthorName());
                currentAuthors.add(documentAuthor);
            }

            book.setAuthors(currentAuthors);
            elasticsearchRepository.save(book);
        }
    }


}

