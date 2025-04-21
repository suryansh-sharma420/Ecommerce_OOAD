package com.example.demo.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class CartController {
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> productColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> priceColumn;
    @FXML private TableColumn<CartItem, Double> totalColumn;
    @FXML private TableColumn<CartItem, Void> actionColumn;
    @FXML private Label totalAmountLabel;
    @FXML private Button continueShoppingButton;
    @FXML private Button checkoutButton;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private Parent mainView;

    @FXML
    public void initialize() {
        setupTableColumns();
        updateTotalAmount();
        cartTable.setItems(cartItems);
    }

    private void setupTableColumns() {
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

        // Add remove button to action column
        actionColumn.setCellFactory(column -> new TableCell<CartItem, Void>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeFromCart(item);
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
    }

    public void addToCart(Product product) {
        // Check if product already exists in cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                updateTotalAmount();
                return;
            }
        }
        
        // If product is not in cart, add it
        cartItems.add(new CartItem(product, 1));
        updateTotalAmount();
    }

    private void removeFromCart(CartItem item) {
        cartItems.remove(item);
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
        totalAmountLabel.setText(currencyFormat.format(total));
    }

    public void setMainView(Parent mainView) {
        this.mainView = mainView;
    }

    @FXML
    private void onContinueShopping() {
        if (mainView != null) {
            Scene scene = continueShoppingButton.getScene();
            scene.setRoot(mainView);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
                Parent root = loader.load();
                Scene scene = continueShoppingButton.getScene();
                scene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading main view");
            }
        }
    }

    @FXML
    private void onCheckout() {
        if (cartItems.isEmpty()) {
            showError("Cart is empty");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CheckoutView.fxml"));
            Parent checkoutRoot = loader.load();
            
            CheckoutController checkoutController = loader.getController();
            checkoutController.setCartItems(cartItems);
            checkoutController.setCartView(checkoutButton.getScene().getRoot());
            
            Scene scene = checkoutButton.getScene();
            scene.setRoot(checkoutRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading checkout view");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 