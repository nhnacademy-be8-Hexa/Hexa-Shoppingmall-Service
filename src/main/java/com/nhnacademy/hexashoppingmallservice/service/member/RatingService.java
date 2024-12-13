package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.RatingRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    @Transactional
    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    @Transactional
    public Rating getRating(Long id) {
        return ratingRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    @Transactional
    public Rating updateRating(Long id, RatingRequestDTO ratingRequestDTO) {
        Rating rating = ratingRepository.findById(id).orElse(null);
        if (Objects.isNull(rating)) {
            throw new RatingNotFoundException(Long.toString(id));
        }
        if (ratingRequestDTO.getRatingName() != null && !ratingRequestDTO.getRatingName().isEmpty()) {
            rating.setRatingName(ratingRequestDTO.getRatingName());
        }
        if (ratingRequestDTO.getRatingPercent() != null && ratingRequestDTO.getRatingPercent() > 0) {
            rating.setRatingPercent(ratingRequestDTO.getRatingPercent());
        }
        return rating;
    }
}
