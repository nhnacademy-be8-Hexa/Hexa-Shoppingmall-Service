package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.RatingRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.service.member.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @DeleteMapping("/api/ratings/{ratingId}")
    public ResponseEntity<Rating> deleteRating(@PathVariable Long ratingId) {
        Rating rating = ratingService.getRating(ratingId);
        if (Objects.isNull(rating)) {
            throw new RatingNotFoundException(Long.toString(ratingId));
        }
        try {
            ratingService.deleteRating(ratingId);
        } catch (RuntimeException e) {
            throw new SqlQueryExecuteFailException(e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/ratings/{ratingId}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long ratingId, @RequestBody RatingRequestDTO ratingRequestDTO) {
        return ResponseEntity.ok(ratingService.updateRating(ratingId, ratingRequestDTO));
    }
}
