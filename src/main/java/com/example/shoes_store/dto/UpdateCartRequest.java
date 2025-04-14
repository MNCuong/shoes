package com.example.shoes_store.dto;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long cartItemId;
    private Integer quantity;
}
