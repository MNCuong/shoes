package com.example.shoes_store.Controller;


import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Entity.ProductReview;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.ProductReviewRepo;
import com.example.shoes_store.Service.CartItemService;
import com.example.shoes_store.Service.CartService;
import com.example.shoes_store.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ShopSingleController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductReviewRepo productReviewRepo;
    @Autowired
    CartItemService cartItemService;

    @GetMapping("/{productId}")
    public String getProductById(HttpSession session, @PathVariable("productId") Long productId, Model model) {
        Product product = productService.getProductById(productId).get();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            Optional<ProductReview> existingReview = productReviewRepo.findByUserIdAndProductId(loggedInUser.getId(), productId);
            existingReview.ifPresent(review -> model.addAttribute("userReview", review));
        }
        Double averageRating = productReviewRepo.getAverageRating(productId);
        if (averageRating != null) {
            averageRating = Math.round(averageRating * 10) / 10.0;
        }
        model.addAttribute("averageRating", averageRating);
        int cartItemQuantity = cartItemService.getQuantity(loggedInUser);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("product", product);
        return "/user/shop-single";
    }

}
