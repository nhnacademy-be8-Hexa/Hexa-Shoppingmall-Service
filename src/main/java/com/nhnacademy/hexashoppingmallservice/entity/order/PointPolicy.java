package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PointPolicy {
    @Id
    @Length(max = 20)
    private String pointPolicyName;

    @Column(nullable = false)
    private Integer pointDelta;
}
