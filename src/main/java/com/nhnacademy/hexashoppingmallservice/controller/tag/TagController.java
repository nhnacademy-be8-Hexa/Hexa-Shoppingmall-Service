package com.nhnacademy.hexashoppingmallservice.controller.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.service.tag.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;

    @PostMapping("/admin/tags")
    public ResponseEntity<Void> createTag(
            @RequestBody @Valid TagRequestDTO requestDTO
    ) {
        tagService.createTag(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/admin/tags/{tagId}")
    public ResponseEntity<Void> updateTag(
            @RequestBody @Valid TagRequestDTO requestDTO,
            @PathVariable Long tagId
    ) {
        tagService.updateTag(tagId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags(Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    @DeleteMapping("/admin/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Long tagId
    ) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }

}
