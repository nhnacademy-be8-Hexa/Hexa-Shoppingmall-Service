package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long publisherId;

    @Column(nullable = false, length = 20)
    private String publisherName;

    public Publisher(String publisherName) {
        this.publisherName = publisherName;
    }
}
