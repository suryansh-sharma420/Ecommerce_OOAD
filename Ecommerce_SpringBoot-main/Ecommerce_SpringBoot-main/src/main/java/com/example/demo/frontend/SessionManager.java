package com.example.demo.frontend;

import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;

/**
 * Singleton class for managing user session information across screens
 */
public class SessionManager {
    private static SessionManager instance;
    
    private User currentUser;
    private Cart cart;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public Cart getCart() {
        return cart;
    }
    
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    
    public ProductService getProductService() {
        return productService;
    }
    
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
    
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public UserService getUserService() {
        return userService;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void clearSession() {
        currentUser = null;
        cart = null;
        // Don't clear services as they're shared resources
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole().toString().equals("ADMIN");
    }
} 