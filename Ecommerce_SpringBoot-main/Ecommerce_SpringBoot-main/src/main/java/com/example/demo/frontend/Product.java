package com.example.demo.frontend;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

public class Product {
    private Long id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;

    public Product(Long id, String name, double price, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Image getImage() {
        String path = "/images/" + imageUrl;
        try {
            var resourceStream = getClass().getResourceAsStream(path);
            if (resourceStream == null) {
                System.err.println("Resource not found: " + path);
                return getPlaceholderImage();
            }
            return new Image(resourceStream);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path + " - Error: " + e.getMessage());
            return getPlaceholderImage();
        }
    }

    private Image getPlaceholderImage() {
        try {
            var placeholderStream = getClass().getResourceAsStream("/images/placeholder.jpg");
            if (placeholderStream == null) {
                System.err.println("Placeholder image not found!");
                // Return an empty image as last resort
                return new Image(new ByteArrayInputStream(new byte[0]));
            }
            return new Image(placeholderStream);
        } catch (Exception e) {
            System.err.println("Failed to load placeholder image: " + e.getMessage());
            // Return an empty image as last resort
            return new Image(new ByteArrayInputStream(new byte[0]));
        }
    }

    public String getDescription() {
        return description;
    }
} 