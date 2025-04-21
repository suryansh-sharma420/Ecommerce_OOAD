package com.example.demo.frontend;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.OrderService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class OrderManagementView {
    private final OrderService orderService;
    private Stage stage;
    private TableView<Order> orderTable;
    private ComboBox<String> statusFilter;
    
    @Autowired
    public OrderManagementView(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Order Management");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create filter section
        HBox filterSection = createFilterSection();
        
        // Create order table
        createOrderTable();
        
        // Load all orders
        loadOrders(null);
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new AdminDashboard(stage).show());
        
        // Add components to root
        root.getChildren().addAll(titleLabel, filterSection, orderTable, backButton);
        
        // Set scene
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Order Management");
        stage.setScene(scene);
        stage.show();
    }
    
    private HBox createFilterSection() {
        HBox filterSection = new HBox(10);
        filterSection.setPadding(new Insets(5));
        
        // Status filter
        Label statusLabel = new Label("Filter by status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().add("ALL");
        statusFilter.getItems().addAll(Arrays.stream(OrderStatus.values())
                .map(Enum::toString)
                .collect(Collectors.toList()));
        statusFilter.setValue("ALL");
        
        // Filter button
        Button filterButton = new Button("Apply Filter");
        filterButton.setOnAction(e -> {
            String selectedStatus = statusFilter.getValue();
            if ("ALL".equals(selectedStatus)) {
                loadOrders(null);
            } else {
                loadOrders(OrderStatus.valueOf(selectedStatus));
            }
        });
        
        // Reset button
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            statusFilter.setValue("ALL");
            loadOrders(null);
        });
        
        filterSection.getChildren().addAll(statusLabel, statusFilter, filterButton, resetButton);
        return filterSection;
    }
    
    private void createOrderTable() {
        orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setPrefHeight(450);
        
        // Order ID column
        TableColumn<Order, String> idColumn = new TableColumn<>("Order #");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId().toString()));
        idColumn.setPrefWidth(60);
        
        // User column
        TableColumn<Order, String> userColumn = new TableColumn<>("Customer");
        userColumn.setCellValueFactory(data -> {
            String fullName = data.getValue().getUser().getFirstName() + " " + 
                              data.getValue().getUser().getLastName();
            return new SimpleStringProperty(fullName);
        });
        userColumn.setPrefWidth(120);
        
        // Email column
        TableColumn<Order, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getUser().getEmail()));
        emailColumn.setPrefWidth(150);
        
        // Order date column
        TableColumn<Order, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(data.getValue().getOrderDate().format(formatter));
        });
        dateColumn.setPrefWidth(120);
        
        // Total amount column
        TableColumn<Order, String> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(data -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormat.format(data.getValue().getTotalAmount()));
        });
        totalColumn.setPrefWidth(80);
        
        // Status column
        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
        statusColumn.setPrefWidth(100);
        
        // Action column
        TableColumn<Order, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<Order, Void>() {
            private final HBox actionButtons = new HBox(5);
            private final Button detailsButton = new Button("Details");
            private final Button updateButton = new Button("Update Status");
            
            {
                detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                
                detailsButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });
                
                updateButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    updateOrderStatus(order);
                });
                
                actionButtons.getChildren().addAll(detailsButton, updateButton);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
        actionColumn.setPrefWidth(160);
        
        orderTable.getColumns().addAll(idColumn, userColumn, emailColumn, dateColumn, totalColumn, statusColumn, actionColumn);
    }
    
    private void loadOrders(OrderStatus status) {
        try {
            List<Order> orders;
            if (status != null) {
                orders = orderService.getOrdersByStatus(status);
            } else {
                // Get all orders
                orders = orderService.getAllOrders();
            }
            orderTable.setItems(FXCollections.observableArrayList(orders));
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
        
        // Customer info
        Label customerLabel = new Label("Customer: " + order.getUser().getFirstName() + " " + order.getUser().getLastName());
        Label emailLabel = new Label("Email: " + order.getUser().getEmail());
        
        Label statusLabel = new Label("Status: " + order.getStatus());
        Label addressLabel = new Label("Shipping Address: " + (order.getShippingAddress() != null ? order.getShippingAddress() : "N/A"));
        Label paymentLabel = new Label("Payment Method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"));
        Label paymentStatusLabel = new Label("Payment Status: " + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "PENDING"));
        
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
        
        // Update status button
        Button updateStatusBtn = new Button("Update Order Status");
        updateStatusBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateStatusBtn.setOnAction(e -> {
            dialog.close();
            updateOrderStatus(order);
        });
        
        // Add all to content
        content.getChildren().addAll(
            customerLabel, emailLabel, dateLabel, statusLabel, 
            addressLabel, paymentLabel, paymentStatusLabel, totalLabel,
            new Separator(),
            new Label("Order Items:"),
            itemsTable,
            updateStatusBtn
        );
        
        // Add close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.showAndWait();
    }
    
    private void updateOrderStatus(Order order) {
        Dialog<OrderStatus> dialog = new Dialog<>();
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Update Status for Order #" + order.getId());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Current status
        Label currentStatusLabel = new Label("Current Status:");
        Label currentStatus = new Label(order.getStatus().toString());
        currentStatus.setStyle("-fx-font-weight: bold;");
        
        // New status dropdown
        Label newStatusLabel = new Label("New Status:");
        ComboBox<OrderStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(OrderStatus.values());
        statusComboBox.setValue(order.getStatus());
        
        grid.add(currentStatusLabel, 0, 0);
        grid.add(currentStatus, 1, 0);
        grid.add(newStatusLabel, 0, 1);
        grid.add(statusComboBox, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, cancelButton);
        
        // Set the result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButton) {
                return statusComboBox.getValue();
            }
            return null;
        });
        
        // Process the result
        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                if (newStatus != order.getStatus()) {
                    orderService.updateOrderStatus(order.getId(), newStatus);
                    showAlert("Success", "Order status updated to " + newStatus);
                    
                    // Refresh the order list
                    String selectedStatus = statusFilter.getValue();
                    if ("ALL".equals(selectedStatus)) {
                        loadOrders(null);
                    } else {
                        loadOrders(OrderStatus.valueOf(selectedStatus));
                    }
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to update order status: " + e.getMessage());
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