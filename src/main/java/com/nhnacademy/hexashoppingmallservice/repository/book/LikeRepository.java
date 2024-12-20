package com.nhnacademy.hexashoppingmallservice.repository.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByBookBookId(Long bookId);
    Boolean existsByBookBookIdAndMemberMemberId(Long bookId, String memberId);
    /**
     * 특정 멤버가 좋아요한 책 목록을 조회합니다.
     *
     * @param memberId 멤버의 ID
     * @return 멤버가 좋아요한 책 리스트
     */
    @Query("SELECT l.book FROM Like l WHERE l.member.memberId = :memberId")
    List<Book> findBooksLikedByMemberId(@Param("memberId") String memberId);
}
