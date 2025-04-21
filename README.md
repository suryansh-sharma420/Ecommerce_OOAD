# Spring Boot E-Commerce Application

A full-featured e-commerce platform built with Spring Boot and JavaFX, featuring both REST API backend and desktop client frontend.

## Technology Stack

- **Backend**: Spring Boot 2.x, Spring Data JPA, Spring Security
- **Frontend**: JavaFX
- **Database**: H2/MySQL (configurable)
- **Build Tool**: Maven

## Features

### Customer Features
- User registration and authentication
- Browse product catalog
- Search and filter products
- Shopping cart management
- Checkout process
- Order history

### Admin Features
- Admin dashboard with sales statistics
- User management
- Product inventory management
- Order management
- Sales reporting
- Low stock alerts

## Project Structure

```
src/main/java/com/example/demo/
├── controller/           # REST API endpoints
│   ├── AdminController   # Admin-specific endpoints
│   ├── ProductController # Product management
│   ├── OrderController   # Order processing
│   └── UserController    # Authentication & user management
├── dto/                  # Data Transfer Objects
├── model/                # JPA Entity classes
│   ├── Product
│   ├── User
│   ├── Order
│   └── UserRole (enum)
├── repository/           # Spring Data JPA repositories
├── service/              # Business logic
│   ├── AdminService      # Admin-specific operations
│   ├── ProductService    # Product management
│   ├── OrderService      # Order processing
│   └── UserService       # User management & auth
├── frontend/             # JavaFX UI components
│   ├── AdminDashboard    # Admin UI
│   ├── CustomerDashboard # Customer UI
│   └── various views     # Other UI screens
├── config/               # Application configuration
├── security/             # Security configuration
└── EcommerceApplication  # Main application class
```

## Setup Instructions

### Prerequisites
- JDK 11+
- Maven 3.6+

### Database Configuration
Edit `src/main/resources/application.properties` to configure your database connection.

### Building the Application
```bash
mvn clean install
```

### Running the Application
```bash
# Using Maven
mvn spring-boot:run

# Or using the included batch file (Windows)
run.bat
```

## API Endpoints

### Authentication
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login and get JWT token

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Orders
- `GET /api/orders` - Get authenticated user's orders
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order details by ID

### Admin Dashboard
- `GET /api/admin/dashboard` - Get dashboard statistics
- `GET /api/admin/users` - Get all users
- `GET /api/admin/low-stock` - Get low stock products
- `GET /api/admin/recent-orders` - Get recent orders
- `GET /api/admin/sales-report` - Generate sales report

## Security

The application uses JWT (JSON Web Tokens) for authentication. Protected endpoints require a valid JWT token in the Authorization header.

## License

[MIT License](LICENSE)
