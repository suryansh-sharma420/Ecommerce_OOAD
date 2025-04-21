package com.example.demo.frontend;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoginScreen {
    private final UserService userService;
    private final ApplicationContext applicationContext;

    @Autowired
    public LoginScreen(UserService userService, ApplicationContext applicationContext) {
        this.userService = userService;
        this.applicationContext = applicationContext;
    }

    public void show(Stage stage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 1);

        TextField emailField = new TextField();
        grid.add(emailField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 4);

        Label messageLabel = new Label();
        grid.add(messageLabel, 1, 6);

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            try {
                UserDTO loginRequest = new UserDTO();
                loginRequest.setEmail(email);
                loginRequest.setPassword(password);

                User user = userService.login(loginRequest);

                SessionManager.getInstance().setCurrentUser(user);
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");
                
                if (user.getRole() == UserRole.ADMIN) {
                    // Try to get the dashboard from Spring context
                    AdminDashboard dashboard;
                    try {
                        dashboard = applicationContext.getBean(AdminDashboard.class);
                        dashboard.show(stage);
                    } catch (Exception ex) {
                        // Fall back to direct instantiation
                        new AdminDashboard(stage).show();
                    }
                } else {
                    // Try to get the dashboard from Spring context
                    CustomerDashboard dashboard;
                    try {
                        dashboard = applicationContext.getBean(CustomerDashboard.class);
                        dashboard.show(stage);
                    } catch (Exception ex) {
                        // Fall back to direct instantiation
                        new CustomerDashboard(stage).show();
                    }
                }
            } catch (RuntimeException ex) {
                messageLabel.setText("Login error: " + ex.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
