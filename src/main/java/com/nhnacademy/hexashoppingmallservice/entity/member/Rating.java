package com.nhnacademy.hexashoppingmallservice.entity.member;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    // 테스트를 위한 세터 설정
    @Setter
    private Long ratingId;
    @Column(nullable = false)
    @Length(max = 20)
    @Setter
    private String ratingName;
    @Setter
    @Column(nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer ratingPercent;

    public static Rating of(String ratingName, Integer ratingPercent) {
        return Rating.builder()
                .ratingName(ratingName)
                .ratingPercent(ratingPercent)
                .build();
    }
}
