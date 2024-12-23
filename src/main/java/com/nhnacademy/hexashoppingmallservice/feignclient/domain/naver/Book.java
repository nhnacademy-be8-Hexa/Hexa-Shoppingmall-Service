package com.nhnacademy.hexashoppingmallservice.feignclient.domain.naver;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Book {
    private String title;
    private String image;
    private String author;
    private String discount;
    private String publisher;
    private String pubdate;
    private String isbn;
    private String description;
}
