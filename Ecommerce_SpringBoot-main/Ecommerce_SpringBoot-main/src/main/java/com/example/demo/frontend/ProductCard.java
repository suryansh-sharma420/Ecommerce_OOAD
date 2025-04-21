package com.example.demo.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class ProductCard extends VBox {
    private Product product;
    private Button addToCartButton;
    private static final double CARD_WIDTH = 200;
    private static final double IMAGE_SIZE = 150;

    public ProductCard(Product product) {
        this.product = product;
        setupCard();
    }

    private void setupCard() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setSpacing(5);
        setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: white;");
        setMinWidth(CARD_WIDTH);
        setMaxWidth(CARD_WIDTH);

        // Create and configure ImageView
        Image productImage = product.getImage();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        // Set image with error handling
        try {
            imageView.setImage(productImage);
            if (productImage.isError()) {
                System.err.println("Error loading image for product: " + product.getName());
            }
        } catch (Exception e) {
            System.err.println("Error setting image for product: " + product.getName() + " - " + e.getMessage());
        }

        // Create labels for product info
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setMaxWidth(CARD_WIDTH - 20);

        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 12px;");

        Label descLabel = new Label(product.getDescription());
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setStyle("-fx-font-size: 12px;");
        descLabel.setMaxWidth(CARD_WIDTH - 20);

        // Create add to cart button
        addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addToCartButton.setPrefWidth(CARD_WIDTH - 40);

        // Add all elements to the card
        getChildren().addAll(imageView, nameLabel, priceLabel, descLabel, addToCartButton);
    }

    public Button getAddToCartButton() {
        return addToCartButton;
    }
} 