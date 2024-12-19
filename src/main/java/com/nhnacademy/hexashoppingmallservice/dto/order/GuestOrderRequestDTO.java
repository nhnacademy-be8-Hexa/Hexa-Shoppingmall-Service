package com.nhnacademy.hexashoppingmallservice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestOrderRequestDTO {
    private Long orderId;
    private String guestOrderPassword;
    private String guestOrderNumber;
    private String guestOrderEmail;
}
