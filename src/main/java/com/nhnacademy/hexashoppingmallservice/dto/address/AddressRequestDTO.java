package com.nhnacademy.hexashoppingmallservice.dto.address;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
public class AddressRequestDTO {
    @NotBlank
    @Column(nullable = false)
    @Setter
    @Length(max = 20)
    private String addressName;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String zoneCode;

    @NotBlank
    @Column(nullable = false)
    @Setter
    @Length(max = 400)
    private String address;

    @NotBlank
    @Column(nullable = false)
    @Setter
    @Length(max = 400)
    private String addressDetail;
}
