package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@Getter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    @Length(max = 30)
    @Setter
    private String tagName;

    @Builder
    private Tag(String tagName) {
        this.tagName = tagName;
    }

    public static Tag of(String tagName){
        return Tag.builder()
                .tagName(tagName)
                .build();
    }
}