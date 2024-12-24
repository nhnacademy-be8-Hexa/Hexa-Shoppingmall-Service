package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ReturnsReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnsReasonId;
    @Column(nullable = false)
    @Length(max = 20)
    @Setter
    private String returnsReason;

    public static ReturnsReason of(String returnsReason) {
        return ReturnsReason.builder()
                .returnsReason(returnsReason).build();
    }
}
