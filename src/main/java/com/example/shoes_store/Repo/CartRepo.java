package com.example.shoes_store.Repo;

import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_IdOrderByIdDesc(Long userId);

    Optional<Cart> findByUser(User user);
}
