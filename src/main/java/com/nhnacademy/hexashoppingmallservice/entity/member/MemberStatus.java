package com.nhnacademy.hexashoppingmallservice.entity.member;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
public class MemberStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long statusId;
    @Column(nullable = false)
    @Length(max = 20)
    private String statusName;

    public MemberStatus(String statusName) {
        this.statusName = statusName;
    }

    public static MemberStatus of(String statusName) {
        return MemberStatus.builder()
                .statusName(statusName)
                .build();
    }
}
