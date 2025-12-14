# Secure Customer API with JWT Authentication

## Student Information
- **Name:** Nguyễn Minh Quân
- **Student ID:** ITITIU23035
- **Class:** Web Application Development G01_lab3 (Tue-1234)
## Features Implemented

### Authentication
- ✅ User registration
- ✅ User login with JWT
- ✅ Logout
- ✅ Get current user
- ✅ Password hashing with BCrypt

### Authorization
- ✅ Role-based access control (USER, ADMIN)
- ✅ Protected endpoints
- ✅ Method-level security with @PreAuthorize

### Additional Features
- ✅ Change password
- ✅ Forgot password / Reset password
- ✅ User profile management
- ✅ Admin user management
- [X ] Refresh token

## API Endpoints

### Public Endpoints
- POST /api/auth/register
- POST /api/auth/login

### Protected Endpoints (Authenticated)
- GET /api/auth/me
- POST /api/auth/logout
- GET /api/customers
- GET /api/customers/{id}

### Admin Only Endpoints
- POST /api/customers
- PUT /api/customers/{id}
- DELETE /api/customers/{id}
- GET /api/admin/users
- PUT /api/admin/users/{id}/role

## Test Users
| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| john | password123 | USER |
| jane | password123 | USER |

## How to Run
1. Create database: `customer_management`
2. Run SQL scripts to create tables
3. Update `application.properties` with your MySQL credentials
4. Run: `mvn spring-boot:run`
5. Test with Thunder Client using provided collection

## Testing
Import Postman collection: `Secure_Customer_API.postman_collection.json`

All endpoints tested and working.

## Security
- Passwords hashed with BCrypt
- JWT tokens with 24-hour expiration
- Stateless authentication
- CORS enabled for frontend
- Protected endpoints with Spring Security


## Time Spent
Approximately [X] hours
