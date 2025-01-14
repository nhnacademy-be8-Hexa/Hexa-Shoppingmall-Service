package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.OrderBookRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long>, OrderBookRepositoryCustom {
//    @Query("SELECT SUM(ob.orderBookAmount) FROM OrderBook ob WHERE ob.order.orderId = :orderId AND ob.book.bookId = :bookId")
//    Long sumOrderBookAmountByOrderIdAndBookId(@Param("orderId") Long orderId, @Param("bookId") Long bookId);
    boolean existsByOrder_Member_MemberIdAndBook_BookId(String memberId, Long bookId);

//    @Query("SELECT ob.order.orderId AS orderId, " +
//            "ob.book.bookId AS bookId, " +
//            "ob.book.bookTitle AS bookTitle, " +
//            "ob.orderBookAmount AS orderBookAmount, " +
//            "ob.book.bookPrice AS bookPrice, " +
//            "ob.couponId AS couponId " +
//            "FROM OrderBook ob " +
//            "WHERE ob.order.orderId = :orderId")
//    List<OrderBookProjection> findOrderBooksByOrderId(@Param("orderId") Long orderId);

    Boolean existsByOrder_OrderId(Long orderOrderId);
}
