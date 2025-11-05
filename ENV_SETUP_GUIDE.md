# 🔐 Environment Variables Setup Guide

## 📋 Quick Start

### 1. Copy `.env.example` to `.env`

```bash
# Windows (PowerShell)
Copy-Item .env.example .env

# Linux/Mac
cp .env.example .env
```

### 2. Update `.env` with your credentials

Open `.env` file and update the values:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=clinic
DB_USERNAME=postgres
DB_PASSWORD=YOUR_ACTUAL_PASSWORD_HERE  # ⚠️ Change this!

# Server Configuration
SERVER_PORT=8000

# JWT Configuration
JWT_SECRET=YOUR_SECRET_KEY_HERE  # ⚠️ Generate a new one!
JWT_EXPIRATION=3600000

# Spring Profile
SPRING_PROFILE=dev

# Logging Level
LOG_LEVEL_SQL=DEBUG
LOG_LEVEL_JPA=DEBUG
```

### 3. Generate a Secure JWT Secret

You can generate a secure JWT secret using one of these methods:

**Method 1: Online Generator**
- Visit: https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
- Select: 256-bit
- Click: Generate
- Copy the Hex key

**Method 2: OpenSSL (Terminal)**
```bash
openssl rand -hex 32
```

**Method 3: Node.js (if you have it installed)**
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

**Method 4: PowerShell**
```powershell
-join ((48..57) + (65..70) | Get-Random -Count 64 | % {[char]$_})
```

### 4. Run the Application

```bash
# Compile and install dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

---

## 📁 File Structure

```
BE-Online-Doctor-Appointment/
├── .env                          # ❌ Git ignored - Your actual credentials
├── .env.example                  # ✅ Template - Committed to Git
├── .gitignore                    # Contains .env
├── src/
│   └── main/
│       └── resources/
│           ├── application.yml              # ❌ Old - Has hardcoded values
│           └── application-secure.yml       # ✅ New - Uses env variables
└── pom.xml                       # Contains spring-dotenv dependency
```

---

## 🔄 Migration from `application.yml` to Environment Variables

### Before (❌ Insecure - Hardcoded)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/clinic
    username: postgres
    password: Binhkute2006@  # ⚠️ Password exposed in Git!
```

### After (✅ Secure - Environment Variables)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:clinic}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}  # ✅ Read from .env file
```

---

## 🌍 Environment Variables Explained

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | PostgreSQL host | `localhost` | ✅ Yes |
| `DB_PORT` | PostgreSQL port | `5432` | ✅ Yes |
| `DB_NAME` | Database name | `clinic` | ✅ Yes |
| `DB_USERNAME` | Database username | `postgres` | ✅ Yes |
| `DB_PASSWORD` | Database password | `YourPassword123` | ✅ Yes |
| `SERVER_PORT` | Spring Boot server port | `8000` | ✅ Yes |
| `JWT_SECRET` | Secret key for JWT signing | 64-char hex string | ✅ Yes |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `3600000` (1 hour) | ✅ Yes |
| `SPRING_PROFILE` | Active Spring profile | `dev`, `prod` | ❌ No (default: `dev`) |
| `LOG_LEVEL_SQL` | SQL logging level | `DEBUG`, `INFO` | ❌ No (default: `DEBUG`) |
| `LOG_LEVEL_JPA` | JPA logging level | `DEBUG`, `INFO` | ❌ No (default: `DEBUG`) |

---

## 🔒 Security Best Practices

### ✅ DO:
- ✅ **Keep `.env` in `.gitignore`** - Never commit it!
- ✅ **Use `.env.example`** - Share template with team
- ✅ **Generate unique JWT secrets** - Don't reuse keys
- ✅ **Use strong database passwords** - Min 12 characters
- ✅ **Rotate secrets regularly** - Change every 3-6 months
- ✅ **Use different secrets per environment** - dev/staging/prod

### ❌ DON'T:
- ❌ **Don't commit `.env` to Git** - Contains real credentials
- ❌ **Don't share `.env` via email/chat** - Use secure channels
- ❌ **Don't use default passwords** - Change them immediately
- ❌ **Don't reuse production secrets in dev** - Separate environments
- ❌ **Don't hardcode secrets in code** - Always use env vars

---

## 🚀 Deployment

### Production Environment Variables

For production, set environment variables directly on your server/cloud platform:

**Heroku:**
```bash
heroku config:set DB_HOST=your-prod-db-host
heroku config:set DB_PASSWORD=your-prod-password
heroku config:set JWT_SECRET=your-prod-jwt-secret
```

**Docker:**
```bash
docker run -e DB_HOST=localhost -e DB_PASSWORD=secret my-app
```

**AWS/Azure/GCP:**
- Use their secret management services:
  - AWS: AWS Secrets Manager
  - Azure: Azure Key Vault
  - GCP: Secret Manager

---

## 🧪 Testing with Different Environments

### Development (.env)
```env
SPRING_PROFILE=dev
DB_NAME=clinic_dev
JWT_EXPIRATION=3600000  # 1 hour
LOG_LEVEL_SQL=DEBUG
```

### Production (.env.production)
```env
SPRING_PROFILE=prod
DB_NAME=clinic_prod
JWT_EXPIRATION=86400000  # 24 hours
LOG_LEVEL_SQL=ERROR
```

Run with specific env file:
```bash
# Load from .env.production
export $(grep -v '^#' .env.production | xargs)
./mvnw spring-boot:run
```

---

## 🐛 Troubleshooting

### Problem: "Cannot connect to database"
**Solution:** Check your `.env` file has correct database credentials:
```env
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=your_actual_password
```

### Problem: "Invalid JWT secret"
**Solution:** Generate a new JWT_SECRET (minimum 256 bits):
```bash
openssl rand -hex 32
```

### Problem: "Environment variables not loaded"
**Solution:** 
1. Ensure `spring-dotenv` dependency is in `pom.xml`
2. Ensure `.env` file exists in project root
3. Restart the application

### Problem: ".env file not found"
**Solution:** Create `.env` file from template:
```bash
cp .env.example .env
```

---

## 📚 Additional Resources

- [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Dotenv Documentation](https://github.com/paulschwarz/spring-dotenv)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)

---

## 📞 Support

If you encounter any issues:
1. Check this README
2. Review `.env.example` for correct format
3. Contact the dev team

**Last Updated:** November 4, 2025  
**Version:** 1.0
