package com.example.shoes_store.Service;

import com.example.shoes_store.Repo.OrderItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepo orderItemRepo;
}
