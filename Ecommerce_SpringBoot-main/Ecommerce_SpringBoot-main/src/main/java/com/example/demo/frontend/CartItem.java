package com.example.demo.frontend;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CartItem {
    private final Product product;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty total;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(product.getPrice());
        this.total = new SimpleDoubleProperty(quantity * product.getPrice());
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int value) {
        quantity.set(value);
        total.set(value * price.get());
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }
} 