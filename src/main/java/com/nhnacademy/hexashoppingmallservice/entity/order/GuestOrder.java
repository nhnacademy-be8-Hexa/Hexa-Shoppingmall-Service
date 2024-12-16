package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@Getter
public class GuestOrder {
    @Id
    @OneToOne
    private Order orderId;

    @NotBlank
    @Length(max = 60)
    private String guestOrderPassword;

    @NotBlank
    @Length(max = 11)
    private String guestOrderNumber;

    @NotBlank
    @Length(max = 320)
    private String guestOrderEmail;

    @Builder
    private GuestOrder(Order orderId, String guestOrderPassword, String guestOrderNumber, String guestOrderEmail) {
        this.orderId = orderId;
        this.guestOrderPassword = guestOrderPassword;
        this.guestOrderNumber = guestOrderNumber;
        this.guestOrderEmail = guestOrderEmail;
    }

    public static GuestOrder of(Order orderId, String guestOrderPassword, String guestOrderNumber, String guestOrderEmail) {
        return builder()
                .orderId(orderId)
                .guestOrderPassword(guestOrderPassword)
                .guestOrderNumber(guestOrderNumber)
                .guestOrderEmail(guestOrderEmail)
                .build();
    }

}
