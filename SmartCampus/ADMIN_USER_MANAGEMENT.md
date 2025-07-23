# Admin User Management Guide

## Overview

This document outlines how administrators can create and manage professor and admin accounts in the SmartCampus system. Public registration is restricted to students only for security reasons.

## 🔒 **Security Policy**

### Public Registration Restrictions
- ✅ **Students**: Can register through the public `/api/auth/register` endpoint
- ❌ **Professors**: Must be created by administrators
- ❌ **Administrators**: Must be created by existing administrators

### Rationale
- **Security**: Prevents unauthorized access to administrative functions
- **Control**: Ensures only qualified individuals receive elevated privileges
- **Audit Trail**: All non-student accounts are created through controlled processes

## 👨‍💼 **Creating Professor Accounts**

### Method 1: Direct Database Insert (Development)
```sql
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
VALUES (
    'Dr. Jane Smith',
    'jane.smith@university.edu',
    '$2a$10$encoded_password_hash', -- Use BCrypt encoder
    'PROFESSOR',
    true,
    NOW(),
    NOW()
);
```

### Method 2: Admin API Endpoint (Recommended)
```bash
POST /api/admin/users
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "name": "Dr. Jane Smith",
  "email": "jane.smith@university.edu",
  "password": "SecurePassword123!",
  "role": "PROFESSOR"
}
```

### Method 3: Admin Interface (Future Implementation)
- Access admin dashboard at `/admin`
- Navigate to "User Management"
- Click "Create Professor Account"
- Fill in required details
- Submit for approval

## 🔧 **Creating Administrator Accounts**

### Method 1: Direct Database Insert (Development)
```sql
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
VALUES (
    'Admin User',
    'admin@university.edu',
    '$2a$10$encoded_password_hash', -- Use BCrypt encoder
    'ADMIN',
    true,
    NOW(),
    NOW()
);
```

### Method 2: Admin API Endpoint (Recommended)
```bash
POST /api/admin/users
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@university.edu",
  "password": "SecurePassword123!",
  "role": "ADMIN"
}
```

## 🛠 **Implementation Details**

### Backend Validation
The registration endpoint includes validation to prevent non-STUDENT roles:

```java
@Override
public User register(RegisterRequest request) {
    // Ensure only STUDENT role is allowed for public registration
    Role userRole = request.getRole();
    if (userRole == null) {
        userRole = Role.STUDENT; // Default to STUDENT if no role provided
    } else if (userRole != Role.STUDENT) {
        throw new RuntimeException("Only STUDENT role is allowed for public registration. " +
                                 "Professors and Admins must be created by administrators.");
    }
    
    // ... rest of registration logic
}
```

### Frontend Restrictions
The registration form automatically assigns STUDENT role:

```typescript
const requestBody = {
  name: data.name.trim(),
  email: data.email.trim().toLowerCase(),
  password: data.password,
  role: 'STUDENT' as const // Automatically assign STUDENT role
};
```

## 📋 **Admin User Creation Checklist**

### For Professor Accounts:
- [ ] Verify academic credentials
- [ ] Confirm university email address
- [ ] Set appropriate department/field
- [ ] Assign initial courses (if applicable)
- [ ] Send welcome email with login credentials
- [ ] Document creation in admin logs

### For Administrator Accounts:
- [ ] Verify administrative authority
- [ ] Confirm institutional email address
- [ ] Set appropriate access levels
- [ ] Provide security training
- [ ] Send welcome email with login credentials
- [ ] Document creation in admin logs

## 🔐 **Security Best Practices**

### Password Requirements
- Minimum 8 characters
- Include uppercase and lowercase letters
- Include numbers and special characters
- Avoid common patterns and dictionary words

### Account Management
- Regular review of admin accounts
- Immediate deactivation of unused accounts
- Audit logging for all administrative actions
- Two-factor authentication for admin accounts (future)

### Access Control
- Principle of least privilege
- Regular access reviews
- Immediate revocation upon role changes
- Secure credential distribution

## 🚨 **Emergency Procedures**

### Account Compromise
1. Immediately disable the compromised account
2. Investigate the security breach
3. Reset all related passwords
4. Review access logs for suspicious activity
5. Document the incident and response

### Admin Account Loss
1. Use backup admin accounts
2. Create new admin account through database
3. Verify system integrity
4. Review and update security procedures

## 📞 **Support and Contact**

For assistance with user management:
- **Technical Support**: tech-support@university.edu
- **Security Team**: security@university.edu
- **System Administrator**: sysadmin@university.edu

## 📚 **Related Documentation**

- [API Documentation](./API_DOCUMENTATION.md)
- [Security Configuration](./SECURITY_CONFIG.md)
- [User Roles and Permissions](./USER_ROLES.md)
- [Database Schema](./DATABASE_SCHEMA.md) 