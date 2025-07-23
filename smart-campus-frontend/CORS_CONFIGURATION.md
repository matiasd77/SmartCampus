# CORS Configuration Guide

## Backend CORS Setup

To fix network errors and allow the frontend to communicate with the backend, ensure your Spring Boot backend has proper CORS configuration.

### Option 1: Global CORS Configuration (Recommended)

Add this configuration class to your Spring Boot backend:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173",  // Vite dev server
                    "http://localhost:3000",  // Alternative dev server
                    "http://127.0.0.1:5173",  // Alternative localhost
                    "http://127.0.0.1:3000"   // Alternative localhost
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### Option 2: Controller-level CORS

Add `@CrossOrigin` annotation to your controllers:

```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:3000",
    "http://127.0.0.1:5173",
    "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class YourController {
    // Your endpoints
}
```

### Option 3: Security Configuration

If using Spring Security, update your security configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

## Frontend Configuration

The frontend is already configured with:

1. **Base URL**: `http://localhost:8080/api`
2. **Credentials**: `withCredentials: true`
3. **Headers**: Proper Authorization and Content-Type headers
4. **Error Handling**: Comprehensive error handling for CORS issues

## Troubleshooting

### Common CORS Errors:

1. **"Access to fetch at 'http://localhost:8080/api/...' from origin 'http://localhost:5173' has been blocked by CORS policy"**
   - Solution: Add `http://localhost:5173` to allowed origins in backend

2. **"Request header field authorization is not allowed by Access-Control-Allow-Headers"**
   - Solution: Ensure `Authorization` header is allowed in CORS configuration

3. **"The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'"**
   - Solution: Use specific origins instead of wildcard `*` when `allowCredentials` is true

### Testing CORS:

1. Check browser's Network tab for CORS errors
2. Verify backend is running on `http://localhost:8080`
3. Verify frontend is running on `http://localhost:5173`
4. Check that JWT token is being sent in Authorization header

### Environment Variables:

Create a `.env` file in the frontend root:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_DEV_MODE=true
VITE_ENABLE_DEBUG_LOGGING=true
```

## Production Considerations

For production deployment:

1. Update allowed origins to include your production domain
2. Use HTTPS for both frontend and backend
3. Consider using a proxy or same-origin deployment
4. Implement proper security headers
5. Use environment-specific CORS configurations 