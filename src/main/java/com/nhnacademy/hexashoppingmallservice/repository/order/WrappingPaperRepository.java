package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.projection.member.wrappingpaper.WrappingPaperProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrappingPaperRepository extends JpaRepository<WrappingPaper, Long> {
    List<WrappingPaperProjection> findAllBy();
}
