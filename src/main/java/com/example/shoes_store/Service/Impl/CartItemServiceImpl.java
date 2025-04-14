package com.example.shoes_store.Service.Impl;

import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.CartItem;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.CartItemRepo;
import com.example.shoes_store.Repo.CartRepo;
import com.example.shoes_store.Service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    CartItemRepo cartItemRepo;
    @Autowired
    CartRepo cartRepo;

    @Override
    public int getQuantity(User user) {
        Optional<Cart> optionalCart = cartRepo.findByUser(user);
        if (optionalCart.isEmpty()) {
            return 0;
        }
        List<CartItem> items = cartItemRepo.findByCart(optionalCart.get());
        return items.size();
    }


}
