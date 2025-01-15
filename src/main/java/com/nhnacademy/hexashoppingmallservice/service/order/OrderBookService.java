package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderBookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl.OrderBookRepositoryCustomImpl;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final OrderBookRepositoryCustomImpl orderBookRepositoryCustom;

    @Autowired
    public OrderBookService(OrderBookRepository orderBookRepository, OrderBookRepositoryCustomImpl orderBookRepositoryCustom) {
        this.orderBookRepository = orderBookRepository;
        this.orderBookRepositoryCustom = orderBookRepositoryCustom;
    }

    /**
     * 특정 주문 ID에 대한 주문 상세 정보를 가져옵니다.
     * @param orderId 주문 ID
     * @return 주문 상세 정보 리스트
     */
    public List<OrderBookDTO> getOrderBooksByOrderId(Long orderId) {

        if(!orderBookRepository.existsByOrder_OrderId(orderId)){
            throw new OrderBookNotFoundException("주문 ID " + orderId + "에 해당하는 주문이 존재하지 않습니다.");
        }

        List<OrderBookDTO> orderBooks =  orderBookRepositoryCustom.findOrderBooksByOrderId(orderId);


        return orderBooks;
    }
}
