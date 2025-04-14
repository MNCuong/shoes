package com.example.shoes_store.Repo;
import com.example.shoes_store.Entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductReviewRepo extends JpaRepository<ProductReview, Long> {
    Optional<ProductReview> findByUserIdAndProductId(Long userId, Long productId);
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId")
    Double getAverageRating(@Param("productId") Long productId);

}
