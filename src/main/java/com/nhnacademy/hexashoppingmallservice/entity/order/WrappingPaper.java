package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@NoArgsConstructor
public class WrappingPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wrappingPaperId;

    @Setter
    @NotBlank
    @Length(max = 20)
    private String wrappingPaperName;

    @Setter
    private Integer wrappingPaperPrice;

    @Builder
    private WrappingPaper(String wrappingPaperName, Integer wrappingPaperPrice) {
        this.wrappingPaperName = wrappingPaperName;
        this.wrappingPaperPrice = wrappingPaperPrice;
    }

    public static WrappingPaper of(String wrappingPaperName, Integer wrappingPaperPrice) {
        return builder()
                .wrappingPaperName(wrappingPaperName)
                .wrappingPaperPrice(wrappingPaperPrice)
                .build();
    }
}
