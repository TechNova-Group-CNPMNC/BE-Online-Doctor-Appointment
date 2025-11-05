# 🏥 Online Doctor Appointment System - Backend

A comprehensive RESTful API for managing online doctor appointments, built with Spring Boot 3.5.7 and Java 21.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [Environment Setup](#-environment-setup)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Database Schema](#-database-schema)
- [Contributing](#-contributing)

---

## ✨ Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 👥 **Role-Based Access Control** - PATIENT, DOCTOR, ADMIN roles
- 📅 **Appointment Management** - Book, reschedule, and cancel appointments
- 🏥 **Doctor Availability** - Manage working hours with 30-minute time slots
- ⭐ **Rating System** - Patients can rate doctors and provide feedback
- 🔍 **Search & Filter** - Find doctors by specialty, name, and availability
- 📧 **Email Reminders** - Automatic appointment reminders
- 🌐 **CORS Enabled** - Ready for frontend integration

---

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.5.7
- **Language:** Java 21
- **Database:** PostgreSQL 18.0
- **Security:** Spring Security + JWT
- **ORM:** Hibernate/JPA
- **Build Tool:** Maven
- **Environment Management:** Spring Dotenv

---

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- PostgreSQL 18.0 or higher
- Maven 3.8+ (or use included Maven wrapper)

### 1. Clone the Repository

\`\`\`bash
git clone https://github.com/TechNova-Group-CNPMNC/BE-Online-Doctor-Appointment.git
cd BE-Online-Doctor-Appointment
\`\`\`

### 2. Setup Environment Variables

**Option A: Automatic Setup (Recommended)**

\`\`\`bash
# Windows (PowerShell)
.\setup-env.ps1

# Linux/Mac
chmod +x setup-env.sh
./setup-env.sh
\`\`\`

**Option B: Manual Setup**

\`\`\`bash
# Copy template
cp .env.example .env

# Edit .env with your credentials
nano .env  # or use any text editor
\`\`\`

📖 **Detailed instructions:** [ENV_SETUP_GUIDE.md](ENV_SETUP_GUIDE.md)

### 3. Create Database

\`\`\`sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE clinic;

-- Exit
\q
\`\`\`

### 4. Run the Application

\`\`\`bash
# Compile and install dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run
\`\`\`

The API will be available at: **http://localhost:8000**

---

## 🔐 Environment Setup

### Required Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| \`DB_HOST\` | PostgreSQL host | \`localhost\` |
| \`DB_PORT\` | PostgreSQL port | \`5432\` |
| \`DB_NAME\` | Database name | \`clinic\` |
| \`DB_USERNAME\` | Database username | \`postgres\` |
| \`DB_PASSWORD\` | Database password | \`YourSecurePassword\` |
| \`JWT_SECRET\` | Secret key for JWT (256-bit) | \`64-character hex string\` |
| \`JWT_EXPIRATION\` | Token expiration (ms) | \`3600000\` (1 hour) |
| \`SERVER_PORT\` | Application port | \`8000\` |

### Generate Secure JWT Secret

\`\`\`bash
# Using OpenSSL
openssl rand -hex 32

# Using PowerShell
-join ((48..57) + (65..70) | Get-Random -Count 64 | % {[char]$_})
\`\`\`

📖 **Complete guide:** [ENV_SETUP_GUIDE.md](ENV_SETUP_GUIDE.md)

---

## 📚 API Documentation

### Base URL
\`\`\`
http://localhost:8000
\`\`\`

### Quick Reference

#### 🔐 Authentication (Public)
- \`POST /api/auth/register\` - Register patient
- \`POST /api/auth/login\` - Login

#### 🏥 Specialties (Public)
- \`GET /api/specialties\` - Get all specialties

#### 👨‍⚕️ Doctors (Authenticated)
- \`GET /api/doctors\` - Get all doctors
- \`GET /api/doctors/search\` - Search doctors
- \`GET /api/doctors/{id}/detail\` - Get doctor details

#### 📅 Availability (Doctor Only)
- \`POST /api/doctors/{id}/availability\` - Create availability block
- \`GET /api/doctors/{id}/availability\` - Get all blocks
- \`DELETE /api/doctors/{id}/availability/{blockId}\` - Delete block

#### 📋 Appointments (Patient Only)
- \`POST /api/appointments\` - Create appointment

📖 **Full API documentation:** [API Documents.md](wiki/API%20Documents.md)

### Postman Collection

Import the ready-to-use Postman collection:

\`\`\`
File: Online-Doctor-Appointment-API.postman_collection.json
Environment: Local-Development.postman_environment.json
\`\`\`

📖 **Postman guides:**
- [Quick Reference](wiki/POSTMAN_QUICK_REF.md)
- [Complete Guide](wiki/POSTMAN_GUIDE.md)
- [API Testing](wiki/API_TESTING_README.md)

---

## 🔒 Security

### Authentication Flow

1. **Register/Login** → Receive JWT token
2. **Include token in requests:**
   \`\`\`
   Authorization: Bearer <your-jwt-token>
   \`\`\`
3. **Token expires** after configured time (default: 1 hour)

### Authorization Matrix

| Endpoint | Public | Patient | Doctor | Admin |
|----------|--------|---------|--------|-------|
| Register/Login | ✅ | ✅ | ✅ | ✅ |
| Get Specialties | ✅ | ✅ | ✅ | ✅ |
| Search Doctors | ❌ | ✅ | ✅ | ✅ |
| Create Availability | ❌ | ❌ | ✅ | ✅ |
| Book Appointment | ❌ | ✅ | ❌ | ✅ |

### Security Features

- ✅ **Password Encryption** - BCrypt hashing
- ✅ **JWT Tokens** - Stateless authentication
- ✅ **Role-Based Access** - Fine-grained permissions
- ✅ **CORS Protection** - Configured for specific origins
- ✅ **SQL Injection Prevention** - JPA/Hibernate protection
- ✅ **Resource Ownership Verification** - Users can only access their own data

---

## 🗄️ Database Schema

See [README.md Database Schema Section](#database-schema-details) for complete schema.

### Core Entities

- **Users** - Authentication and user management
- **Patients** - Patient profiles
- **Doctors** - Doctor profiles and specialties
- **AvailabilityBlocks** - Doctor working hours
- **TimeSlots** - 30-minute appointment slots
- **Appointments** - Booked appointments
- **Ratings** - Patient feedback and ratings

---

## 🧪 Testing

### Run Tests

\`\`\`bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AvailabilityBlockServiceTest

# Run with coverage
./mvnw clean verify jacoco:report
\`\`\`

### Test with Postman

1. Import collection: \`Online-Doctor-Appointment-API.postman_collection.json\`
2. Set environment: \`Local-Development.postman_environment.json\`
3. Run collection or individual requests

📖 **Testing guide:** [API_TESTING_README.md](wiki/API_TESTING_README.md)

---

## 🚀 Deployment

### Production Checklist

- [ ] Update \`.env\` with production credentials
- [ ] Use strong JWT secret (rotate regularly)
- [ ] Configure HTTPS
- [ ] Set \`SPRING_PROFILE=prod\`
- [ ] Disable \`show-sql\` in production
- [ ] Set up database backups
- [ ] Configure monitoring/logging
- [ ] Review CORS settings

### Environment-Specific Configs

\`\`\`bash
# Development
SPRING_PROFILE=dev
LOG_LEVEL_SQL=DEBUG

# Production
SPRING_PROFILE=prod
LOG_LEVEL_SQL=ERROR
\`\`\`

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: \`git checkout -b feature/amazing-feature\`
3. Commit changes: \`git commit -m 'Add amazing feature'\`
4. Push to branch: \`git push origin feature/amazing-feature\`
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Team

**TechNova Group - CNPMNC**

- Backend Development
- API Design
- Security Implementation
- Database Architecture

---

## 📞 Support

- 📧 Email: support@technova-group.com
- 📖 Documentation: [wiki/](wiki/)
- 🐛 Issues: [GitHub Issues](https://github.com/TechNova-Group-CNPMNC/BE-Online-Doctor-Appointment/issues)

---

## 🔗 Related Documentation

- [Environment Setup Guide](ENV_SETUP_GUIDE.md)
- [API Documentation](wiki/API%20Documents.md)
- [Postman Quick Reference](wiki/POSTMAN_QUICK_REF.md)
- [Partial Delete Guide](PARTIAL_DELETE_GUIDE.md)
- [JWT Auth Tests](wiki/JWT_AUTH_TESTS.md)

---

**Last Updated:** November 4, 2025  
**Version:** 1.0.0
