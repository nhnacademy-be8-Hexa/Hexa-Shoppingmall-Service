package com.nhnacademy.hexashoppingmallservice.service.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.exception.tag.TagNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    public List<Tag> getAllTags(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);
        return page.getContent();
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
        // 먼저 book과의 관계 모두 삭제
        List<BookTag> bookTagList = bookTagRepository.findByTag_TagId(tagId);
        bookTagRepository.deleteAll(bookTagList);

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
