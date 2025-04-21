package com.example.demo.frontend;

import com.example.demo.frontend.service.AuthService;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        try {
            User user = authService.login(email, password);
            if (user != null) {
                // Store the logged-in user
                SessionManager.getInstance().setCurrentUser(user);
                
                // Load appropriate view based on user role
                loadMainView();
            } else {
                errorLabel.setText("Invalid email or password");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
            Parent registerView = loader.load();
            Scene scene = loginButton.getScene();
            scene.setRoot(registerView);
        } catch (IOException e) {
            errorLabel.setText("Error loading registration view");
            e.printStackTrace();
        }
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent mainView = loader.load();
            Scene scene = loginButton.getScene();
            scene.setRoot(mainView);
        } catch (IOException e) {
            errorLabel.setText("Error loading main view");
            e.printStackTrace();
        }
    }
} 