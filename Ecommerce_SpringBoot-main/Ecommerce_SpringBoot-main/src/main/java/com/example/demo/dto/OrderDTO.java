package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private List<OrderItemDTO> orderItems;
    private String shippingAddress;
    private String paymentMethod;
} 