package com.nhnacademy.hexashoppingmallservice.feignclient.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AladinBookDTO {
    private String title;
    private List<String> authors;
    private int priceSales;
    private int priceStandard;
    private String publisher;
    private LocalDate pubDate;
    private Long isbn13;
    private String description;
    private int salesPoint;
    private String cover;
}
