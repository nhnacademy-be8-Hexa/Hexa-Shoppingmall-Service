package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.LikeAlreadyExistsException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.LikeRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.LikeRepositoryCustom;
import com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.impl.LikeRepositoryCustomImpl;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LikeRepositoryCustomImpl likeRepositoryCustom;

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
            throw new LikeAlreadyExistsException(
                    String.format("Like already exists for bookId %d and memberId %s.", bookId, memberId));
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
        return likeRepositoryCustom.findBooksLikedByMemberId(memberId);
    }


    /**
     * 좋아요를 토글하는 메서드
     * 이미 좋아요가 있으면 취소하고, 없으면 추가합니다.
     *
     * @param bookId   좋아요를 토글할 책의 ID
     * @param memberId 좋아요를 토글할 회원의 ID
     */
    @Transactional
    public void toggleLike(Long bookId, String memberId) {

        if (!bookRepository.existsById(bookId)) {
            throw new BookNotExistException("BookId %d is not exist".formatted(bookId));
        }

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("MemberId %s is not exist".formatted(memberId));
        }

        boolean exists = likeRepository.existsByBookBookIdAndMemberMemberId(bookId, memberId);

        if (exists) {
            deleteLike(bookId, memberId);
        } else {
            createLike(bookId, memberId);
        }
    }

    /**
     * 좋아요를 삭제하는 메서드
     */
    @Transactional
    public void deleteLike(Long bookId, String memberId) {
        likeRepository.deleteByBookBookIdAndMemberMemberId(bookId, memberId);
    }


    /**
     * 특정 회원이 특정 도서에 대해 좋아요를 눌렀는지 확인하는 메서드
     *
     * @param bookId   확인할 도서의 ID
     * @param memberId 확인할 회원의 ID
     * @return 회원이 해당 도서에 좋아요를 눌렀으면 true, 그렇지 않으면 false
     */
    @Transactional(readOnly = true)
    public Boolean hasLiked(Long bookId, String memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotExistException("BookId %d does not exist".formatted(bookId));
        }

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("MemberId %s is not exist".formatted(memberId));
        }

        return likeRepository.existsByBookBookIdAndMemberMemberId(bookId, memberId);
    }
}
