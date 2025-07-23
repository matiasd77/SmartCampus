# 403 Forbidden Error Debugging Guide

## 🔍 **Problem Analysis**

You're getting a 403 Forbidden error when calling `/api/users/me` with a valid JWT token. This indicates that:

1. **Authentication is working** (token is valid and being sent)
2. **Authorization is failing** (user doesn't have required permissions)
3. **Role/Authority mismatch** (token doesn't contain expected roles)

## 🛠️ **Root Causes & Solutions**

### 1. **Role Mismatch in JWT Token**

**Problem**: The JWT token doesn't contain the correct role information.

**Solution**: Enhanced JWT token generation to include authorities:

```java
// In JwtTokenProvider.java
public String generateToken(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    
    // Extract authorities and convert to role names
    List<String> authorities = userDetails.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.toList());

    return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("authorities", authorities)  // ✅ Added authorities claim
            .claim("roles", authorities)        // ✅ Added roles claim
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
}
```

### 2. **Missing Role Prefix**

**Problem**: Spring Security expects roles with "ROLE_" prefix.

**Solution**: Ensure CustomUserDetailsService adds the prefix:

```java
// In CustomUserDetailsService.java
return new org.springframework.security.core.userdetails.User(
    user.getEmail(),
    user.getPassword(),
    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
);
```

### 3. **Security Configuration Issues**

**Problem**: The `/api/users/me` endpoint might not be properly configured.

**Solution**: Added explicit configuration in SecurityConfig:

```java
// In SecurityConfig.java
.requestMatchers("/api/users/me").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
.requestMatchers("/api/users/me/**").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
```

### 4. **Enhanced Error Handling**

**Problem**: Poor error messages make debugging difficult.

**Solution**: Added comprehensive logging and error handling:

```java
// In UserController.java
public ResponseEntity<ApiResponse<UserDTO>> getCurrentUserProfile() {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(/* ... */);
        }
        
        String email = authentication.getName();
        
        // Log authentication details for debugging
        log.debug("Getting current user profile for email: {} with authorities: {}", 
                email, authentication.getAuthorities());
        
        // ... rest of the method
    } catch (Exception e) {
        log.error("Error getting current user profile", e);
        return ResponseEntity.status(500).body(/* ... */);
    }
}
```

## 🔧 **Debugging Steps**

### Step 1: Check JWT Token Content

1. **Decode the JWT token** at [jwt.io](https://jwt.io)
2. **Look for authorities/roles claims** in the payload
3. **Verify the role format** (should be "ROLE_STUDENT", "ROLE_PROFESSOR", etc.)

### Step 2: Check Backend Logs

Look for these log messages:

```bash
# JWT token generation
Generating JWT token for user: user@example.com with authorities: [ROLE_STUDENT]

# Authentication in filter
Authenticating user: user@example.com with authorities: [ROLE_STUDENT]
Authentication set in SecurityContext for user: user@example.com

# Controller access
Getting current user profile for email: user@example.com with authorities: [ROLE_STUDENT]
```

### Step 3: Check Frontend Console

Look for these log messages:

```javascript
// Token validation
AuthContext.checkAuth - Validating token...
AuthContext.checkAuth - Token validation successful

// API requests
API Request: GET /users/me - Authorization header included
authAPI.getCurrentUser - Making request to /users/me
authAPI.getCurrentUser - Response: { success: true, data: {...} }
```

### Step 4: Test with Different Users

1. **Create a test user with ADMIN role**
2. **Try logging in with different roles**
3. **Check if the issue is role-specific**

## 🧪 **Testing Commands**

### 1. **Test Backend Health**

```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. **Test Authentication Endpoint**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### 3. **Test Profile Endpoint with Token**

```bash
# Replace YOUR_JWT_TOKEN with the actual token
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 4. **Test with Different Roles**

```bash
# Test with ADMIN role
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Test with STUDENT role  
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

## 🚨 **Common Issues & Fixes**

### Issue 1: Token Missing Authorities

**Symptoms**: 403 error, no authorities in JWT payload

**Fix**: Ensure JWT token generation includes authorities:

```java
.claim("authorities", authorities)
.claim("roles", authorities)
```

### Issue 2: Wrong Role Format

**Symptoms**: 403 error, authorities present but wrong format

**Fix**: Ensure roles have "ROLE_" prefix:

```java
new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
```

### Issue 3: User Not Found

**Symptoms**: 404 error after authentication

**Fix**: Check if user exists in database:

```sql
SELECT * FROM users WHERE email = 'user@example.com';
```

### Issue 4: Token Expired

**Symptoms**: 401 error, not 403

**Fix**: Check token expiration and refresh if needed

### Issue 5: CORS Issues

**Symptoms**: Network errors in browser console

**Fix**: Verify CORS configuration allows your frontend origin

## 📋 **Checklist for Resolution**

- [ ] JWT token contains authorities/roles claims
- [ ] Roles have "ROLE_" prefix (ROLE_STUDENT, ROLE_PROFESSOR, ROLE_ADMIN)
- [ ] Security configuration allows `/api/users/me` for authenticated users
- [ ] User exists in database with correct role
- [ ] Token is not expired
- [ ] CORS is properly configured
- [ ] Backend logs show successful authentication
- [ ] Frontend logs show successful API calls

## 🔍 **Advanced Debugging**

### 1. **Enable Debug Logging**

Add to `application.properties`:

```properties
# Enable debug logging for security
logging.level.org.springframework.security=DEBUG
logging.level.com.smartcampus=DEBUG

# Enable JWT debugging
logging.level.com.smartcampus.config=DEBUG
```

### 2. **Check Database Role Values**

```sql
-- Check user roles
SELECT id, email, role FROM users WHERE email = 'user@example.com';

-- Check role enum values
SELECT enumlabel FROM pg_enum WHERE enumtypid = 'role'::regtype;
```

### 3. **Verify JWT Token Structure**

```javascript
// In browser console
const token = localStorage.getItem('token');
const payload = JSON.parse(atob(token.split('.')[1]));
console.log('JWT Payload:', payload);
console.log('Authorities:', payload.authorities);
console.log('Roles:', payload.roles);
```

### 4. **Test with Postman**

1. **Login endpoint**: `POST /api/auth/login`
2. **Copy JWT token** from response
3. **Profile endpoint**: `GET /api/users/me` with Authorization header
4. **Check response** and status code

## ✅ **Expected Results After Fixes**

1. **JWT token contains authorities**: `["ROLE_STUDENT"]`
2. **Backend logs show authentication**: User authenticated with correct authorities
3. **Controller logs show access**: Profile request processed successfully
4. **Frontend receives 200 response**: Profile data returned successfully
5. **No 403 errors**: Authorization working correctly

## 🆘 **If Issues Persist**

1. **Check backend startup logs** for any configuration errors
2. **Verify database connection** and user data integrity
3. **Test with a fresh user account** with known role
4. **Check for any custom security filters** that might interfere
5. **Verify Spring Security version** compatibility

## 📞 **Support Information**

If you continue to experience issues:

1. **Collect logs**: Backend and frontend console logs
2. **JWT token**: Decoded payload (remove sensitive data)
3. **User details**: Email and role (without password)
4. **Error messages**: Exact error text and status codes
5. **Steps to reproduce**: Detailed steps from login to error 