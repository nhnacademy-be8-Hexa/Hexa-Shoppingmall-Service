package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String publisherName;

    @Builder
    private Publisher(String publisherName) {
        this.publisherName = publisherName;
    }

    public static Publisher of(String publisherName){
        return Publisher.builder()
                .publisherName(publisherName)
                .build();
    }
}