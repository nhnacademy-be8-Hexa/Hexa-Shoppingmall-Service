package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
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

        Book book = bookRepository.findById(bookId).get();
        Member member = memberRepository.findById(memberId).get();

        Like like = Like.of(book, member);

        likeRepository.save(like);
    }

    @Transactional(readOnly = true)
    public Long sumLikes(Long bookId) {
        return likeRepository.countByBookBookId(bookId);
    }


    //TODO 좋아요 선택된 도서 목록 구현은 도서 API에서?
}
