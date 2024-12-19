package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    @Setter
    private String publisherName;

    public static Publisher of(String publisherName){
        return Publisher.builder()
                .publisherName(publisherName)
                .build();
    }
}