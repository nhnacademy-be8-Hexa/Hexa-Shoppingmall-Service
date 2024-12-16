package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BookStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bookStatusId;

    @Column(nullable = false, length = 20)
    private String bookStatus;

    public BookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }
}
