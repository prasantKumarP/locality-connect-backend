# Locality Connect - Community Feedback Platform

A Spring Boot REST API backend for a community platform where residents can submit suggestions/complaints for their locality and collectively prioritize them through voting.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Business Logic](#business-logic)
- [Testing the API](#testing-the-api)

## Features

### Core Functionality
- **Locality Management**: Admin can register localities with configurable voting settings
- **User Registration**: Users register and associate with a locality
- **Suggestion/Complaint System**: 
  - Users can create max 5 suggestions/complaints with NEW status
  - Each has user-defined priority (1-5)
  - Categories: SUGGESTION or COMPLAINT
- **Dashboard**: Display all NEW suggestions from the locality
- **Voting System**: 
  - Users can like/dislike suggestions
  - One vote per user per suggestion
  - Can change or remove vote
- **Automatic Status Transitions**:
  - NEW → IN_DISCUSSION (if >50% likes within voting period)
  - NEW → VALID/INVALID/LATER (after voting period based on votes)
- **Discussion Forum**: Shows IN_DISCUSSION items with calculated priority (1-5 based on likes)
- **Scheduled Jobs**: Hourly check for expired voting periods

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL** (or H2 for testing)
- **Lombok**
- **Maven**

## Project Structure

```
locality-connect/
├── src/main/java/com/localityconnect/
│   ├── LocalityConnectApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── LocalityController.java
│   │   ├── SuggestionController.java
│   │   └── VoteController.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   ├── JwtResponse.java
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── LocalityRequest.java
│   │   ├── LocalityResponse.java
│   │   ├── SuggestionRequest.java
│   │   ├── SuggestionResponse.java
│   │   └── VoteRequest.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── Locality.java
│   │   ├── Suggestion.java
│   │   ├── SuggestionCategory.java
│   │   ├── SuggestionStatus.java
│   │   ├── Vote.java
│   │   └── VoteType.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── LocalityRepository.java
│   │   ├── SuggestionRepository.java
│   │   └── VoteRepository.java
│   ├── scheduler/
│   │   └── SuggestionStatusScheduler.java
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   └── UserPrincipal.java
│   └── service/
│       ├── AuthService.java
│       ├── LocalityService.java
│       ├── SuggestionService.java
│       └── VoteService.java
└── src/main/resources/
    └── application.properties
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use H2 for development)

### 1. Clone the Repository
```bash
git clone <repository-url>
cd locality-connect
```

### 2. Configure Database

**Option A: PostgreSQL (Production)**

Create a database:
```sql
CREATE DATABASE locality_connect;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/locality_connect
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**Option B: H2 (Development)**

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Database Schema

### Tables

**users**
- id (PK)
- username (unique)
- email (unique)
- password (encrypted)
- full_name
- phone_number
- locality_id (FK)
- role (ADMIN/RESIDENT)
- active
- created_at
- updated_at

**localities**
- id (PK)
- name (unique)
- description
- address, city, state, pincode
- voting_threshold_percentage (default: 50)
- voting_period_days (default: 30)
- active
- created_at
- updated_at

**suggestions**
- id (PK)
- title
- description
- category (SUGGESTION/COMPLAINT)
- status (NEW/VALID/INVALID/LATER/IN_DISCUSSION)
- user_priority (1-5)
- calculated_priority (1-5, for IN_DISCUSSION)
- user_id (FK)
- locality_id (FK)
- like_count
- dislike_count
- created_at
- updated_at
- discussion_started_at

**votes**
- id (PK)
- user_id (FK)
- suggestion_id (FK)
- vote_type (LIKE/DISLIKE)
- created_at
- UNIQUE(user_id, suggestion_id)

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### 1. Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "1234567890",
  "localityId": 1
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "RESIDENT",
  "localityId": 1
}
```

#### 2. Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "RESIDENT",
  "localityId": 1
}
```

### Locality Endpoints

#### 3. Create Locality (ADMIN only)
```http
POST /localities
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Green Valley Apartments",
  "description": "Residential community in downtown",
  "address": "123 Main Street",
  "city": "Bangalore",
  "state": "Karnataka",
  "pincode": "560001",
  "votingThresholdPercentage": 50,
  "votingPeriodDays": 30
}

Response: 200 OK
{
  "id": 1,
  "name": "Green Valley Apartments",
  "description": "Residential community in downtown",
  ...
}
```

#### 4. Get All Localities
```http
GET /localities/all

Response: 200 OK
[
  {
    "id": 1,
    "name": "Green Valley Apartments",
    ...
  }
]
```

#### 5. Get Locality by ID
```http
GET /localities/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "name": "Green Valley Apartments",
  ...
}
```

#### 6. Update Locality (ADMIN only)
```http
PUT /localities/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Green Valley Apartments",
  "votingThresholdPercentage": 60,
  "votingPeriodDays": 45
}
```

### Suggestion Endpoints

#### 7. Create Suggestion
```http
POST /suggestions
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Install Speed Breakers",
  "description": "Need speed breakers near the playground for child safety",
  "category": "SUGGESTION",
  "userPriority": 1
}

Response: 200 OK
{
  "id": 1,
  "title": "Install Speed Breakers",
  "status": "NEW",
  "userPriority": 1,
  "likeCount": 0,
  "dislikeCount": 0,
  ...
}
```

#### 8. Update Suggestion
```http
PUT /suggestions/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Install Speed Breakers Near Playground",
  "description": "Updated description",
  "category": "SUGGESTION",
  "userPriority": 2
}
```

#### 9. Delete Suggestion
```http
DELETE /suggestions/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "success": true,
  "message": "Suggestion deleted successfully"
}
```

#### 10. Get My Suggestions (ordered by user priority)
```http
GET /suggestions/my
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "title": "Install Speed Breakers",
    "userPriority": 1,
    ...
  },
  {
    "id": 2,
    "title": "Fix Street Light",
    "userPriority": 2,
    ...
  }
]
```

#### 11. Get Dashboard (all NEW suggestions in locality)
```http
GET /suggestions/dashboard
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "title": "Install Speed Breakers",
    "status": "NEW",
    "likeCount": 15,
    "dislikeCount": 3,
    ...
  }
]
```

#### 12. Get Discussion Forum (all IN_DISCUSSION suggestions)
```http
GET /suggestions/discussion
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 5,
    "title": "Community Garden",
    "status": "IN_DISCUSSION",
    "calculatedPriority": 1,
    "likeCount": 120,
    ...
  }
]
```

### Vote Endpoints

#### 13. Cast/Change/Remove Vote
```http
POST /votes
Authorization: Bearer {token}
Content-Type: application/json

{
  "suggestionId": 1,
  "voteType": "LIKE"
}

Response: 200 OK
{
  "success": true,
  "message": "Vote cast successfully"
}

// To change vote, send opposite voteType
// To remove vote, send same voteType again (toggle)
```

## Business Logic

### Suggestion Status Flow

```
NEW (created)
  ↓
  → If likes >= 50% within voting period → IN_DISCUSSION
  → If voting period expired:
      - likes > dislikes → VALID
      - dislikes > likes → INVALID
      - equal → LATER
```

### Constraints

1. **User Limitations**:
   - Max 5 suggestions with NEW status at any time
   - Each suggestion has user-defined priority (1-5)
   - Can only vote once per suggestion (can change or remove)

2. **Locality Configuration**:
   - Voting threshold percentage (default 50%)
   - Voting period in days (default 30 days)

3. **Priority Calculation** (for IN_DISCUSSION):
   - 100+ likes → Priority 1 (highest)
   - 50-99 likes → Priority 2
   - 25-49 likes → Priority 3
   - 10-24 likes → Priority 4
   - <10 likes → Priority 5 (lowest)

### Scheduled Jobs

**SuggestionStatusScheduler** runs every hour:
- Checks all NEW suggestions
- If voting period expired, updates status based on votes
- Moves to VALID/INVALID/LATER based on like/dislike ratio

## Testing the API

### Step-by-Step Testing Flow

1. **Create a Locality (as ADMIN)**:
   - First, manually create an admin user in the database or modify the registration to allow admin creation
   - Create a locality using POST /localities

2. **Register Users**:
   - Register multiple users with the locality ID
   - Save the JWT tokens

3. **Create Suggestions**:
   - Each user creates suggestions (max 5 with NEW status)
   - Set different priorities (1-5)

4. **Vote on Suggestions**:
   - Users vote on each other's suggestions
   - Test like, dislike, change vote, remove vote

5. **Check Dashboard**:
   - View all NEW suggestions
   - Verify like/dislike counts

6. **Test Status Transitions**:
   - Get a suggestion to 50%+ likes
   - Check if it moves to IN_DISCUSSION
   - Verify calculated priority

7. **Check Discussion Forum**:
   - View all IN_DISCUSSION suggestions
   - Verify they're ordered by priority

### Postman Collection

Create a Postman collection with the above endpoints for easy testing.

**Environment Variables**:
```
base_url: http://localhost:8080/api
token: (set after login)
```

## Security

- **JWT Authentication**: All endpoints except /auth/** and /localities/all require authentication
- **Role-Based Access**: 
  - ADMIN: Can create/update localities
  - RESIDENT: Can create suggestions, vote
- **Password Encryption**: BCrypt with salt
- **CORS**: Configure as needed for frontend integration

## Future Enhancements

- [ ] Comments on suggestions
- [ ] Image upload for suggestions
- [ ] Email notifications
- [ ] Admin dashboard with analytics
- [ ] Export reports
- [ ] Mobile app integration
- [ ] Real-time updates using WebSocket
- [ ] Search and filter suggestions
- [ ] User reputation system

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please contact the development team.
