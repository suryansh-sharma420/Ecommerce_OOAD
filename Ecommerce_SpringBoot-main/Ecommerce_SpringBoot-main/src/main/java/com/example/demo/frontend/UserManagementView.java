package com.example.demo.frontend;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManagementView {
    private final UserService userService;
    private Stage stage;
    private TableView<User> userTable;
    
    @Autowired
    public UserManagementView(UserService userService) {
        this.userService = userService;
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create user table
        createUserTable();
        
        // Load users
        loadUsers();
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new AdminDashboard(stage).show());
        
        // Add components to root
        root.getChildren().addAll(titleLabel, userTable, backButton);
        
        // Set scene
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("User Management");
        stage.setScene(scene);
        stage.show();
    }
    
    private void createUserTable() {
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // ID column
        TableColumn<User, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId().toString()));
        
        // Email column
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        emailColumn.setPrefWidth(200);
        
        // First Name column
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        
        // Last Name column
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        
        // Role column
        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));
        
        // Status column
        TableColumn<User, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isEnabled() ? "Active" : "Disabled"));
        
        // Action column
        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final HBox actionButtons = new HBox(5);
            private final Button enableDisableBtn = new Button();
            private final Button viewDetailsBtn = new Button("Details");
            
            {
                viewDetailsBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                
                viewDetailsBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDetails(user);
                });
                
                enableDisableBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    toggleUserStatus(user);
                });
                
                actionButtons.getChildren().addAll(viewDetailsBtn, enableDisableBtn);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user.isEnabled()) {
                        enableDisableBtn.setText("Disable");
                        enableDisableBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    } else {
                        enableDisableBtn.setText("Enable");
                        enableDisableBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    }
                    setGraphic(actionButtons);
                }
            }
        });
        
        userTable.getColumns().addAll(idColumn, emailColumn, firstNameColumn, lastNameColumn, 
                                    roleColumn, statusColumn, actionColumn);
    }
    
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }
    
    private void toggleUserStatus(User user) {
        try {
            if (user.isEnabled()) {
                userService.disableUser(user.getId());
                showAlert("Success", "User disabled successfully.");
            } else {
                userService.enableUser(user.getId());
                showAlert("Success", "User enabled successfully.");
            }
            loadUsers(); // Reload to update the table
        } catch (Exception e) {
            showAlert("Error", "Failed to update user status: " + e.getMessage());
        }
    }
    
    private void showUserDetails(User user) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("User Details");
        dialog.setHeaderText("User: " + user.getFirstName() + " " + user.getLastName());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // User info
        Label idLabel = new Label("ID: " + user.getId());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label nameLabel = new Label("Name: " + user.getFirstName() + " " + user.getLastName());
        Label roleLabel = new Label("Role: " + user.getRole());
        Label statusLabel = new Label("Status: " + (user.isEnabled() ? "Active" : "Disabled"));
        
        content.getChildren().addAll(idLabel, emailLabel, nameLabel, roleLabel, statusLabel);
        
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