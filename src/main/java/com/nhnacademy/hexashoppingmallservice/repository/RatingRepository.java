package com.nhnacademy.hexashoppingmallservice.repository;

import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
