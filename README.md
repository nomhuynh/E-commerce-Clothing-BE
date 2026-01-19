# E-commerce Clothing Backend API

A robust RESTful API backend service for an E-commerce Clothing website built with Spring Boot and MySQL.

## 📋 Description

This project provides a comprehensive backend solution for an online clothing store, handling user authentication, product management, shopping cart operations, order processing, and payment integration.

## ✨ Features

- **User Management**
  - User registration and authentication
  - JWT-based authorization
  - User profile management
  - Role-based access control (Admin, Customer)

- **Product Management**
  - CRUD operations for products
  - Category and subcategory management
  - Product search and filtering
  - Product images and variants (size, color)
  - Inventory management

- **Shopping Cart**
  - Add/remove items to cart
  - Update cart quantities
  - Cart persistence for logged-in users

- **Order Management**
  - Order creation and tracking
  - Order history
  - Order status updates
  - Invoice generation

- **Payment Integration**
  - Multiple payment gateway support
  - Payment processing and verification
  - Transaction history

- **Admin Dashboard**
  - User management
  - Product and inventory management
  - Order management
  - Sales analytics

## 🛠️ Technology Stack

- **Backend Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Database:** MySQL 8.0+
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JWT
- **Build Tool:** Maven / Gradle
- **API Documentation:** Swagger / OpenAPI
- **Validation:** Hibernate Validator
- **Testing:** JUnit 5, Mockito, Spring Boot Test

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java JDK 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **MySQL 8.0 or higher** - [Download](https://dev.mysql.com/downloads/mysql/)
- **Maven 3.6+** (if using Maven) - [Download](https://maven.apache.org/download.cgi)
- **Gradle 7.0+** (if using Gradle) - [Download](https://gradle.org/install/)
- **Git** - [Download](https://git-scm.com/downloads/)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/nomhuynh/E-commerce-Clothing-BE.git
cd E-commerce-Clothing-BE
```

### 2. Configure MySQL Database

Create a new MySQL database:

```sql
CREATE DATABASE ecommerce_clothing;
CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerce_clothing.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_clothing
spring.datasource.username=ecommerce_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# JWT Configuration
jwt.secret=your_jwt_secret_key_here
jwt.expiration=86400000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Note:** For production, use environment variables or a separate configuration file for sensitive data.

### 4. Build the Project

**Using Maven:**
```bash
mvn clean install
```

**Using Gradle:**
```bash
gradle build
```

### 5. Run the Application

**Using Maven:**
```bash
mvn spring-boot:run
```

**Using Gradle:**
```bash
gradle bootRun
```

**Or run the JAR file:**
```bash
java -jar target/ecommerce-clothing-backend-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

Once the application is running, access the API documentation at:

- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api/v3/api-docs`

### Main API Endpoints

#### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

#### Users
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users` - Get all users (Admin only)

#### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product (Admin only)
- `PUT /api/products/{id}` - Update product (Admin only)
- `DELETE /api/products/{id}` - Delete product (Admin only)

#### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category (Admin only)

#### Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update cart item
- `DELETE /api/cart/items/{id}` - Remove item from cart

#### Orders
- `GET /api/orders` - Get user's orders
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status (Admin only)

## 📁 Project Structure

```
E-commerce-Clothing-BE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ecommerce/clothing/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── entity/          # JPA entities
│   │   │       ├── exception/       # Custom exceptions
│   │   │       ├── repository/      # JPA repositories
│   │   │       ├── security/        # Security configurations
│   │   │       ├── service/         # Business logic
│   │   │       └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── static/              # Static resources
│   └── test/
│       └── java/                    # Test classes
├── .gitignore
├── pom.xml / build.gradle           # Build configuration
└── README.md
```

## 🧪 Testing

Run tests using:

**Maven:**
```bash
mvn test
```

**Gradle:**
```bash
gradle test
```

## 🔒 Security

- All passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- CORS is configured for frontend integration
- Input validation is implemented on all endpoints
- SQL injection protection via JPA
- XSS protection enabled

## 🌍 Environment Variables

For production deployment, set the following environment variables:

```bash
DB_URL=jdbc:mysql://your-db-host:3306/ecommerce_clothing
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

## 🐳 Docker Support

Build and run using Docker:

```bash
docker build -t ecommerce-clothing-backend .
docker run -p 8080:8080 ecommerce-clothing-backend
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Huynh Quoc Nam** - [@nomhuynh](https://github.com/nomhuynh)

## 📧 Contact

For any questions or suggestions, please open an issue or contact the repository owner.

## 🙏 Acknowledgments

- Spring Boot documentation
- MySQL documentation
- All contributors and supporters of this project