package com.example.demo.frontend;

import com.example.demo.frontend.service.AuthService;
import com.example.demo.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;
    @FXML private Label errorLabel;

    private final AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (!isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters long");
            return;
        }

        // Create UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        try {
            authService.register(userDTO);
            // Show success message and return to login
            showSuccessAndReturnToLogin();
        } catch (Exception e) {
            errorLabel.setText("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        loadLoginView();
    }

    private void showSuccessAndReturnToLogin() {
        // You could show a success alert here if desired
        loadLoginView();
    }

    private void loadLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent loginView = loader.load();
            Scene scene = registerButton.getScene();
            scene.setRoot(loginView);
        } catch (IOException e) {
            errorLabel.setText("Error returning to login view");
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
} 