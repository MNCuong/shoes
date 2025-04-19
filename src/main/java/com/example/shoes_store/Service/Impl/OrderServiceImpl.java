package com.example.shoes_store.Service.Impl;

import com.example.shoes_store.Entity.*;
import com.example.shoes_store.Repo.*;
import com.example.shoes_store.Service.CartService;
import com.example.shoes_store.Service.OrderService;
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

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    HttpSession session;
    @Autowired
    private OrderItemRepo orderItemRepo;

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepo.findById(id);
    }

    @Override
    public void updateStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if ("CANCEL".equalsIgnoreCase(status)) {
            if (!"DELIVERED".equals(order.getStatus()) && !"CANCEL".equals(order.getStatus())) {
                order.setStatus("CANCEL");
            } else {
                throw new IllegalStateException("Không thể hủy đơn đã giao hoặc đã bị hủy");
            }
        } else {
            switch (order.getStatus()) {
                case "PENDING":
                    order.setStatus("CONFIRMED");
                    break;
                case "CONFIRMED":
                    order.setStatus("PROCESSING");
                    break;
                case "PROCESSING":
                    order.setStatus("SHIPPING");
                    break;
                case "SHIPPING":
                    order.setStatus("DELIVERED");
                    break;
                default:
                    throw new IllegalStateException("Trạng thái không hợp lệ hoặc không thể cập nhật");
            }
        }

        orderRepo.save(order);
    }


//    @Override
//    public List<Order> getOrdersByUserId(Long userId) {
//        return orderRepo.findByUser_IdOrderByIdDesc(userId);
//    }

    @Override
    public List<Order> getOrderByUser(User user) {
        return orderRepo.findByUser_IdOrderByIdDesc(user.getId());
    }

    @Override
    public Map<String, String> getRevenueByDay(int month, int year) {
        List<Object[]> results = orderRepo.getRevenueByDayInMonth(month, year);
        Map<String, String> revenueMap = new LinkedHashMap<>();
        for (Object[] row : results) {
            if (row[0] instanceof Number) {
                String day = String.valueOf(((Number) row[0]).intValue());
                String revenue = (row[1] instanceof BigDecimal) ? ((BigDecimal) row[1]).toString() : "0.0";
                revenueMap.put(day, revenue);
            } else {
                System.err.println("Dữ liệu không hợp lệ tại row[0]: " + row[0]);
            }
        }
        return revenueMap;
    }

    @Transactional
    @Override
    public Boolean addOrder(OrderRequest request) {
        try {
            Order order = new Order();
            User user = (User) session.getAttribute("loggedInUser");
            order.setUser(user);
            order.setStatus("PENDING");
            order.setAddress(request.getAddress());
            order.setFullName(request.getFullName());
            order.setPhone(request.getPhone());
            order.setOrderDate(LocalDateTime.now());

            Cart cart = cartRepo.findById(request.getCartId())
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            BigDecimal total = cart.getTotalPrice();
            List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());

            List<OrderItem> items = new ArrayList<>();
            for (CartItem item : cartItems) {
                OrderItem orderItem = new OrderItem();
                Product product = item.getProduct();

                int newQuantity = product.getStockQuantity() - item.getQuantity();
                if (newQuantity < 0) {
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho");
                }
                product.setStockQuantity(newQuantity);
                productRepo.save(product);
                orderItem.setProduct(item.getProduct());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getTotalPrice());
                orderItem.setOrder(order);
                orderItemRepo.save(orderItem);
                items.add(orderItem);
            }
            String orderCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            order.setCode(orderCode);
            order.setOrderItems(items);
//            BigDecimal total = items.stream()
//                    .map(OrderItem::getPrice)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalPrice(total);
            orderRepo.save(order);
            cartService.clearCart(cart.getId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
