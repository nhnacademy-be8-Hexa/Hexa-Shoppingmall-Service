package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestControllerAdviceTest.TestController.class)
@Import(RestControllerAdvice.class) // RestControllerAdvice를 명시적으로 등록
class RestControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/conflict")
        public void throwConflictException() {
            throw new ResourceConflictException("Conflict occurred");
        }

        @GetMapping("/not-found")
        public void throwNotFoundException() {
            throw new ResourceNotFoundException("Resource not found");
        }

        @GetMapping("/sql-error")
        public void throwSqlQueryExecuteFailException() {
            throw new SqlQueryExecuteFailException("SQL execution failed");
        }
    }

    @Test
    void handleAlreadyExistException_shouldReturnConflict() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict());
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleSqlQueryExecuteFailException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/sql-error"))
                .andExpect(status().isInternalServerError());
    }
}