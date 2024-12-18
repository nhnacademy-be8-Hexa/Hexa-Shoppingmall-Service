package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.service.book.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping("api/publishers")
    public List<Publisher> getPublishers(){
        return publisherService.getAllPublisher();
    }

    @PostMapping("/api/publishers")
    public ResponseEntity<Publisher> createPublisher(@RequestBody @Valid Publisher publisher){
        return ResponseEntity.ok().body(publisherService.createPublisher(publisher));
    }

    @GetMapping("api/publishers/{publisherId}")
    public Publisher getPublisher(@PathVariable Long publisherId){
        return publisherService.getPublisher(publisherId);
    }
}
