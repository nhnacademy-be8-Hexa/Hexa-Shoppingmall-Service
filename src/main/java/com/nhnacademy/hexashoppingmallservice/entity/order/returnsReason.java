package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class returnsReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnsReasonId;
    @Column(nullable = false)
    @Length(max = 20)
    private String returnsReason;
}
