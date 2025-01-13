package com.nhnacademy.hexashoppingmallservice.util;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberRatingUtils {

    final String RATING_NORMAL = "Normal";
    final String RATING_ROYAL = "Royal";
    final String RATING_Gold = "Gold";
    final String RATING_PLATINUM = "Platinum";

    final Long ROYAL_AMOUNT = 100000L;
    final Long GOLD_AMOUNT = 200000L;
    final Long PLATINUM_AMOUNT = 300000L;


    private final RatingRepository ratingRepository;

    final Rating royal;
    final Rating gold ;
    final Rating platimum;

    public MemberRatingUtils(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
        royal =  ratingRepository.findByRatingName(RATING_ROYAL);
        gold =  ratingRepository.findByRatingName(RATING_Gold);
        platimum =ratingRepository.findByRatingName(RATING_PLATINUM);

    }

    public Member refreshRating(Member member , MemberOrderSummary3M memberOrderSummary3M){
        int totalOrderPrice = memberOrderSummary3M.getTotalOrderPrice();

        if(totalOrderPrice>=ROYAL_AMOUNT){
            member.setRating(royal);
        }
        if (totalOrderPrice>=GOLD_AMOUNT) {
            member.setRating(gold);
        }
        if (totalOrderPrice>=PLATINUM_AMOUNT) {
            member.setRating(platimum);
        }
        return member;
    }
}
