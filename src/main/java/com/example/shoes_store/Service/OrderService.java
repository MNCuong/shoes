package com.example.shoes_store.Service;

import com.example.shoes_store.Entity.*;
import com.example.shoes_store.Repo.*;
import com.example.shoes_store.dto.OrderItemRequest;
import com.example.shoes_store.dto.OrderRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


public interface OrderService {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    void updateStatus(Long orderId, String status);

//    List<Order> getOrdersByUserId(Long userId);

    List<Order> getOrderByUser(User user);

    Map<String, String> getRevenueByDay(int month, int year);

    Boolean addOrder(OrderRequest request);
    Map<Integer, BigDecimal> getRevenueByQuarter(int quarter, int year);
}
