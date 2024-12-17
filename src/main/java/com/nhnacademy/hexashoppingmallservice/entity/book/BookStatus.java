package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class BookStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookStatusId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String bookStatus;

    @Builder
    private BookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public static BookStatus of(String bookStatus){
        return BookStatus.builder()
                .bookStatus(bookStatus)
                .build();
    }
}
