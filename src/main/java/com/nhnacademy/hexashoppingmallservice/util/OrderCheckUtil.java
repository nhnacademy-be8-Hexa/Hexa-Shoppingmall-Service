package com.nhnacademy.hexashoppingmallservice.util;

import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


// order이 3개월 이전에 속하는지 체크하는 코드
@Component
public class OrderCheckUtil {
    public boolean isOrderedWithinLastThreeMonths(Order order) {
        // 오늘 자정(LocalDateTime) 구하기
        LocalDateTime nowAtMidnight = LocalDate.now().atStartOfDay();

        // 3개월 전 날짜 구하기
        LocalDateTime threeMonthsAgo = nowAtMidnight.minusMonths(3);

        // 주문 시간이 3개월 이전인지 확인
        return order.getOrderedAt().isAfter(threeMonthsAgo);
    }
}
