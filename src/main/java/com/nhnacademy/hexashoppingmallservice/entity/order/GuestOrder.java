package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@Getter
public class GuestOrder {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @NotBlank
    @Length(max = 60)
    @Setter
    private String guestOrderPassword;

    @NotBlank
    @Length(max = 11)
    @Setter
    private String guestOrderNumber;

    @NotBlank
    @Length(max = 320)
    @Setter
    private String guestOrderEmail;

    @Builder
    private GuestOrder(Order order, Long orderId, String guestOrderPassword, String guestOrderNumber,
                       String guestOrderEmail) {
        this.order = order;
        this.guestOrderPassword = guestOrderPassword;
        this.guestOrderNumber = guestOrderNumber;
        this.guestOrderEmail = guestOrderEmail;
    }

    public static GuestOrder of(Order order, String guestOrderPassword, String guestOrderNumber,
                                String guestOrderEmail) {
        return builder()
                .order(order)
                .orderId(order.getOrderId())
                .guestOrderPassword(guestOrderPassword)
                .guestOrderNumber(guestOrderNumber)
                .guestOrderEmail(guestOrderEmail)
                .build();
    }

}