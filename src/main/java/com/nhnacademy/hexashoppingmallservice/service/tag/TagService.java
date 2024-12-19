package com.nhnacademy.hexashoppingmallservice.service.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.exception.tag.TagNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional
    public void createTag(TagRequestDTO requestDTO){
        Tag tag = Tag.of(
                requestDTO.tagName()
        );

        tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }

    @Transactional
    public void updateTag(Long tagId, TagRequestDTO requestDTO) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(
                ()-> new TagNotFoundException("Tag: %d not found.".formatted(tagId))
        );

        tag.setTagName(requestDTO.tagName());
    }

}
