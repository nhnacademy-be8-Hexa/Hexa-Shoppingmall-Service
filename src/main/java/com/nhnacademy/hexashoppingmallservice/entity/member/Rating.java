package com.nhnacademy.hexashoppingmallservice.entity.member;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ratingId;
    @Column(nullable = false)
    @Length(max = 20)
    private String ratingName;
    @Column(nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer ratingPercent;

    public Rating(String ratingName, Integer ratingPercent) {
        this.ratingName = ratingName;
        this.ratingPercent = ratingPercent;
    }
}
