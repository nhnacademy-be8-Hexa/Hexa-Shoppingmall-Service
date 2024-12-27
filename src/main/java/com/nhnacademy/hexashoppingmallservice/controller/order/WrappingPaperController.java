package com.nhnacademy.hexashoppingmallservice.controller.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.WrappingPaperRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.order.WrappingPaperNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.order.WrappingPaperService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wrappingPaper")
public class WrappingPaperController {
    private final WrappingPaperService wrappingPaperService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public List<WrappingPaper> getAllWrappingPapers() {
        return wrappingPaperService.getAllWrappingPaper();
    }

    @PostMapping
    public ResponseEntity<WrappingPaper> createWrappingPaper(
            @Valid @RequestBody WrappingPaperRequestDTO wrappingPaperRequestDTO, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.status(201).body(wrappingPaperService.createWrappingPaper(wrappingPaperRequestDTO));
    }


    @GetMapping("/{wrappingPaperId}")
    public WrappingPaper getWrappingPaper(@PathVariable Long wrappingPaperId) {
        return wrappingPaperService.getWrappingPaper(wrappingPaperId);
    }

    @PutMapping("/{wrappingPaperId}")
    public ResponseEntity<WrappingPaper> updateWrappingPaper(@PathVariable Long wrappingPaperId,
                                                             @Valid @RequestBody
                                                             WrappingPaperRequestDTO wrappingPaperRequestDTO,
                                                             HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(wrappingPaperService.updateWrappingPaper(wrappingPaperId, wrappingPaperRequestDTO));
    }

    @DeleteMapping("/{wrappingPaperId}")
    public ResponseEntity<WrappingPaper> deleteWrappingPaper(@PathVariable Long wrappingPaperId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        WrappingPaper wrappingPaper = wrappingPaperService.getWrappingPaper(wrappingPaperId);
        if (Objects.isNull(wrappingPaper)) {
            throw new WrappingPaperNotFoundException(Long.toString(wrappingPaperId));
        }
        try {
            wrappingPaperService.deleteWrappingPaper(wrappingPaperId);
        } catch (RuntimeException e) {
            throw new SqlQueryExecuteFailException(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
