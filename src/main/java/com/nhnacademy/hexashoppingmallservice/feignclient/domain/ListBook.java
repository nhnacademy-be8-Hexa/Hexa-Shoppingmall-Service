package com.nhnacademy.hexashoppingmallservice.feignclient.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListBook {
    private List<Book> item;
}
