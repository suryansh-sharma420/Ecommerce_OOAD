package com.example.demo;

import com.example.demo.frontend.LoginScreen;
import com.example.demo.frontend.SessionManager;
import com.example.demo.service.ProductService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EcommerceApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = SpringApplication.run(EcommerceApplication.class);
    }

    @Override
    public void start(Stage primaryStage) {
        // Store service references in SessionManager for easier access
        ProductService productService = springContext.getBean(ProductService.class);
        OrderService orderService = springContext.getBean(OrderService.class);
        UserService userService = springContext.getBean(UserService.class);
        
        SessionManager.getInstance().setProductService(productService);
        SessionManager.getInstance().setOrderService(orderService);
        SessionManager.getInstance().setUserService(userService);
        
        // Initialize login screen
        LoginScreen loginScreen = springContext.getBean(LoginScreen.class);
        loginScreen.show(primaryStage);
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
