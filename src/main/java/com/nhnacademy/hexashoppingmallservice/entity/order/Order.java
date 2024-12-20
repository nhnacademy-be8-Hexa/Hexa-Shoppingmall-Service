package com.nhnacademy.hexashoppingmallservice.entity.order;


import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @Setter
    private Integer orderPrice;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @ManyToOne
    @JoinColumn(name = "wrapping_paper_id")
    @Setter
    private WrappingPaper wrappingPaper;

    @ManyToOne
    @Setter
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Length(max = 5)
    @Setter
    private String zoneCode;

    @Column(nullable = false)
    @Length(max = 200)
    @Setter
    private String address;

    @Column
    @Length(max = 100)
    @Setter
    private String addressDetail;


    public static Order of(Member member, Integer orderPrice,
                           WrappingPaper wrappingPaper,
                           OrderStatus orderStatus, String zoneCode, String address, String addressDetail) {
        return Order.builder()
                .member(member)
                .orderPrice(orderPrice)
                .orderedAt(LocalDateTime.now())
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode(zoneCode)
                .address(address)
                .addressDetail(addressDetail).build();
    }
}
