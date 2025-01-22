package com.nhnacademy.hexashoppingmallservice.dto.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointDetailsRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Validator 초기화
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPointDetailsRequestDTO() {
        // 유효한 데이터로 객체 생성
        PointDetailsRequestDTO dto = new PointDetailsRequestDTO(100, "Point added for order", LocalDateTime.now());

        // 유효성 검사
        Set<ConstraintViolation<PointDetailsRequestDTO>> violations = validator.validate(dto);

        // 유효성 검증에 실패한 사항이 없어야 함
        assertTrue(violations.isEmpty(), "Validation failed: " + violations);
    }

    @Test
    void testNullPointDetailsIncrement() {
        // pointDetailsIncrement가 null인 객체 생성
        PointDetailsRequestDTO dto = new PointDetailsRequestDTO(null, "Point added for order", LocalDateTime.now());

        // 유효성 검사
        Set<ConstraintViolation<PointDetailsRequestDTO>> violations = validator.validate(dto);

        // pointDetailsIncrement가 null이므로 validation 에러가 있어야 함
        assertFalse(violations.isEmpty(), "Validation should fail when pointDetailsIncrement is null");
    }

    @Test
    void testEmptyPointDetailsComment() {
        // pointDetailsComment가 비어있는 객체 생성
        PointDetailsRequestDTO dto = new PointDetailsRequestDTO(100, "", LocalDateTime.now());

        // 유효성 검사
        Set<ConstraintViolation<PointDetailsRequestDTO>> violations = validator.validate(dto);

        // pointDetailsComment가 빈 문자열이므로 validation 에러가 있어야 함
        assertFalse(violations.isEmpty(), "Validation should fail when pointDetailsComment is empty");
    }

    @Test
    void testPointDetailsCommentTooLong() {
        // pointDetailsComment가 200자를 초과하는 객체 생성
        String longComment = "A".repeat(201); // 201자의 문자열
        PointDetailsRequestDTO dto = new PointDetailsRequestDTO(100, longComment, LocalDateTime.now());

        // 유효성 검사
        Set<ConstraintViolation<PointDetailsRequestDTO>> violations = validator.validate(dto);

        // pointDetailsComment가 200자를 초과하므로 validation 에러가 있어야 함
        assertFalse(violations.isEmpty(), "Validation should fail when pointDetailsComment exceeds 200 characters");
    }

    @Test
    void testNullPointDetailsDatetime() {
        // pointDetailsDatetime가 null인 객체 생성
        PointDetailsRequestDTO dto = new PointDetailsRequestDTO(100, "Point added for order", null);

        // 유효성 검사
        Set<ConstraintViolation<PointDetailsRequestDTO>> violations = validator.validate(dto);

        // pointDetailsDatetime이 null이므로 validation 에러가 있어야 함
        assertFalse(violations.isEmpty(), "Validation should fail when pointDetailsDatetime is null");
    }
}
