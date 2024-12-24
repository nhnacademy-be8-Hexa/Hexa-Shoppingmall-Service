package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.service.book.PublisherService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;
    private final JwtUtils jwtUtils;

    @GetMapping("/api/publishers")
    public List<Publisher> getPublishers(Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return publisherService.getAllPublisher(pageable);
    }

    @PostMapping("/api/publishers")
    public ResponseEntity<Publisher> createPublisher(@RequestBody @Valid Publisher publisher, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok().body(publisherService.createPublisher(publisher));
    }

    @GetMapping("/api/publishers/{publisherId}")
    public Publisher getPublisher(@PathVariable Long publisherId){
        return publisherService.getPublisher(publisherId);
    }
}
