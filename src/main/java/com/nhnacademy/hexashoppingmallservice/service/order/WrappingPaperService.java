package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.WrappingPaperRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.order.WrappingPaperNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.WrappingPaperRepository;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WrappingPaperService {

    private final WrappingPaperRepository wrappingPaperRepository;

    @Transactional(readOnly = true)
    public WrappingPaper getWrappingPaper(Long wrappingPaperId) {
        if (!wrappingPaperRepository.existsById(wrappingPaperId)) {
            throw new WrappingPaperNotFoundException("WrappingPaper Id %s not found".formatted(wrappingPaperId));
        }
        return wrappingPaperRepository.findById(wrappingPaperId).orElseThrow();
    }

    @Transactional
    public WrappingPaper createWrappingPaper(WrappingPaperRequestDTO wrappingPaperRequestDTO) {
        WrappingPaper wrappingPaper = WrappingPaper.of(
                wrappingPaperRequestDTO.getWrappingPaperName(),
                wrappingPaperRequestDTO.getWrappingPaperPrice()
        );
        return wrappingPaperRepository.save(wrappingPaper);
    }


    @Transactional(readOnly = true)
    public List<WrappingPaper> getAllWrappingPaper() {
        return wrappingPaperRepository.findAll();
    }


    @Transactional
    public WrappingPaper updateWrappingPaper(Long wrappingPaperId, WrappingPaperRequestDTO wrappingPaperRequestDTO) {
        WrappingPaper wrappingPaper = wrappingPaperRepository.findById(wrappingPaperId).orElse(null);
        if (Objects.isNull(wrappingPaper)) {
            throw new WrappingPaperNotFoundException("WrappingPaper Id %s not found".formatted(wrappingPaperId));
        }
        updateIfNotNull(wrappingPaperRequestDTO.getWrappingPaperName(), wrappingPaper::setWrappingPaperName);
        updateIfNotNull(wrappingPaperRequestDTO.getWrappingPaperPrice(), wrappingPaper::setWrappingPaperPrice);
        return wrappingPaper;
    }

    @Transactional
    public void deleteWrappingPaper(Long wrappingPaperId) {
        wrappingPaperRepository.deleteById(wrappingPaperId);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }

}
