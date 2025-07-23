# 403 Forbidden Errors - Comprehensive Fix Guide

## 🔍 **Problem Summary**

You're experiencing 403 Forbidden errors across multiple protected API endpoints:
- `/api/students`
- `/api/professors`
- `/api/announcements/stats/count/active`
- `/api/notifications/unread/count`
- `/api/announcements/recent`
- `/api/users/me`

## 🛠️ **Fixes Implemented**

### 1. **Enhanced Axios Request Interceptor**

**File**: `src/services/api.ts`

**Changes**:
- ✅ Added detailed token logging for debugging
- ✅ Enhanced token expiration checking
- ✅ Better error handling for malformed tokens
- ✅ Comprehensive request logging

**Key Improvements**:
```typescript
// Enhanced token validation and logging
if (token && token.trim() !== '') {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    
    if (payload.exp < currentTime) {
      console.warn('Token is expired, clearing auth data');
      clearAuthData();
    } else {
      config.headers.Authorization = `Bearer ${token}`;
      
      // Log token details for debugging
      if (import.meta.env.DEV) {
        console.log('Token Details:', {
          subject: payload.sub,
          authorities: payload.authorities || payload.roles || 'No authorities found',
          issuedAt: new Date(payload.iat * 1000).toISOString(),
          expiresAt: new Date(payload.exp * 1000).toISOString(),
          issuer: payload.iss
        });
      }
    }
  } catch (error) {
    console.error('Error parsing JWT token:', error);
    clearAuthData();
  }
}
```

### 2. **Enhanced Response Interceptor with 403 Handling**

**File**: `src/services/api.ts`

**Changes**:
- ✅ Added specific 403 error handling
- ✅ Detailed logging for authorization failures
- ✅ Token analysis on 403 errors
- ✅ Better error categorization

**Key Improvements**:
```typescript
// Handle authorization errors (403 Forbidden)
if (error.response?.status === 403) {
  console.error('API: Forbidden request (403), authorization error');
  console.error('API: 403 Error Details:', {
    url: error.config?.url,
    method: error.config?.method,
    headers: error.config?.headers,
    responseData: error.response?.data,
    token: error.config?.headers?.Authorization ? 'Present' : 'Missing'
  });
  
  // Log the current token for debugging
  const token = getStoredToken();
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.error('API: Current token details:', {
        subject: payload.sub,
        authorities: payload.authorities || payload.roles || 'No authorities found',
        issuedAt: new Date(payload.iat * 1000).toISOString(),
        expiresAt: new Date(payload.exp * 1000).toISOString()
      });
    } catch (e) {
      console.error('API: Failed to parse current token:', e);
    }
  }
}
```

### 3. **Enhanced AuthContext with Better Token Validation**

**File**: `src/contexts/AuthContext.tsx`

**Changes**:
- ✅ Token format validation before setting state
- ✅ Token expiration checking during initialization
- ✅ Better error handling for 401 vs 403 errors
- ✅ Enhanced logging for debugging

**Key Improvements**:
```typescript
// Validate token format and expiration before setting
try {
  const payload = JSON.parse(atob(savedToken.split('.')[1]));
  const currentTime = Date.now() / 1000;
  
  if (payload.exp < currentTime) {
    console.warn('AuthContext: Token is expired, clearing auth data');
    clearAuthData();
    setToken(null);
    setUser(null);
    return;
  }
  
  console.log('AuthContext: Token format is valid, expires at:', new Date(payload.exp * 1000).toISOString());
  console.log('AuthContext: Token authorities:', payload.authorities || payload.roles || 'No authorities found');
  
} catch (error) {
  console.error('AuthContext: Invalid token format, clearing auth data:', error);
  clearAuthData();
  setToken(null);
  setUser(null);
  return;
}
```

### 4. **Token Debugging Utility**

**File**: `src/utils/tokenDebugger.ts` (New)

**Features**:
- ✅ JWT token analysis and validation
- ✅ Authority/role checking
- ✅ Token expiration analysis
- ✅ API endpoint testing
- ✅ Comprehensive debugging tools

**Key Functions**:
```typescript
// Analyze JWT token
export const analyzeToken = (token: string): TokenDebugInfo => {
  // Comprehensive token analysis
};

// Debug current authentication state
export const debugAuthState = (): void => {
  // Full auth state debugging
};

// Test API endpoints
export const testApiEndpoint = async (endpoint: string): Promise<void> => {
  // Test individual endpoints
};

// Test all problematic endpoints
export const testAllEndpoints = async (): Promise<void> => {
  // Test all endpoints at once
};
```

### 5. **Enhanced Profile Component with Debug Tools**

