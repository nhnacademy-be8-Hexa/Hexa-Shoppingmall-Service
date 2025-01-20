package com.nhnacademy.hexashoppingmallservice.feignclient.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Book {
    private String title;
    private String author;
    private String priceSales;
    private String priceStandard;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private String description;
    private String salesPoint;
    private String cover;
}
