package com.nhnacademy.hexashoppingmallservice.repository.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    private Tag tag;

    @BeforeEach
    void setUp() {
        // 태그 초기 데이터 생성
        tag = Tag.of("Test Tag");
        tag = tagRepository.save(tag);
    }

    @Test
    @DisplayName("태그 저장 테스트")
    void saveTag() {
        Tag newTag = Tag.of("New Tag");
        Tag savedTag = tagRepository.save(newTag);

        assertThat(savedTag).isNotNull();
        assertThat(savedTag.getTagId()).isNotNull();
        assertThat(savedTag.getTagName()).isEqualTo("New Tag");
    }

    @Test
    @DisplayName("태그 조회 테스트")
    void findTagById() {
        Optional<Tag> foundTag = tagRepository.findById(tag.getTagId());

        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getTagName()).isEqualTo("Test Tag");
    }

    @Test
    @DisplayName("태그 업데이트 테스트")
    void updateTag() {
        tag = tagRepository.findById(tag.getTagId()).orElseThrow();
        tag.setTagName("Updated Tag");

        Tag updatedTag = tagRepository.save(tag);

        assertThat(updatedTag.getTagName()).isEqualTo("Updated Tag");
    }

    @Test
    @DisplayName("태그 삭제 테스트")
    void deleteTag() {
        tagRepository.delete(tag);

        Optional<Tag> deletedTag = tagRepository.findById(tag.getTagId());

        assertThat(deletedTag).isEmpty();
    }

    @Test
    @DisplayName("모든 태그 조회 테스트")
    void findAllTags() {
        Tag newTag = Tag.of("Another Tag");
        tagRepository.save(newTag);

        Iterable<Tag> tags = tagRepository.findAll();

        assertThat(tags).isNotNull();
        assertThat(tags).hasSize(2); // 기존 태그 + 새로 저장된 태그
    }
}
