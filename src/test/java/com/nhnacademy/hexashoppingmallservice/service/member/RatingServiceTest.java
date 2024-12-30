package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.dto.member.RatingRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void createRating_success() {
        Rating rating = Rating.of("Gold",20);
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating result = ratingService.createRating(rating);

        assertNotNull(result);
        assertEquals("Gold", result.getRatingName());
        assertEquals(20, result.getRatingPercent());
        verify(ratingRepository).save(rating);
    }

    @Test
    void getAllRatings_success() {
        List<Rating> ratings = List.of(
                Rating.of("Gold", 20),
                Rating.of("Silver", 15)
        );
        when(ratingRepository.findAll()).thenReturn(ratings);

        List<Rating> result = ratingService.getAllRatings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ratingRepository).findAll();
    }

    @Test
    void getRating_success() {
        Rating rating = Rating.of("Gold", 20);
        rating.setRatingId(1L);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        Rating result = ratingService.getRating(1L);

        assertNotNull(result);
        assertEquals("Gold", result.getRatingName());
        assertEquals(20, result.getRatingPercent());
        verify(ratingRepository).findById(1L);
    }

    @Test
    void getRating_notFound() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        Rating result = ratingService.getRating(1L);

        assertNull(result);
        verify(ratingRepository).findById(1L);
    }

    @Test
    void deleteRating_success() {
        Long id = 1L;

        doNothing().when(ratingRepository).deleteById(id);

        ratingService.deleteRating(id);

        verify(ratingRepository).deleteById(id);
    }

    @Test
    void updateRating_notFound() {
        Long id = 1L;
        RatingRequestDTO requestDTO = new RatingRequestDTO("Platinum", 25);

        when(ratingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RatingNotFoundException.class, () -> ratingService.updateRating(id, requestDTO));

        verify(ratingRepository).findById(id);
    }

    @Test
    void updateRating_partialUpdate() {
        Long id = 1L;
        Rating rating = Rating.of("Gold", 20);
        rating.setRatingId(id);
        RatingRequestDTO requestDTO = new RatingRequestDTO(null, 30);

        when(ratingRepository.findById(id)).thenReturn(Optional.of(rating));

        Rating result = ratingService.updateRating(id, requestDTO);

        assertNotNull(result);
        assertEquals("Gold", result.getRatingName()); // Name remains unchanged
        assertEquals(30, result.getRatingPercent()); // Percent updated
        verify(ratingRepository).findById(id);
    }
}