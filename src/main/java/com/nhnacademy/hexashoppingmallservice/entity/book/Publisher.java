package com.nhnacademy.hexashoppingmallservice.entity.book;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long publisherId;

    @Column(nullable = false, length = 20)
    private String publisherName;

    @Builder
    private Publisher(String publisherName) {
        this.publisherName = publisherName;
    }

    public static Publisher of(String publisherName) {
        return Publisher.builder()
                .publisherName(publisherName)
                .build();
    }
}
