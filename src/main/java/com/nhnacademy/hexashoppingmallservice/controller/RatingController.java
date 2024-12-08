package com.nhnacademy.hexashoppingmallservice.controller;

import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/api/ratings")
    public List<Rating> getRatings() {
        return ratingService.getAllRatings();
    }

    @PostMapping("/api/ratings")
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        return ResponseEntity.status(201).body(ratingService.createRating(rating));
    }
}
