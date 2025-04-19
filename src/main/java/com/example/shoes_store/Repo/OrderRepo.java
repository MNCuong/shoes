package com.example.shoes_store.Repo;

import com.example.shoes_store.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser_IdOrderByIdDesc(Long userId);

    Order save(Order order);

    @Query("SELECT FUNCTION('DAY', o.orderDate) AS day, SUM(o.totalPrice) AS revenue " +
            "FROM Order o " +
            "WHERE FUNCTION('MONTH', o.orderDate) = :month AND FUNCTION('YEAR', o.orderDate) = :year " +
            "AND o.status = 'DELIVERED' " +
            "GROUP BY FUNCTION('DAY', o.orderDate) " +
            "ORDER BY day")
    List<Object[]> getRevenueByDayInMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
