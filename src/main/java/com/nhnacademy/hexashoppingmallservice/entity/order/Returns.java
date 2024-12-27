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
    @NotNull
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

    @Setter
    @Column
    @Length(max = 100)
    private String returnsDetail;

    public static Returns of(Order order, ReturnsReason returnsReason, String returnsDetail) {
        return Returns.builder()
                .order(order)
                .returnsReason(returnsReason)
                .returnsDetail(returnsDetail)
                .build();
    }
}
