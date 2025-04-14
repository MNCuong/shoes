package com.example.shoes_store.Service;


import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.CartItem;
import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.CartRepo;
import com.example.shoes_store.Repo.CartItemRepo;
import com.example.shoes_store.Repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CartService {

    Cart getCartByUserId(Long userid);

    void updateCartTotalPrice(Cart cart);

    void removeProductFromCart(Long cartId, Long productId, int size);

    void clearCart(Long cartId);

    Cart updateCartItem(Long cartItemId, Integer quantity);

    void addToCart(User user, Long productId, int quantity, String size);

}
