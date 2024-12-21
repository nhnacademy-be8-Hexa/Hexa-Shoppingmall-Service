package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Returns {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name="returns_reason_id")
    private ReturnsReason returnsReason;

    @Column
    @Length(min = 100)
    private String returnsDetail;
}
