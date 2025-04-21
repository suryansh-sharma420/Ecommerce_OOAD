package com.example.demo.frontend;

import com.example.demo.frontend.service.ProductService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class MainViewController {
    @FXML
    private GridPane productGrid;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button cartButton;
    
    private List<Product> products;
    private final ProductService productService;
    private CartController cartController;
    private Parent cartRoot;
    private FXMLLoader cartLoader;
    private Parent mainRoot;
    
    public MainViewController() {
        this.productService = new ProductService();
    }
    
    @FXML
    private void initialize() {
        updateLoginButton();
        if (loginButton != null && cartButton != null) {
            setupEventHandlers();
        }
        
        // Initialize cart view
        initializeCart();
        
        // Load products after UI is initialized
        Platform.runLater(() -> {
            if (productGrid != null) {
                mainRoot = productGrid.getScene().getRoot();
                if (cartController != null) {
                    cartController.setMainView(mainRoot);
                }
            }
            loadProducts();
        });
    }
    
    private void updateLoginButton() {
        if (loginButton != null) {
            if (SessionManager.getInstance().isLoggedIn()) {
                loginButton.setText("Logout");
            } else {
                loginButton.setText("Login");
            }
        }
    }
    
    private void initializeCart() {
        try {
            cartLoader = new FXMLLoader(getClass().getResource("/fxml/CartView.fxml"));
            cartRoot = cartLoader.load();
            cartController = cartLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to initialize cart view");
        }
    }
    
    private void loadProducts() {
        // Load products in a background thread
        new Thread(() -> {
            try {
                products = productService.getAllProducts();
                // Update UI in JavaFX thread
                Platform.runLater(this::displayProducts);
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error loading products", e.getMessage()));
            }
        }).start();
    }
    
    private void displayProducts() {
        if (productGrid != null) {
            productGrid.getChildren().clear();
            int column = 0;
            int row = 0;
            
            for (Product product : products) {
                ProductCard card = new ProductCard(product);
                card.getAddToCartButton().setOnAction(event -> handleAddToCart(product));
                
                productGrid.add(card, column, row);
                
                column++;
                if (column > 2) {  // 3 products per row
                    column = 0;
                    row++;
                }
            }
        }
    }
    
    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        cartButton.setOnAction(event -> handleCart());
        
        if (sidebar != null) {
            sidebar.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .forEach(button -> {
                    String category = button.getText();
                    if ("All Products".equals(category)) {
                        button.setOnAction(event -> loadProducts());
                    } else {
                        button.setOnAction(event -> handleCategorySelection(category));
                    }
                });
        }
    }
    
    private void handleAddToCart(Product product) {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showError("Error", "Please login to add items to cart");
            return;
        }

        if (cartController != null) {
            cartController.addToCart(product);
            showInfo("Success", "Added " + product.getName() + " to cart");
        } else {
            showError("Error", "Cart is not initialized");
        }
    }
    
    private void handleLogin() {
        if (SessionManager.getInstance().isLoggedIn()) {
            // Handle logout
            SessionManager.getInstance().clearSession();
            updateLoginButton();
            
            // Redirect to login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
                Parent loginView = loader.load();
                Scene scene = loginButton.getScene();
                scene.setRoot(loginView);
                
                // Clear any existing cart items or session data
                if (cartController != null) {
                    cartController = null;
                }
            } catch (IOException e) {
                showError("Error", "Failed to load login view");
                e.printStackTrace();
            }
        } else {
            // Handle login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
                Parent loginView = loader.load();
                Scene scene = loginButton.getScene();
                scene.setRoot(loginView);
            } catch (IOException e) {
                showError("Error", "Failed to load login view");
                e.printStackTrace();
            }
        }
    }
    
    private void handleCart() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showError("Error", "Please login to view cart");
            return;
        }

        if (cartRoot != null && cartButton != null) {
            Scene scene = cartButton.getScene();
            scene.setRoot(cartRoot);
        } else {
            showError("Error", "Failed to open cart: Cart view not initialized");
        }
    }
    
    private void handleCategorySelection(String category) {
        // Load category products in background thread
        new Thread(() -> {
            try {
                products = productService.getProductsByCategory(category);
                Platform.runLater(this::displayProducts);
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "Failed to load products for category: " + category));
            }
        }).start();
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 