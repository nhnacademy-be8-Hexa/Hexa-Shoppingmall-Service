package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Returns {
    @OneToOne
    @JoinColumn(name = "order_id")
    @Id
    private Order order;

    @Column
    @Length(min = 100)
    private String returnsDetail;
}
