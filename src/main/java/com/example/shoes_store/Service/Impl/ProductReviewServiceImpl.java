package com.example.shoes_store.Service.Impl;

import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Entity.ProductReview;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.ProductReviewRepo;
import com.example.shoes_store.Service.ProductService;
import com.example.shoes_store.Service.ProductReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepo reviewRepo;
    private final ProductService productService;

    @Override
    public void saveReview(User user, Long productId, int rating, String comment) {

        Optional<ProductReview> existingReview = reviewRepo.findByUserIdAndProductId(user.getId(), productId);
        ProductReview review = existingReview.orElse(new ProductReview());
        Product product = productService.getProductById(productId).get();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepo.save(review);
    }
}
