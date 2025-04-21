package com.example.demo.frontend;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CartView {
    private final OrderService orderService;
    private final ProductService productService;
    private Stage stage;
    private TableView<Map.Entry<Product, Integer>> cartTable;
    private Label totalLabel;
    
    @Autowired
    public CartView(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Your Shopping Cart");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create cart table
        createCartTable();
        
        // Create summary section
        VBox summarySection = createSummarySection();
        
        // Back button
        Button backButton = new Button("Continue Shopping");
        backButton.setOnAction(e -> {
            try {
                ProductListView productListView = new ProductListView(productService);
                productListView.show(stage);
            } catch (Exception ex) {
                showAlert("Error", "Failed to navigate back: " + ex.getMessage());
            }
        });
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        clearCartButton.setOnAction(e -> clearCart());
        
        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutButton.setOnAction(e -> proceedToCheckout());
        
        actionButtons.getChildren().addAll(clearCartButton, checkoutButton);
        
        // Add components to root
        root.getChildren().addAll(titleLabel, cartTable, summarySection, actionButtons, backButton);
        
        // Set scene
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Shopping Cart");
        stage.setScene(scene);
        stage.show();
        
        // Load cart items
        loadCartItems();
    }
    
    private void createCartTable() {
        cartTable = new TableView<>();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setPrefHeight(350);
        
        // Product column
        TableColumn<Map.Entry<Product, Integer>, String> productColumn = new TableColumn<>("Product");
        productColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey().getName()));
        productColumn.setPrefWidth(200);
        
        // Price column
        TableColumn<Map.Entry<Product, Integer>, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(data.getValue().getKey().getPrice()));
        });
        priceColumn.setPrefWidth(100);
        
        // Quantity column
        TableColumn<Map.Entry<Product, Integer>, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());
        quantityColumn.setCellFactory(col -> new TableCell<Map.Entry<Product, Integer>, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Spinner<Integer> spinner = new Spinner<>(1, 100, item);
                    spinner.setEditable(true);
                    spinner.setPrefWidth(80);
                    spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                        Map.Entry<Product, Integer> entry = getTableView().getItems().get(getIndex());
                        updateQuantity(entry.getKey(), newValue);
                    });
                    setGraphic(spinner);
                }
            }
        });
        quantityColumn.setPrefWidth(100);
        
        // Subtotal column
        TableColumn<Map.Entry<Product, Integer>, String> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(data -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            BigDecimal price = data.getValue().getKey().getPrice();
            Integer quantity = data.getValue().getValue();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            return new SimpleStringProperty(currencyFormat.format(subtotal));
        });
        subtotalColumn.setPrefWidth(100);
        
        // Action column
        TableColumn<Map.Entry<Product, Integer>, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<Map.Entry<Product, Integer>, Void>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    Map.Entry<Product, Integer> entry = getTableView().getItems().get(getIndex());
                    removeFromCart(entry.getKey());
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
        actionColumn.setPrefWidth(100);
        
        cartTable.getColumns().addAll(productColumn, priceColumn, quantityColumn, subtotalColumn, actionColumn);
    }
    
    private VBox createSummarySection() {
        VBox summarySection = new VBox(10);
        summarySection.setPadding(new Insets(10));
        summarySection.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        
        // Total price
        HBox totalRow = new HBox(10);
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        
        Label totalTitleLabel = new Label("Total:");
        totalTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        totalLabel = new Label("$0.00");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        totalRow.getChildren().addAll(totalTitleLabel, totalLabel);
        
        summarySection.getChildren().add(totalRow);
        return summarySection;
    }
    
    private void loadCartItems() {
        Cart cart = SessionManager.getInstance().getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            cartTable.setPlaceholder(new Label("Your cart is empty"));
            updateTotal(BigDecimal.ZERO);
            return;
        }
        
        ObservableList<Map.Entry<Product, Integer>> items = FXCollections.observableArrayList(cart.getItems().entrySet());
        cartTable.setItems(items);
        
        // Update total
        updateTotal(cart.calculateTotal());
    }
    
    private void updateQuantity(Product product, int newQuantity) {
        Cart cart = SessionManager.getInstance().getCart();
        if (cart != null) {
            cart.updateQuantity(product, newQuantity);
            updateTotal(cart.calculateTotal());
        }
    }
    
    private void removeFromCart(Product product) {
        Cart cart = SessionManager.getInstance().getCart();
        if (cart != null) {
            cart.removeProduct(product);
            loadCartItems(); // Reload to refresh the table
        }
    }
    
    private void clearCart() {
        Cart cart = SessionManager.getInstance().getCart();
        if (cart != null) {
            cart.clear();
            loadCartItems(); // Reload to refresh the table
        }
    }
    
    private void updateTotal(BigDecimal total) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        totalLabel.setText(currencyFormat.format(total));
    }
    
    private void proceedToCheckout() {
        Cart cart = SessionManager.getInstance().getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some products before checkout.");
            return;
        }
        
        // Create checkout dialog
        Dialog<OrderDTO> dialog = new Dialog<>();
        dialog.setTitle("Checkout");
        dialog.setHeaderText("Complete Your Order");
        
        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // Create the checkout form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField shippingAddressField = new TextField();
        shippingAddressField.setPromptText("Enter shipping address");
        
        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.getItems().addAll("Credit Card", "PayPal", "Cash on Delivery");
        paymentMethodComboBox.setValue("Credit Card");
        
        grid.add(new Label("Shipping Address:"), 0, 0);
        grid.add(shippingAddressField, 1, 0);
        grid.add(new Label("Payment Method:"), 0, 1);
        grid.add(paymentMethodComboBox, 1, 1);
        
        // Order summary
        Label summaryLabel = new Label("Order Summary:");
        summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        VBox summaryBox = new VBox(5);
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Label itemLabel = new Label(product.getName() + " x " + quantity + " = " + currencyFormat.format(itemTotal));
            summaryBox.getChildren().add(itemLabel);
        }
        
        // Add total
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label totalLabel = new Label("Total: " + currencyFormat.format(cart.calculateTotal()));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        VBox contentBox = new VBox(20);
        contentBox.getChildren().addAll(grid, new Separator(), summaryLabel, summaryBox, totalLabel);
        
        dialog.getDialogPane().setContent(contentBox);
        
        // Request focus on the shipping address field by default
        shippingAddressField.requestFocus();
        
        // Convert the result to OrderDTO when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                if (shippingAddressField.getText().isEmpty()) {
                    showAlert("Missing Information", "Please enter a shipping address.");
                    return null;
                }
                
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setShippingAddress(shippingAddressField.getText());
                orderDTO.setPaymentMethod(paymentMethodComboBox.getValue());
                
                List<OrderItemDTO> orderItems = new ArrayList<>();
                for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
                    Product product = entry.getKey();
                    Integer quantity = entry.getValue();
                    
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(product.getId());
                    itemDTO.setQuantity(quantity);
                    itemDTO.setPrice(product.getPrice());
                    
                    orderItems.add(itemDTO);
                }
                orderDTO.setOrderItems(orderItems);
                
                return orderDTO;
            }
            return null;
        });
        
        // Process the result
        dialog.showAndWait().ifPresent(orderDTO -> {
            try {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser == null) {
                    showAlert("Error", "You must be logged in to place an order.");
                    return;
                }
                
                Order placedOrder = orderService.createOrder(currentUser.getId(), orderDTO);
                
                // Clear the cart after successful order
                cart.clear();
                
                showAlert("Success", "Your order has been placed successfully! Order ID: " + placedOrder.getId());
                
                // Navigate to order history
                OrderHistoryView orderHistoryView = new OrderHistoryView(orderService);
                orderHistoryView.show(stage);
                
            } catch (Exception e) {
                showAlert("Error", "Failed to place order: " + e.getMessage());
            }
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 