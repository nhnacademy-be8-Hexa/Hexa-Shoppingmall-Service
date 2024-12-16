package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
