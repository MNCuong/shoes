package com.example.shoes_store.Repo;

import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.CartItem;
import com.example.shoes_store.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByCartAndProductAndSize(Cart cart, Product product, String size);

    List<CartItem> findByCart(Cart cart);
}
