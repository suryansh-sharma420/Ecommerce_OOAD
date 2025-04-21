package com.example.demo.frontend;

import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CustomerDashboard {
    private Stage stage;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public CustomerDashboard(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.stage = null; // Will be set when show() is called
    }
    
    // Constructor for direct instantiation (used for navigation)
    public CustomerDashboard(Stage stage) {
        this.stage = stage;
        this.applicationContext = null; // Not available when directly instantiated
    }

    // Method for use when injected by Spring
    public void show(Stage stage) {
        this.stage = stage;
        show();
    }

    public void show() {
        if (stage == null) {
            throw new IllegalStateException("Stage must be set before calling show()");
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Button browseProductsBtn = new Button("Browse Products");
        Button viewCartBtn = new Button("View Cart");
        Button orderHistoryBtn = new Button("Order History");
        Button profileBtn = new Button("My Profile");
        Button logoutBtn = new Button("Logout");

        browseProductsBtn.setMaxWidth(Double.MAX_VALUE);
        viewCartBtn.setMaxWidth(Double.MAX_VALUE);
        orderHistoryBtn.setMaxWidth(Double.MAX_VALUE);
        profileBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        browseProductsBtn.setOnAction(e -> {
            try {
                // Get ProductListView from Spring context if possible
                ProductListView productListView;
                if (applicationContext != null) {
                    productListView = applicationContext.getBean(ProductListView.class);
                } else {
                    // Fallback: Get ProductService directly and create a new view
                    ProductService productService = SessionManager.getInstance().getProductService();
                    if (productService == null) {
                        throw new RuntimeException("Product service not available");
                    }
                    productListView = new ProductListView(productService);
                }
                productListView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing product list: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        viewCartBtn.setOnAction(e -> {
            try {
                // Get CartView from Spring context if possible
                CartView cartView;
                if (applicationContext != null) {
                    cartView = applicationContext.getBean(CartView.class);
                } else {
                    // Fallback: Get services directly and create a new view
                    ProductService productService = SessionManager.getInstance().getProductService();
                    OrderService orderService = SessionManager.getInstance().getOrderService();
                    if (productService == null || orderService == null) {
                        throw new RuntimeException("Required services not available");
                    }
                    cartView = new CartView(orderService, productService);
                }
                cartView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing cart view: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        orderHistoryBtn.setOnAction(e -> {
            try {
                // Get OrderHistoryView from Spring context if possible
                OrderHistoryView orderHistoryView;
                if (applicationContext != null) {
                    orderHistoryView = applicationContext.getBean(OrderHistoryView.class);
                } else {
                    // Fallback: Get OrderService directly and create a new view
                    OrderService orderService = SessionManager.getInstance().getOrderService();
                    if (orderService == null) {
                        throw new RuntimeException("Order service not available");
                    }
                    orderHistoryView = new OrderHistoryView(orderService);
                }
                orderHistoryView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing order history: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        profileBtn.setOnAction(e -> {
            try {
                // Get ProfileView from Spring context if possible
                ProfileView profileView;
                if (applicationContext != null) {
                    profileView = applicationContext.getBean(ProfileView.class);
                } else {
                    // Fallback: Get UserService directly and create a new view
                    UserService userService = SessionManager.getInstance().getUserService();
                    if (userService == null) {
                        throw new RuntimeException("User service not available");
                    }
                    profileView = new ProfileView(userService);
                }
                profileView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing profile view: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().clearSession();
            System.out.println("Logout clicked - Navigate back to Login Screen");
            stage.close();
        });

        root.getChildren().addAll(
            browseProductsBtn,
            viewCartBtn,
            orderHistoryBtn,
            profileBtn,
            logoutBtn
        );

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Customer Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
