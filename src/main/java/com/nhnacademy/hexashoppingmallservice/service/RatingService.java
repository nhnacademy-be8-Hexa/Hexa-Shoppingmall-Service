package com.nhnacademy.hexashoppingmallservice.service;

import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
