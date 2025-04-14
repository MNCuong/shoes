package com.example.shoes_store.Service;

import com.example.shoes_store.Entity.Order;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {


    User login(String username, String password);

    List<User> getAllUsers();

    List<User> getAllUsersByRole(String role);

    User getUserById(Long id);

    List<Order> getOrdersByUserId(Long UserId);

    User registerUser(User user);

    boolean userExists(String username);

    void updateUser(User updatedUser);
}

