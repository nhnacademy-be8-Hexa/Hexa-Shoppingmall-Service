package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Column(nullable = false, length = 20)
    private String authorName;

    @OneToMany(mappedBy = "author" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors;
}
