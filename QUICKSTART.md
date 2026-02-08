# Locality Connect - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (or use H2 for quick testing)

### Step 1: Setup Database

**Option A: PostgreSQL (Recommended for Production)**
```bash
# Create database
psql -U postgres
CREATE DATABASE locality_connect;
\q

# Update application.properties with your credentials
```

**Option B: H2 (Quick Testing)**
```bash
# Update src/main/resources/application.properties.bak:
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

### Step 2: Run the Application
```bash
cd locality-connect
mvn clean install
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

### Step 3: Test the API

#### 1. Create a Locality
```bash
curl -X POST http://localhost:8080/api/localities \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Green Valley",
    "votingThresholdPercentage": 50,
    "votingPeriodDays": 30,
    "city": "Bangalore",
    "state": "Karnataka",
    "pincode": "560001"
  }'
```

Note: First create an admin user or modify security temporarily

#### 2. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "1234567890",
    "localityId": 1
  }'
```

Save the token from response!

#### 3. Create a Suggestion
```bash
curl -X POST http://localhost:8080/api/suggestions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "title": "Install Speed Breakers",
    "description": "Need speed breakers near playground",
    "category": "SUGGESTION",
    "userPriority": 1
  }'
```

#### 4. Vote on a Suggestion
```bash
curl -X POST http://localhost:8080/api/votes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "suggestionId": 1,
    "voteType": "LIKE"
  }'
```

#### 5. View Dashboard
```bash
curl -X GET http://localhost:8080/api/suggestions/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 📚 Full API Documentation

See `README.md` for complete API documentation.

## 🔧 Common Issues

### Issue: "Port 8080 already in use"
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: "Unable to connect to database"
**Solution**: Verify PostgreSQL is running:
```bash
sudo service postgresql status
```

### Issue: "JWT secret too short"
**Solution**: The default secret is already configured. For production, generate a new one:
```bash
openssl rand -hex 64
```

## 📦 Project Structure
```
locality-connect/
├── src/main/java/com/localityconnect/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Database access
│   ├── entity/         # JPA entities
│   ├── dto/            # Data transfer objects
│   ├── security/       # JWT & authentication
│   ├── config/         # Spring configuration
│   ├── exception/      # Error handling
│   └── scheduler/      # Scheduled jobs
├── pom.xml             # Maven dependencies
└── README.md           # Full documentation
```

## 🎯 Key Features Implemented

✅ User registration and JWT authentication  
✅ Locality management with configurable settings  
✅ Create max 5 suggestions with priority  
✅ Like/dislike voting system  
✅ Automatic status transitions (NEW → IN_DISCUSSION)  
✅ Dashboard for NEW suggestions  
✅ Discussion forum for IN_DISCUSSION items  
✅ Scheduled job for expired voting periods  
✅ Role-based access control (ADMIN/RESIDENT)  

## 🧪 Testing with Postman

1. Import the endpoints into Postman
2. Create an environment with:
   - `base_url`: http://localhost:8080/api
   - `token`: (set after login)
3. Use `{{base_url}}` and `{{token}}` in requests

## 📞 Need Help?

Check the main README.md for:
- Detailed API documentation
- Business logic explanation
- Database schema
- Troubleshooting guide

Happy coding! 🚀
