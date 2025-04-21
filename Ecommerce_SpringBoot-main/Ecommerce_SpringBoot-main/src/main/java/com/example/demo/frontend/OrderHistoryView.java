package com.example.demo.frontend;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;

import javafx.beans.property.SimpleStringProperty;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class OrderHistoryView {
    private final OrderService orderService;
    private Stage stage;
    private TableView<Order> orderTable;
    
    @Autowired
    public OrderHistoryView(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Your Order History");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create order table
        createOrderTable();
        
        // Load order data
        loadOrders();
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new CustomerDashboard(stage).show());
        
        // Add components to root
        root.getChildren().addAll(titleLabel, orderTable, backButton);
        
        // Set scene
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Order History");
        stage.setScene(scene);
        stage.show();
    }
    
    private void createOrderTable() {
        orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setPlaceholder(new Label("You have no orders yet"));
        
        // Order ID column
        TableColumn<Order, String> idColumn = new TableColumn<>("Order #");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId().toString()));
        
        // Order date column
        TableColumn<Order, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(data.getValue().getOrderDate().format(formatter));
        });
        
        // Total amount column
        TableColumn<Order, String> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(data -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(data.getValue().getTotalAmount()));
        });
        
        // Status column
        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
        
        // Items count column
        TableColumn<Order, String> itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getOrderItems() != null ? 
            String.valueOf(data.getValue().getOrderItems().size()) : "0"));
        
        // Action column
        TableColumn<Order, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<Order, Void>() {
            private final Button detailsButton = new Button("View Details");
            
            {
                detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                detailsButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
        
        orderTable.getColumns().addAll(idColumn, dateColumn, totalColumn, statusColumn, itemsColumn, actionColumn);
    }
    
    private void loadOrders() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                List<Order> orders = orderService.getOrdersByUser(currentUser.getId());
                orderTable.setItems(FXCollections.observableArrayList(orders));
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load orders: " + e.getMessage());
        }
    }
    
    private void showOrderDetails(Order order) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Order Details");
        dialog.setHeaderText("Order #" + order.getId());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Order info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Label dateLabel = new Label("Date: " + order.getOrderDate().format(formatter));
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        Label totalLabel = new Label("Total: " + currencyFormat.format(order.getTotalAmount()));
        
        Label statusLabel = new Label("Status: " + order.getStatus());
        Label addressLabel = new Label("Shipping Address: " + (order.getShippingAddress() != null ? order.getShippingAddress() : "N/A"));
        Label paymentLabel = new Label("Payment Method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"));
        
        // Items table
        TableView<OrderItem> itemsTable = new TableView<>();
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<OrderItem, String> nameColumn = new TableColumn<>("Product");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        
        TableColumn<OrderItem, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(
            currencyFormat.format(data.getValue().getPrice())));
        
        TableColumn<OrderItem, String> qtyColumn = new TableColumn<>("Quantity");
        qtyColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getQuantity().toString()));
        
        TableColumn<OrderItem, String> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(data -> new SimpleStringProperty(
            currencyFormat.format(data.getValue().getSubtotal())));
        
        itemsTable.getColumns().addAll(nameColumn, priceColumn, qtyColumn, subtotalColumn);
        itemsTable.setItems(FXCollections.observableArrayList(order.getOrderItems()));
        itemsTable.setPrefHeight(200);
        
        // Add all to content
        content.getChildren().addAll(
            dateLabel, statusLabel, addressLabel, paymentLabel, totalLabel,
            new Separator(),
            new Label("Order Items:"),
            itemsTable
        );
        
        // Add close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);
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