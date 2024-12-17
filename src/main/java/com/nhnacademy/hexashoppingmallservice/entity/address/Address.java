package com.nhnacademy.hexashoppingmallservice.entity.address;


import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Address(String addressName, String zoneCode, String addressDetail, Member member) {
        this.addressName = addressName;
        this.zoneCode = zoneCode;
        this.addressDetail = addressDetail;
        this.member = member;
    }

    public static Address of(String addressName, String zoneCode, String addressDetail, Member member) {
        return builder()
                .addressName(addressName)
                .zoneCode(zoneCode)
                .addressDetail(addressDetail)
                .member(member)
                .build();
    }

}
