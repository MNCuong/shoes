package com.example.shoes_store.Controller;

import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Service.CartService;
import com.example.shoes_store.dto.CartItemRequest;
import com.example.shoes_store.dto.UpdateCartRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/update")
    public String updateCartItem(@RequestBody UpdateCartRequest request) {
        Cart updatedCart = cartService.updateCartItem(request.getCartItemId(), request.getQuantity());
        return "redirect:/cart";
    }


    @PostMapping("/add")
    public String addToCart(@RequestBody CartItemRequest cartItemRequest, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        cartService.addToCart(loggedInUser, cartItemRequest.getProductId(), cartItemRequest.getQuantity(), cartItemRequest.getSize());
        return "redirect:/cart";
    }


    @PostMapping("/remove/{cartId}/{productId}/{size}")
    @ResponseBody
    public ResponseEntity<String> removeProductFromCart(@PathVariable String cartId,
                                                        @PathVariable String productId,
                                                        @PathVariable String size) {
        log.info("Remove product from cart {}", cartId);
        log.info("Remove product from cart {}", productId);

        Long cartIdLong = Long.parseLong(cartId);
        Long productIdLong = Long.parseLong(productId);
        int sizeInt = Integer.parseInt(size);

        cartService.removeProductFromCart(cartIdLong, productIdLong, sizeInt);

        return ResponseEntity.ok("Xóa thành công");
    }


    @PostMapping("/checkout/{cartId}")
    public String checkout(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return "redirect:/order";
    }
}
