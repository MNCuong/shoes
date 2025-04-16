package com.example.shoes_store.Service.Impl;

import com.example.shoes_store.Entity.Order;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.UserRepo;
import com.example.shoes_store.Service.UserService;
import com.example.shoes_store.dto.ChangePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        log.info("login user:{}", user);
        log.info("login user:{}", user.getEmail());
        log.info("login user:{}", user.getUsername());
        log.info("login user:{}", user.getRole());

        if (user.getPassword().equals(password)) {
            log.info("login user:{}", user.getUsername());
            return user;
        }
        log.info("login loi:{}", user.getUsername());
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<User> getAllUsersByRole(String role) {
        return userRepo.findAllByRole(role);
    }

    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public List<Order> getOrdersByUserId(Long UserId) {
        return userRepo.findById(UserId).map(User -> User.getOrders()).orElse(null);
    }

    @Override
    public User registerUser(User user) {
        user.setRole("CUSTOMER");
        return userRepo.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public void updateUser(User updatedUser) {
        User existingUser = userRepo.findById(updatedUser.getId()).orElse(null);
        if (existingUser != null) {
            log.info("update user:{}", updatedUser.getUsername());
        }
        assert existingUser != null;
        updatedUser.setPassword(existingUser.getPassword());
        updatedUser.setRole(existingUser.getRole());

        userRepo.save(updatedUser);
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, User user) {
        if (!request.getOldPassword().equals(user.getPassword())) {
            return;
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return;
        }
        user.setPassword((request.getNewPassword()));
        userRepo.save(user);

    }

}