**File**: `src/pages/Profile.tsx`

**Changes**:
- ✅ Added "Debug Auth" button
- ✅ Added "Test Endpoints" button
- ✅ Enhanced error state with debugging options
- ✅ Better user feedback

## 🔧 **Debugging Steps**

### Step 1: Use the Debug Tools

1. **Navigate to Profile page** (or any page with error)
2. **Click "Debug Auth"** to analyze current token
3. **Click "Test Endpoints"** to test all problematic endpoints
4. **Check browser console** for detailed logs

### Step 2: Check Token Content

In browser console, run:
```javascript
// Analyze current token
debugAuthState();

// Test specific endpoint
testApiEndpoint('/users/me');

// Test all endpoints
testAllEndpoints();
```

### Step 3: Verify Backend Configuration

Check that your Spring Security configuration allows the required roles:

```java
// In SecurityConfig.java
.requestMatchers("/api/students/**").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
.requestMatchers("/api/professors/**").hasAnyRole("PROFESSOR", "ADMIN")
.requestMatchers("/api/announcements/**").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
.requestMatchers("/api/notifications/**").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
```

### Step 4: Check JWT Token Generation

Ensure your backend JWT token includes authorities:

```java
// In JwtTokenProvider.java
return Jwts.builder()
    .setSubject(userDetails.getUsername())
    .claim("authorities", authorities)  // ✅ Must include authorities
    .claim("roles", authorities)        // ✅ Alternative claim name
    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
    .compact();
```

## 🚨 **Common Issues & Solutions**

### Issue 1: Token Missing Authorities

**Symptoms**: 403 errors, no authorities in JWT payload

**Solution**: Ensure JWT token generation includes authorities:
```java
.claim("authorities", authorities)
.claim("roles", authorities)
```

### Issue 2: Wrong Role Format

**Symptoms**: 403 errors, authorities present but wrong format

**Solution**: Ensure roles have "ROLE_" prefix:
```java
new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
```

### Issue 3: User Role Mismatch

**Symptoms**: 403 errors on role-specific endpoints

**Solution**: Check user role in database:
```sql
SELECT id, email, role FROM users WHERE email = 'user@example.com';
```

### Issue 4: Token Expiration

**Symptoms**: 401 errors (not 403)

**Solution**: Check token expiration and refresh if needed

## 🧪 **Testing Commands**

### 1. **Test Backend Health**
```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. **Test Authentication**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### 3. **Test Protected Endpoint**
```bash
# Replace YOUR_JWT_TOKEN with actual token
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 4. **Test with Different Roles**
```bash
# Test with ADMIN role
curl -X GET http://localhost:8080/api/professors \
  -H "Authorization: Bearer ADMIN_TOKEN"

# Test with STUDENT role
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer STUDENT_TOKEN"
```

## 📋 **Debugging Checklist**

- [ ] JWT token contains authorities/roles claims
- [ ] Roles have "ROLE_" prefix (ROLE_STUDENT, ROLE_PROFESSOR, ROLE_ADMIN)
- [ ] Security configuration allows endpoints for user's role
- [ ] User exists in database with correct role
- [ ] Token is not expired
- [ ] CORS is properly configured
- [ ] Backend logs show successful authentication
- [ ] Frontend logs show successful API calls
- [ ] Authorization headers are being sent correctly

## 🔍 **Advanced Debugging**

### 1. **Enable Backend Debug Logging**

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
3. **Test protected endpoints** with Authorization header
4. **Check response** and status code

## ✅ **Expected Results After Fixes**

1. **JWT token contains authorities**: `["ROLE_STUDENT"]`
2. **Backend logs show authentication**: User authenticated with correct authorities
3. **Controller logs show access**: Requests processed successfully
4. **Frontend receives 200 responses**: Data returned successfully
5. **No 403 errors**: Authorization working correctly
6. **Debug tools available**: Easy troubleshooting with built-in tools

## 🆘 **If Issues Persist**

1. **Check backend startup logs** for configuration errors
2. **Verify database connection** and user data integrity
3. **Test with a fresh user account** with known role
4. **Check for custom security filters** that might interfere
5. **Verify Spring Security version** compatibility
6. **Use debug tools** to analyze token and test endpoints

## 📞 **Support Information**

If you continue to experience issues:

1. **Collect logs**: Backend and frontend console logs
2. **JWT token**: Decoded payload (remove sensitive data)
3. **User details**: Email and role (without password)
4. **Error messages**: Exact error text and status codes
5. **Steps to reproduce**: Detailed steps from login to error
6. **Debug tool output**: Results from debugAuthState() and testAllEndpoints() 