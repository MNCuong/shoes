package com.example.shoes_store.Service.Impl;


import com.example.shoes_store.Entity.Cart;
import com.example.shoes_store.Entity.CartItem;
import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Repo.CartRepo;
import com.example.shoes_store.Repo.CartItemRepo;
import com.example.shoes_store.Repo.ProductRepo;
import com.example.shoes_store.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public Cart getCartByUserId(Long userid) {
        return cartRepo.findByUser_IdOrderByIdDesc(userid).orElse(new Cart());
    }

    @Override
    public void updateCartTotalPrice(Cart cart) {
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
        cartRepo.save(cart);
    }

    @Override
    public void removeProductFromCart(Long cartId, Long productId, int size) {
        Optional<Cart> cartOptional = cartRepo.findById(cartId);
        Optional<Product> productOptional = productRepo.findById(productId);

        if (cartOptional.isPresent() && productOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Product product = productOptional.get();

            Optional<CartItem> cartItemOpt = cartItemRepo.findByCartAndProductAndSize(cart, product, size + "");
            if (cartItemOpt.isPresent()) {
                CartItem cartItem = cartItemOpt.get();
                cartItemRepo.delete(cartItem);
                updateCartTotalPrice(cart);
            }
        }
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> items = new ArrayList<>(cart.getCartItems());

        cart.getCartItems().clear();
        cartItemRepo.deleteAll(items);

        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepo.save(cart);
    }

    @Override
    public Cart updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);
        BigDecimal newTotalPrice = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(quantity));
        cartItem.setTotalPrice(newTotalPrice);

        cartItemRepo.save(cartItem);
        Cart cart = cartItem.getCart();
        BigDecimal totalCartPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalCartPrice);

        cartRepo.save(cart);

        return cart;
    }

    @Transactional
    @Override
    public void addToCart(User user, Long productId, int quantity, String size) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = productOpt.get();

        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalPrice(BigDecimal.ZERO);
            newCart.setCartItems(new ArrayList<>());
            return cartRepo.save(newCart);
        });
        List<CartItem> cartItems;
        Optional<CartItem> existingItemOpt;

        if (cart.getCartItems() != null) {
            cartItems = cart.getCartItems();
            existingItemOpt = cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId) && item.getSize().equals(size))
                    .findFirst();
        } else {
            existingItemOpt = Optional.empty();
        }

        if (existingItemOpt.isPresent()) {
            CartItem cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(newQuantity);
            BigDecimal newItemTotal = product.getPrice().multiply(BigDecimal.valueOf(newQuantity));
            cartItem.setTotalPrice(newItemTotal);
            cartItemRepo.save(cartItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSize(size);
            BigDecimal newItemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            newItem.setTotalPrice(newItemTotal);
            cart.getCartItems().add(newItem);
            cartItemRepo.save(newItem);
        }
        BigDecimal cartTotal = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(cartTotal);
        cartRepo.save(cart);
    }
}
