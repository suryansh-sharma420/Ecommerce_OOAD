package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total orders
        stats.put("totalOrders", orderRepository.count());
        
        // Total revenue
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        
        // Total products
        stats.put("totalProducts", productRepository.count());
        
        // Total users
        stats.put("totalUsers", userRepository.count());
        
        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status.name(), orderRepository.countByStatus(status));
        }
        stats.put("ordersByStatus", ordersByStatus);
        
        return stats;
    }

    public Map<String, Object> getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();
        
        // Get orders in date range
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        // Calculate total sales
        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalSales", totalSales);
        
        // Number of orders
        report.put("numberOfOrders", orders.size());
        
        // Average order value
        BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO :
                totalSales.divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
        report.put("averageOrderValue", averageOrderValue);
        
        // Sales by product category
        Map<String, BigDecimal> salesByCategory = new HashMap<>();
        orders.forEach(order -> {
            order.getOrderItems().forEach(item -> {
                String category = item.getProduct().getCategory();
                BigDecimal amount = item.getSubtotal();
                salesByCategory.merge(category, amount, BigDecimal::add);
            });
        });
        report.put("salesByCategory", salesByCategory);
        
        return report;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAllByOrderByOrderDateDesc(PageRequest.of(0, limit));
    }
} 