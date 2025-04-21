package com.example.demo.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CheckoutController {
    @FXML private TableView<CartItem> orderSummaryTable;
    @FXML private TableColumn<CartItem, String> productColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> priceColumn;
    @FXML private TableColumn<CartItem, Double> totalColumn;
    @FXML private Label totalAmountLabel;
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    
    @FXML private TextField cardNumberField;
    @FXML private ComboBox<String> expiryMonthCombo;
    @FXML private ComboBox<String> expiryYearCombo;
    @FXML private TextField cvvField;
    
    @FXML private Button backToCartButton;
    @FXML private Button placeOrderButton;
    
    private Parent cartView;
    private ObservableList<CartItem> orderItems;
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @FXML
    public void initialize() {
        setupOrderSummaryTable();
        setupExpiryDateCombos();
        setupInputValidation();
    }
    
    private void setupOrderSummaryTable() {
        productColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduct().getName()));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Format price and total columns as currency
        priceColumn.setCellFactory(column -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        totalColumn.setCellFactory(column -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(total));
                }
            }
        });
    }
    
    private void setupExpiryDateCombos() {
        // Set up month combo box (01-12)
        List<String> months = IntStream.rangeClosed(1, 12)
                .mapToObj(m -> String.format("%02d", m))
                .collect(Collectors.toList());
        expiryMonthCombo.setItems(FXCollections.observableArrayList(months));
        
        // Set up year combo box (current year + 10 years)
        int currentYear = Year.now().getValue();
        List<String> years = IntStream.rangeClosed(currentYear, currentYear + 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        expiryYearCombo.setItems(FXCollections.observableArrayList(years));
    }
    
    private void setupInputValidation() {
        // Card number validation (numbers and dashes only)
        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,4}-?\\d{0,4}-?\\d{0,4}-?\\d{0,4}")) {
                cardNumberField.setText(oldValue);
            }
        });
        
        // CVV validation (3-4 digits only)
        cvvField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,4}")) {
                cvvField.setText(oldValue);
            }
        });
        
        // Phone number validation (numbers and dashes only)
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,3}-?\\d{0,3}-?\\d{0,4}")) {
                phoneField.setText(oldValue);
            }
        });
    }
    
    public void setCartItems(ObservableList<CartItem> items) {
        this.orderItems = items;
        orderSummaryTable.setItems(items);
        updateTotalAmount();
    }
    
    private void updateTotalAmount() {
        if (orderItems != null) {
            double total = orderItems.stream()
                    .mapToDouble(CartItem::getTotal)
                    .sum();
            totalAmountLabel.setText(currencyFormat.format(total));
        }
    }
    
    public void setCartView(Parent cartView) {
        this.cartView = cartView;
    }
    
    @FXML
    private void onBackToCart() {
        if (cartView != null) {
            Scene scene = backToCartButton.getScene();
            scene.setRoot(cartView);
        }
    }
    
    @FXML
    private void onPlaceOrder() {
        if (!validateForm()) {
            return;
        }
        
        showInfo("Success", "Order placed successfully!\nThank you for your purchase!");
        
        // Clear the cart and return to main view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent mainView = loader.load();
            Scene scene = placeOrderButton.getScene();
            scene.setRoot(mainView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to return to main view");
        }
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errors.append("- Please enter your full name\n");
        }
        
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("- Please enter a valid email address\n");
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("- Please enter your phone number\n");
        }
        
        if (addressField.getText().trim().isEmpty()) {
            errors.append("- Please enter your address\n");
        }
        
        if (!cardNumberField.getText().matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
            errors.append("- Please enter a valid card number (XXXX-XXXX-XXXX-XXXX)\n");
        }
        
        if (expiryMonthCombo.getValue() == null || expiryYearCombo.getValue() == null) {
            errors.append("- Please select card expiry date\n");
        }
        
        if (!cvvField.getText().matches("\\d{3,4}")) {
            errors.append("- Please enter a valid CVV (3-4 digits)\n");
        }
        
        if (errors.length() > 0) {
            showError("Validation Error", "Please fix the following errors:\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 