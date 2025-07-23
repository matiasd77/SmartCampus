# JWT Security Configuration Guide

This guide explains how to properly configure JWT security for the SmartCampus application.

## 🔐 JWT Key Requirements

The application uses **HS512** algorithm for JWT signing, which requires:
- **Minimum key length**: 64 bytes (512 bits)
- **Recommended**: Base64-encoded key for production

## 🚀 Development Mode

In development mode, the application will:
1. Detect if you're using the default secret
2. Auto-generate a secure 512-bit key
3. Log a warning about using the generated key
4. Work seamlessly without additional configuration

## 🏭 Production Mode

For production deployment, follow these steps:

### Step 1: Generate a Secure Key

#### Option A: Using the Utility Class
```bash
# Compile the application first
mvn compile

# Generate a secure key
java -cp target/classes com.smartcampus.util.JwtKeyGenerator
```

#### Option B: Using OpenSSL
```bash
# Generate 64 bytes of random data and encode as Base64
openssl rand -base64 64
```

#### Option C: Using Java
```bash
# Run this in a Java environment
java -cp target/classes com.smartcampus.util.JwtKeyGenerator
```

### Step 2: Configure the Key

Add the generated key to your `application.properties` or environment variables:

```properties
# application.properties
jwt.secret=YOUR_GENERATED_BASE64_KEY_HERE
```

Or set as environment variable:
```bash
export JWT_SECRET=YOUR_GENERATED_BASE64_KEY_HERE
```

### Step 3: Verify Configuration

The application will log the key initialization:
```
INFO  - Using Base64-encoded JWT secret from configuration
INFO  - JWT signing key initialized successfully. Key length: 64 bytes
```

## 🔧 Configuration Options

### Development Configuration
```properties
# Auto-generates secure key
jwt.secret=your-secret-key-here-make-it-long-and-secure-for-production
```

### Production Configuration
```properties
# Use Base64-encoded key
jwt.secret=YOUR_ACTUAL_BASE64_ENCODED_KEY_HERE
```

### Environment Variables
```properties
# Can be overridden with environment variable
jwt.secret=${JWT_SECRET:your-secret-key-here-make-it-long-and-secure-for-production}
```

## 🛡️ Security Best Practices

1. **Never commit real secrets to version control**
2. **Use environment variables in production**
3. **Rotate keys periodically**
4. **Use different keys for different environments**
5. **Monitor for key compromise**

## 🔍 Troubleshooting

### WeakKeyException
If you see `WeakKeyException`, it means:
- The key is too short for HS512
- The key is not properly formatted

**Solution**: Generate a new 64-byte key using the utility class.

### Key Length Validation
The application validates key strength:
```java
if (cachedSigningKey.getEncoded().length < 64) {
    throw new IllegalStateException("JWT signing key is too weak for HS512 algorithm");
}
```

### Logging
Check application logs for key initialization messages:
```
INFO  - JWT signing key initialized successfully. Key length: 64 bytes
```

## 📝 Example Key Generation

```bash
$ java -cp target/classes com.smartcampus.util.JwtKeyGenerator

=== JWT Key Generation ===
Generated secure key for HS512:
dGVzdC1zZWNyZXQta2V5LWZvci1kZXZlbG9wbWVudC1wdXJwb3Nlcy1vbmx5LWRvLW5vdC11c2UtaW4tcHJvZHVjdGlvbg==

Add this to your application.properties:
jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci1kZXZlbG9wbWVudC1wdXJwb3Nlcy1vbmx5LWRvLW5vdC11c2UtaW4tcHJvZHVjdGlvbg==
===========================
```

## 🔄 Key Rotation

To rotate JWT keys:

1. **Generate new key** using the utility
2. **Update configuration** with new key
3. **Restart application**
4. **Existing tokens will become invalid** (users need to re-login)

## 📚 Additional Resources

- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [Spring Security JWT](https://docs.spring.io/spring-security/reference/servlet/authentication/jwt.html)
- [JWT Security Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/) 