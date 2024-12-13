package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.member.RatingRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.member.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = RatingController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void getRatings() throws Exception {
        List<Rating> ratings = List.of(
                new Rating("Gold", 20),
                new Rating("Silver", 15)
        );
        given(ratingService.getAllRatings()).willReturn(ratings);

        ratings.get(0).setRatingId(1L);
        ratings.get(1).setRatingId(2L);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/ratings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-ratings", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].ratingId").description("등급 ID").optional(),
                                fieldWithPath("[].ratingName").description("등급 이름"),
                                fieldWithPath("[].ratingPercent").description("등급 퍼센트")
                        )));
    }

    @Test
    void addRating() throws Exception {
        Rating rating = new Rating("Gold", 20);
        rating.setRatingId(1L);
        given(ratingService.createRating(any(Rating.class))).willReturn(rating);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ratingName").value("Gold"))
                .andDo(document("add-rating", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("ratingId").description("등급 ID").optional(),
                                fieldWithPath("ratingName").description("등급 이름"),
                                fieldWithPath("ratingPercent").description("등급 퍼센트")
                        ),
                        responseFields(
                                fieldWithPath("ratingId").description("등급 ID").optional(),
                                fieldWithPath("ratingName").description("등급 이름"),
                                fieldWithPath("ratingPercent").description("등급 퍼센트")
                        )));
    }

    @Test
    void deleteRating() throws Exception {
        Rating rating = new Rating("Gold", 20);
        rating.setRatingId(1L);

        given(ratingService.getRating(1L)).willReturn(rating);
        doNothing().when(ratingService).deleteRating(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/ratings/{ratingId}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("delete-rating",
                        pathParameters(
                                parameterWithName("ratingId").description("삭제할 등급 ID")
                        )));
    }

    @Test
    void deleteRating_notFound() throws Exception {
        given(ratingService.getRating(1L)).willReturn(null);
        doThrow(new RatingNotFoundException("1")).when(ratingService).deleteRating(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/ratings/{ratingId}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRating_sqlQueryExecuteFail() throws Exception {
        Rating rating = new Rating("Gold", 20);
        rating.setRatingId(1L);

        given(ratingService.getRating(1L)).willReturn(rating);
        doThrow(new SqlQueryExecuteFailException("Failed to delete rating"))
                .when(ratingService).deleteRating(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/ratings/{ratingId}", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateRating() throws Exception {
        RatingRequestDTO requestDTO = new RatingRequestDTO("Platinum", 25);
        Rating updatedRating = new Rating("Platinum", 25);
        updatedRating.setRatingId(1L);

        given(ratingService.updateRating(eq(1L), any(RatingRequestDTO.class))).willReturn(updatedRating);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/ratings/{ratingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingName").value("Platinum"))
                .andExpect(jsonPath("$.ratingPercent").value(25))
                .andDo(document("update-rating", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ratingId").description("수정할 등급 ID")
                        ),
                        requestFields(
                                fieldWithPath("ratingName").description("수정할 등급 이름").optional(),
                                fieldWithPath("ratingPercent").description("수정할 등급 퍼센트").optional()
                        ),
                        responseFields(
                                fieldWithPath("ratingId").description("등급 ID").optional(),
                                fieldWithPath("ratingName").description("등급 이름"),
                                fieldWithPath("ratingPercent").description("등급 퍼센트")
                        )));
    }

    @Test
    void updateRating_notFound() throws Exception {
        RatingRequestDTO requestDTO = new RatingRequestDTO("Platinum", 25);

        given(ratingService.updateRating(eq(1L), any(RatingRequestDTO.class))).willThrow(new RatingNotFoundException("1"));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/ratings/{ratingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }
}