package com.example.shoes_store.Controller;


import com.example.shoes_store.Entity.Order;
import com.example.shoes_store.Service.OrderService;
import com.example.shoes_store.dto.OrderItemRequest;
import com.example.shoes_store.dto.OrderRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
        }
        return "admin/order-details";
    }


    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(orderId, status);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật trạng thái đơn hàng!");
            e.printStackTrace();
        }
        return "redirect:/admin/home#order";
    }

    @PostMapping("/add-order")
    public String addOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.addOrder(orderRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/order";
    }

    @GetMapping("/revenue-by-day")
    @ResponseBody
    public Map<String, String> getRevenueByDay(@RequestParam int month, @RequestParam int year) {
        return orderService.getRevenueByDay(month, year);
    }
    @GetMapping("/revenue-by-quarter")
    @ResponseBody
    public Map<Integer, BigDecimal> getRevenueByQuarter(@RequestParam int quarter, @RequestParam int year) {
        return orderService.getRevenueByQuarter(quarter, year);
    }

}
