package com.nhnacademy.hexashoppingmallservice.projection.cart;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;

public interface CartProjection {
    Long getCartId();
    MemberProjection getMember();
    BookProjection getBook();
    Integer getCartAmount();

    interface MemberProjection {
        String getMemberId();
    }
    interface BookProjection {
        Long getBookId();
        String getBookTitle();
    }
}
