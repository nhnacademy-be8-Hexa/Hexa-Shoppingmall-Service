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
public class BookStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookStatusId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    @Setter
    private String bookStatus;

    public static BookStatus of(String bookStatus) {
        return BookStatus.builder()
                .bookStatus(bookStatus)
                .build();

    }
}
