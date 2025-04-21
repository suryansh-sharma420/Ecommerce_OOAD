package com.example.demo.frontend;

import com.example.demo.model.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a shopping cart that stores products and their quantities
 */
public class Cart {
    private Map<Product, Integer> items;
    
    public Cart() {
        this.items = new HashMap<>();
    }
    
    /**
     * Add a product to the cart
     * @param product Product to add
     * @param quantity Quantity to add
     */
    public void addProduct(Product product, int quantity) {
        // Look for the product by ID first
        Product existingProduct = findProductById(product.getId());
        if (existingProduct != null) {
            items.put(existingProduct, items.get(existingProduct) + quantity);
        } else {
            items.put(product, quantity);
        }
    }
    
    /**
     * Helper method to find a product in the cart by ID
     * @param productId ID of the product to find
     * @return Product if found, null otherwise
     */
    private Product findProductById(Long productId) {
        for (Product product : items.keySet()) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    /**
     * Remove a product from the cart
     * @param product Product to remove
     */
    public void removeProduct(Product product) {
        Product existingProduct = findProductById(product.getId());
        if (existingProduct != null) {
            items.remove(existingProduct);
        }
    }
    
    /**
     * Update the quantity of a product in the cart
     * @param product Product to update
     * @param quantity New quantity
     */
    public void updateQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            removeProduct(product);
        } else {
            Product existingProduct = findProductById(product.getId());
            if (existingProduct != null) {
                items.put(existingProduct, quantity);
            } else {
                items.put(product, quantity);
            }
        }
    }
    
    /**
     * Get all items in the cart
     * @return Map of products and their quantities
     */
    public Map<Product, Integer> getItems() {
        return items;
    }
    
    /**
     * Clear all items from the cart
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Calculate the total price of all items in the cart
     * @return Total price
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            BigDecimal quantity = new BigDecimal(entry.getValue());
            total = total.add(entry.getKey().getPrice().multiply(quantity));
        }
        return total;
    }
} 