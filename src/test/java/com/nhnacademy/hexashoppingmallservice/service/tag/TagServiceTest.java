package com.nhnacademy.hexashoppingmallservice.service.tag;

import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.exception.tag.TagNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookTagRepository bookTagRepository;


    @Test
    @DisplayName("모든 태그 조회 테스트")
    void getAllTags() {
        Tag tag = Tag.of("Test Tag");
        Page<Tag> page = mock(Page.class);
        Pageable pageable = Pageable.unpaged();

        // 스텁 설정: Pageable.unpaged()를 전달할 때 Page<Tag> 반환
        when(tagRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(tag));

        // When
        List<Tag> tags = tagService.getAllTags(pageable);

        assertThat(tags).isNotEmpty();
        assertThat(tags.get(0).getTagName()).isEqualTo("Test Tag");
        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("태그 생성 테스트")
    void createTag() {
        TagRequestDTO requestDTO = new TagRequestDTO("New Tag");
        Tag tag = Tag.of(requestDTO.tagName());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        tagService.createTag(requestDTO);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 삭제 테스트")
    void deleteTag() {
        Long tagId = 1L;
        BookTag bookTag = mock(BookTag.class);
        when(bookTagRepository.findByTag_TagId(tagId)).thenReturn(List.of(bookTag));
        doNothing().when(bookTagRepository).deleteAll(anyList());
        doNothing().when(tagRepository).deleteById(tagId);

        tagService.deleteTag(tagId);

        verify(bookTagRepository, times(1)).findByTag_TagId(tagId);
        verify(bookTagRepository, times(1)).deleteAll(anyList());
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    @DisplayName("태그 업데이트 테스트")
    void updateTag() {
        Long tagId = 1L;
        TagRequestDTO requestDTO = new TagRequestDTO("Updated Tag");
        Tag tag = Tag.of("Old Tag");
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagService.updateTag(tagId, requestDTO);

        assertThat(tag.getTagName()).isEqualTo("Updated Tag");
        verify(tagRepository, times(1)).findById(tagId);
    }

    @Test
    @DisplayName("태그 업데이트 실패 테스트 - TagNotFoundException")
    void updateTagThrowsException() {
        Long tagId = 1L;
        TagRequestDTO requestDTO = new TagRequestDTO("Updated Tag");
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.updateTag(tagId, requestDTO))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("Tag: 1 not found.");

        verify(tagRepository, times(1)).findById(tagId);
    }
}
