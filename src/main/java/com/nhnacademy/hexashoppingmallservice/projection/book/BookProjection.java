package com.nhnacademy.hexashoppingmallservice.projection.book;

import java.time.LocalDate;

public interface BookProjection {
    Long getBookId();
    String getBookTitle();
    String getBookDescription();
    LocalDate getBookPubDate();
    Long getBookIsbn();
    int getBookOriginPrice();
    int getBookPrice();
    String getPublisher_PublisherName();
    String getBookStatus_BookStatus();
}
