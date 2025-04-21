package com.example.demo.frontend;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileView {
    private final UserService userService;
    private Stage stage;
    
    @Autowired
    public ProfileView(UserService userService) {
        this.userService = userService;
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        
        // Get the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "You must be logged in to view your profile.");
            return;
        }
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Profile form
        GridPane form = createProfileForm(currentUser);
        
        // Buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button backButton = new Button("Back to Dashboard");
        
        buttonBox.getChildren().addAll(backButton, saveButton);
        
        // Add components to root
        root.getChildren().addAll(titleLabel, form, buttonBox);
        
        // Set up button actions
        saveButton.setOnAction(e -> saveProfile(form, currentUser));
        backButton.setOnAction(e -> new CustomerDashboard(stage).show());
        
        // Set scene
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("My Profile");
        stage.setScene(scene);
        stage.show();
    }
    
    private GridPane createProfileForm(User user) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        
        // Email
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(user.getEmail());
        emailField.setEditable(false); // Email cannot be changed
        emailField.setStyle("-fx-background-color: #f0f0f0;");
        
        // First Name
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField(user.getFirstName());
        firstNameField.setId("firstNameField");
        
        // Last Name
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField(user.getLastName());
        lastNameField.setId("lastNameField");
        
        // Current Password
        Label currentPasswordLabel = new Label("Current Password:");
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setId("currentPasswordField");
        currentPasswordField.setPromptText("Enter to verify identity");
        
        // New Password
        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setId("newPasswordField");
        newPasswordField.setPromptText("Leave blank to keep current password");
        
        // Confirm New Password
        Label confirmPasswordLabel = new Label("Confirm New Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setId("confirmPasswordField");
        
        // Add all to form
        form.add(emailLabel, 0, 0);
        form.add(emailField, 1, 0);
        
        form.add(firstNameLabel, 0, 1);
        form.add(firstNameField, 1, 1);
        
        form.add(lastNameLabel, 0, 2);
        form.add(lastNameField, 1, 2);
        
        form.add(currentPasswordLabel, 0, 3);
        form.add(currentPasswordField, 1, 3);
        
        form.add(newPasswordLabel, 0, 4);
        form.add(newPasswordField, 1, 4);
        
        form.add(confirmPasswordLabel, 0, 5);
        form.add(confirmPasswordField, 1, 5);
        
        return form;
    }
    
    private void saveProfile(GridPane form, User currentUser) {
        try {
            TextField firstNameField = (TextField) form.lookup("#firstNameField");
            TextField lastNameField = (TextField) form.lookup("#lastNameField");
            PasswordField currentPasswordField = (PasswordField) form.lookup("#currentPasswordField");
            PasswordField newPasswordField = (PasswordField) form.lookup("#newPasswordField");
            PasswordField confirmPasswordField = (PasswordField) form.lookup("#confirmPasswordField");
            
            // Basic validation
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                showAlert("Error", "First name and last name cannot be empty.");
                return;
            }
            
            if (currentPasswordField.getText().isEmpty()) {
                showAlert("Error", "Please enter your current password to make changes.");
                return;
            }
            
            // Verify current password
            if (!currentPasswordField.getText().equals(currentUser.getPassword())) {
                showAlert("Error", "Current password is incorrect.");
                return;
            }
            
            // Check if new password is being changed
            String newPassword = currentUser.getPassword(); // Default to current
            if (!newPasswordField.getText().isEmpty()) {
                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    showAlert("Error", "New password and confirmation do not match.");
                    return;
                }
                newPassword = newPasswordField.getText();
            }
            
            // Update user information
            currentUser.setFirstName(firstNameField.getText().trim());
            currentUser.setLastName(lastNameField.getText().trim());
            currentUser.setPassword(newPassword);
            
            // Save the changes
            User updatedUser = userService.updateUser(currentUser);
            
            // Update the session
            SessionManager.getInstance().setCurrentUser(updatedUser);
            
            showAlert("Success", "Profile updated successfully.");
            
            // Return to dashboard
            new CustomerDashboard(stage).show();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to update profile: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 