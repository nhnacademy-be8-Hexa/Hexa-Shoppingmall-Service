package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestOrderValidateRequestDTO {

    @NotNull
    private Long orderId;

    @NotNull
    @Length(max = 60)
    private String guestOrderPassword;
}

