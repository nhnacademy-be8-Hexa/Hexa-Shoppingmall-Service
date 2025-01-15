package com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
public class Book {
    @Setter
    private String title;
    @Setter
    private String author;
    private String priceSales;
    private String priceStandard;
    private String publisher;
    private String pubDate;
    private String isbn13;
    @Setter
    private String description;
    private String categoryName;
    private String salesPoint;
    @Setter
    private String cover;
}
