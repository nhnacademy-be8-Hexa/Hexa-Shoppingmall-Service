package com.nhnacademy.hexashoppingmallservice.entity.address;


import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String addressName;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String zoneCode;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String address;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String addressDetail;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
