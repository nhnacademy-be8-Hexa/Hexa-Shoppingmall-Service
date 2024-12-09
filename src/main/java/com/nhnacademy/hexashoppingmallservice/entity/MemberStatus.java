package com.nhnacademy.hexashoppingmallservice.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
}
