package com.example.demo.frontend.service;

import com.example.demo.frontend.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static final String API_BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Product> getAllProducts() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/products"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<com.example.demo.model.Product> backendProducts = objectMapper.readValue(
                    response.body(), 
                    new TypeReference<List<com.example.demo.model.Product>>() {}
                );
                
                return convertToFrontendProducts(backendProducts);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Product> getProductsByCategory(String category) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/products/category/" + category))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<com.example.demo.model.Product> backendProducts = objectMapper.readValue(
                    response.body(), 
                    new TypeReference<List<com.example.demo.model.Product>>() {}
                );
                
                return convertToFrontendProducts(backendProducts);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<Product> convertToFrontendProducts(List<com.example.demo.model.Product> backendProducts) {
        List<Product> frontendProducts = new ArrayList<>();
        for (com.example.demo.model.Product bp : backendProducts) {
            frontendProducts.add(new Product(
                bp.getId(),
                bp.getName(),
                bp.getPrice().doubleValue(),
                bp.getImageUrl(),
                bp.getDescription()
            ));
        }
        return frontendProducts;
    }

    public void addToCart(Long productId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/cart/add/" + productId))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                System.err.println("Error adding product to cart: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
        }
    }
} 