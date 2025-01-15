package com.nhnacademy.hexashoppingmallservice.feignclient.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AladinBookDTO {
    private String title;
    private List<String> authors;
    private String priceSales;
    private String priceStandard;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private String description;
    private String salesPoint;
    private String cover;
}
