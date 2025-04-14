package com.example.shoes_store.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String fullName;
    private String address;
    private String phone;
    private Long cartId;
}