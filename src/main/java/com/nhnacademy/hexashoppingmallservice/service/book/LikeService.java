package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.LikeAlreadyExistsException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.LikeRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createLike(Long bookId, String memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotExistException("BookId %d is not exist".formatted(bookId));
        }

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("MemberId %s is not exist".formatted(memberId));
        }

        boolean exists = likeRepository.existsByBookBookIdAndMemberMemberId(bookId, memberId);
        if (exists) {
            throw new LikeAlreadyExistsException(String.format("Like already exists for bookId %d and memberId %s.", bookId, memberId));
        }

        Book book = bookRepository.findById(bookId).get();
        Member member = memberRepository.findById(memberId).get();

        Like like = Like.of(book, member);

        likeRepository.save(like);
    }

    @Transactional(readOnly = true)
    public Long sumLikes(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotExistException(String.format("BookId %d does not exist.", bookId));
        }
        return likeRepository.countByBookBookId(bookId);
    }


    /**
     * 특정 멤버가 좋아요한 책 목록을 조회합니다.
     *
     * @param memberId 멤버의 ID
     * @return 멤버가 좋아요한 책 리스트
     */
    public List<Book> getBooksLikedByMember(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("MemberId %s is not exist".formatted(memberId));
        }
        return likeRepository.findBooksLikedByMemberId(memberId);
    }
}
