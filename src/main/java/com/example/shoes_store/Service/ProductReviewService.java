package com.example.shoes_store.Service;

import com.example.shoes_store.Entity.User;

public interface ProductReviewService {
    void saveReview(User user, Long product, int rating, String comment);
}
