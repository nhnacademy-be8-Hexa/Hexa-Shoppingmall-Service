package com.nhnacademy.hexashoppingmallservice.controller.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.service.tag.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 전체 태그 수 조회 (검색 조건 포함)
    @GetMapping("/admin/tags/count")
    public ResponseEntity<Long> getTotalTags() {
        long totalTag = tagService.getTotal();
        return ResponseEntity.ok().body(totalTag);
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.findTagById(tagId));
    }

}
