package com.example.demo.frontend;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class ProductListView {
    private final ProductService productService;
    private Stage stage;
    private VBox rootLayout;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private VBox productsContainer;
    
    @Autowired
    public ProductListView(ProductService productService) {
        this.productService = productService;
    }

    public void show(Stage stage) {
        this.stage = stage;
        
        // Create main layout
        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(20));
        
        // Add title
        Label titleLabel = new Label("Browse Products");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create search and filter section
        HBox filterSection = createFilterSection();
        
        // Create products container
        ScrollPane scrollPane = new ScrollPane();
        productsContainer = new VBox(10);
        scrollPane.setContent(productsContainer);
        scrollPane.setFitToWidth(true);
        
        // Add back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            // Get current user and check role to decide which dashboard to return to
            if (SessionManager.getInstance().getCurrentUser().getRole().toString().equals("ADMIN")) {
                new AdminDashboard(stage).show();
            } else {
                new CustomerDashboard(stage).show();
            }
        });
        
        // Add all components to root layout
        rootLayout.getChildren().addAll(titleLabel, filterSection, scrollPane, backButton);
        
        // Load initial products
        loadProducts();
        
        // Set the scene
        Scene scene = new Scene(rootLayout, 800, 600);
        stage.setTitle("Browse Products");
        stage.setScene(scene);
        stage.show();
    }
    
    private HBox createFilterSection() {
        HBox filterSection = new HBox(10);
        filterSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setPrefWidth(200);
        
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchProducts());
        
        // Category filter
        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by category");
        // Add categories from the database (we'll populate this with unique categories)
        categoryFilter.getItems().addAll("All", "Electronics", "Clothing", "Books");
        categoryFilter.setValue("All");
        
        // Apply filter button
        Button filterButton = new Button("Apply Filter");
        filterButton.setOnAction(e -> filterByCategory());
        
        filterSection.getChildren().addAll(searchField, searchButton, categoryFilter, filterButton);
        return filterSection;
    }
    
    private void loadProducts() {
        try {
            // Clear the current products
            productsContainer.getChildren().clear();
            
            // Get all products from the service
            List<Product> products = productService.getAllProducts();
            
            if (products.isEmpty()) {
                Label noProductsLabel = new Label("No products available.");
                productsContainer.getChildren().add(noProductsLabel);
            } else {
                // Display each product
                for (Product product : products) {
                    productsContainer.getChildren().add(createProductCard(product));
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
        }
    }
    
    private void searchProducts() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadProducts();
            return;
        }
        
        try {
            productsContainer.getChildren().clear();
            List<Product> products = productService.searchProducts(searchTerm);
            
            if (products.isEmpty()) {
                Label noProductsLabel = new Label("No products found matching '" + searchTerm + "'");
                productsContainer.getChildren().add(noProductsLabel);
            } else {
                for (Product product : products) {
                    productsContainer.getChildren().add(createProductCard(product));
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Search failed: " + e.getMessage());
        }
    }
    
    private void filterByCategory() {
        String category = categoryFilter.getValue();
        if (category == null || category.equals("All")) {
            loadProducts();
            return;
        }
        
        try {
            productsContainer.getChildren().clear();
            List<Product> products = productService.getProductsByCategory(category);
            
            if (products.isEmpty()) {
                Label noProductsLabel = new Label("No products found in category '" + category + "'");
                productsContainer.getChildren().add(noProductsLabel);
            } else {
                for (Product product : products) {
                    productsContainer.getChildren().add(createProductCard(product));
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Filter failed: " + e.getMessage());
        }
    }
    
    private GridPane createProductCard(Product product) {
        // Create card container
        GridPane card = new GridPane();
        card.setPadding(new Insets(10));
        card.setHgap(10);
        card.setVgap(5);
        card.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: white;");
        
        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        GridPane.setConstraints(nameLabel, 0, 0, 2, 1);
        
        // Product price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label priceLabel = new Label(currencyFormat.format(product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: green;");
        GridPane.setConstraints(priceLabel, 0, 1);
        
        // Product category
        Label categoryLabel = new Label("Category: " + product.getCategory());
        GridPane.setConstraints(categoryLabel, 0, 2);
        
        // Stock status
        Label stockLabel = new Label("In Stock: " + product.getStockQuantity());
        GridPane.setConstraints(stockLabel, 0, 3);
        
        // Add to cart button
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addToCartBtn.setOnAction(e -> addToCart(product));
        GridPane.setConstraints(addToCartBtn, 1, 3);
        
        // View details button
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setOnAction(e -> showProductDetails(product));
        GridPane.setConstraints(viewDetailsBtn, 1, 2);
        
        // Add all elements to the card
        card.getChildren().addAll(nameLabel, priceLabel, categoryLabel, stockLabel, addToCartBtn, viewDetailsBtn);
        
        return card;
    }
    
    private void addToCart(Product product) {
        // Get the cart from the session or create a new one
        Cart cart = SessionManager.getInstance().getCart();
        if (cart == null) {
            cart = new Cart();
            SessionManager.getInstance().setCart(cart);
        }
        
        // Add the product to the cart
        cart.addProduct(product, 1);
        
        // Show success message
        showAlert("Success", product.getName() + " added to your cart.");
    }
    
    private void showProductDetails(Product product) {
        // Create a dialog to show product details
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Product Details");
        dialog.setHeaderText(product.getName());
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label priceLabel = new Label("Price: " + currencyFormat.format(product.getPrice()));
        
        // Description
        Label descLabel = new Label("Description:");
        TextArea descText = new TextArea(product.getDescription());
        descText.setEditable(false);
        descText.setWrapText(true);
        descText.setPrefHeight(100);
        
        // Category
        Label categoryLabel = new Label("Category: " + product.getCategory());
        
        // Stock
        Label stockLabel = new Label("In Stock: " + product.getStockQuantity());
        
        // Add to cart button
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addToCartBtn.setOnAction(e -> {
            addToCart(product);
            dialog.close();
        });
        
        // Add all to content
        content.getChildren().addAll(priceLabel, categoryLabel, stockLabel, descLabel, descText, addToCartBtn);
        
        // Add close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 