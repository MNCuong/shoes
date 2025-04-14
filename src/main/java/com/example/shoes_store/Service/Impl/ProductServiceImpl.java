package com.example.shoes_store.Service.Impl;


import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Repo.ProductRepo;
import com.example.shoes_store.Service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepo productRepo;

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    @Override
    public List<Product> getAllProductsWithCateActive() {
        return productRepo.findByCategory_Active(true);
    }

    @Override
    public Product saveProduct(MultipartFile productImage, Product product) {
        if (!productImage.isEmpty()) {
            String originalFileName = productImage.getOriginalFilename();
            product.setImageUrl(originalFileName);
            Path uploadPath = Paths.get("src/main/resources/static/assets/img/products/");
            try {
                Files.createDirectories(uploadPath);
                assert originalFileName != null;
                productImage.transferTo(uploadPath.resolve(originalFileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return productRepo.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    @Override
    public Product updateProduct(MultipartFile productImage, Product product) {
        log.info("" +
                "Updating product with ID " + product.getId());
        if (productRepo.existsById(product.getId())) {
            if (!productImage.isEmpty()) {
                String originalFileName = productImage.getOriginalFilename();
                product.setImageUrl(originalFileName);
                Path uploadPath = Paths.get("src/main/resources/static/assets/img/products/");
                try {
                    Files.createDirectories(uploadPath);
                    assert originalFileName != null;
                    productImage.transferTo(uploadPath.resolve(originalFileName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return productRepo.save(product);
        } else {
            throw new EntityNotFoundException("Product with ID " + product.getId() + " not found");
        }
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepo.findByCategory_Id(categoryId);
    }

    @Override
    public Product toggleProductStatus(Long id) {
        Product category = productRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category with ID " + id + " not found"));

        category.setActive(!category.isActive());
        return productRepo.save(category);
    }

}
