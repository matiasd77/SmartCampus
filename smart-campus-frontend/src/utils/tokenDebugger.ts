// Token debugging utility for JWT token analysis

import { getUserRole, hasAnyRole, getUserPermissions } from './authUtils';

export interface TokenInfo {
  subject: string;
  authorities: string[];
  issuedAt: Date;
  expiresAt: Date;
  issuer: string;
  isValid: boolean;
  isExpired: boolean;
  timeUntilExpiry: number; // in seconds
}

export interface TokenDebugInfo {
  token: string;
  tokenInfo: TokenInfo | null;
  error: string | null;
  recommendations: string[];
}

/**
 * Parse and analyze a JWT token
 */
export const analyzeToken = (token: string): TokenDebugInfo => {
  const recommendations: string[] = [];
  let tokenInfo: TokenInfo | null = null;
  let error: string | null = null;

  try {
    // Check if token is present
    if (!token || token.trim() === '') {
      error = 'Token is empty or missing';
      recommendations.push('Ensure token is properly stored in localStorage');
      return { token, tokenInfo, error, recommendations };
    }

    // Check if token has correct format (3 parts separated by dots)
    const parts = token.split('.');
    if (parts.length !== 3) {
      error = 'Invalid token format - should have 3 parts separated by dots';
      recommendations.push('Token appears to be malformed');
      return { token, tokenInfo, error, recommendations };
    }

    // Decode the payload
    const payload = JSON.parse(atob(parts[1]));
    const currentTime = Date.now() / 1000;

    // Extract token information
    tokenInfo = {
      subject: payload.sub || 'No subject found',
      authorities: payload.authorities || payload.roles || [],
      issuedAt: new Date(payload.iat * 1000),
      expiresAt: new Date(payload.exp * 1000),
      issuer: payload.iss || 'No issuer found',
      isValid: true,
      isExpired: payload.exp < currentTime,
      timeUntilExpiry: Math.max(0, payload.exp - currentTime)
    };

    // Check for common issues
    if (tokenInfo.authorities.length === 0) {
      recommendations.push('Token has no authorities/roles - this may cause 403 errors');
    }

    if (tokenInfo.isExpired) {
      recommendations.push('Token is expired - user should be logged out');
    }

    if (tokenInfo.timeUntilExpiry < 300) { // Less than 5 minutes
      recommendations.push('Token expires soon - consider refreshing');
    }

    // Check authority format
    const hasRolePrefix = tokenInfo.authorities.some(auth => auth.startsWith('ROLE_'));
    if (!hasRolePrefix) {
      recommendations.push('Authorities should have ROLE_ prefix (e.g., ROLE_STUDENT)');
    }

    // Check for required authorities
    const requiredRoles = ['ROLE_STUDENT', 'ROLE_PROFESSOR', 'ROLE_ADMIN'];
    const hasRequiredRole = tokenInfo.authorities.some(auth => requiredRoles.includes(auth));
    if (!hasRequiredRole) {
      recommendations.push('Token should have at least one of: ROLE_STUDENT, ROLE_PROFESSOR, ROLE_ADMIN');
    }

  } catch (e) {
    error = `Failed to parse token: ${e instanceof Error ? e.message : 'Unknown error'}`;
    recommendations.push('Token appears to be corrupted or invalid');
  }

  return { token, tokenInfo, error, recommendations };
};

/**
 * Debug current authentication state
 */
export const debugAuthState = () => {
  console.log('🔍 === AUTH STATE DEBUG ===');
  
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  
  console.log('📋 Token exists:', !!token);
  console.log('📋 User data exists:', !!user);
  
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('🔑 Token payload:', {
        subject: payload.sub,
        authorities: payload.authorities || payload.roles || 'No authorities found',
        issuedAt: new Date(payload.iat * 1000).toISOString(),
        expiresAt: new Date(payload.exp * 1000).toISOString(),
        issuer: payload.iss
      });
      
      // Check if token is expired
      const currentTime = Date.now() / 1000;
      const isExpired = payload.exp < currentTime;
      console.log('⏰ Token expired:', isExpired);
      
      if (isExpired) {
        console.warn('⚠️ Token is expired! This will cause authentication failures.');
      }
    } catch (error) {
      console.error('❌ Error parsing token:', error);
    }
  }
  
  if (user) {
    try {
      const userData = JSON.parse(user);
      console.log('👤 User data:', userData);
      
      // Check role-based permissions
      const permissions = getUserPermissions();
      console.log('🔐 User permissions:', permissions);
      
      // Check specific role access
      console.log('🎯 Role checks:');
      console.log('  - Is Admin:', hasAnyRole(['ADMIN']));
      console.log('  - Is Professor:', hasAnyRole(['PROFESSOR']));
      console.log('  - Is Student:', hasAnyRole(['STUDENT']));
      console.log('  - Can view students:', permissions.canViewStudents);
      console.log('  - Can view professors:', permissions.canViewProfessors);
      console.log('  - Can manage students:', permissions.canManageStudents);
      console.log('  - Can manage professors:', permissions.canManageProfessors);
      
    } catch (error) {
      console.error('❌ Error parsing user data:', error);
    }
  }
  
  console.log('🔍 === END AUTH DEBUG ===');
};

/**
 * Test API endpoint with current token
 */
export const testApiEndpoint = async (endpoint: string): Promise<void> => {
  console.group(`🧪 Testing API Endpoint: ${endpoint}`);
  
  const token = localStorage.getItem('token');
  
  if (!token) {
    console.error('❌ No token available');
    console.groupEnd();
    return;
  }
  
  try {
    const response = await fetch(`http://localhost:8080/api${endpoint}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('📡 Response Status:', response.status, response.statusText);
    
    if (response.ok) {
      const data = await response.json();
      console.log('✅ Success Response:', data);
    } else {
      const errorData = await response.text();
      console.error('❌ Error Response:', errorData);
      
      if (response.status === 403) {
        console.error('🚫 403 Forbidden - Authorization issue');
        const debugInfo = analyzeToken(token);
        if (debugInfo.tokenInfo) {
          console.error('🔍 Token authorities:', debugInfo.tokenInfo.authorities);
        }
      } else if (response.status === 401) {
        console.error('🔐 401 Unauthorized - Authentication issue');
      }
    }
  } catch (error) {
    console.error('❌ Network Error:', error);
  }
  
  console.groupEnd();
};

/**
 * Test all problematic endpoints
 */
export const testAllEndpoints = async (): Promise<void> => {
  console.group('🧪 Testing All Problematic Endpoints');
  
  const endpoints = [
    '/users/me',
    '/students',
    '/professors',
    '/announcements/stats/count/active',
    '/notifications/unread/count',
    '/announcements/recent'
  ];
  
  for (const endpoint of endpoints) {
    await testApiEndpoint(endpoint);
    // Small delay between requests
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  
  console.groupEnd();
};

export const testAnnouncementsEndpoint = async () => {
  console.log('🔍 Testing /announcements/stats/count/active endpoint...');
  
  try {
    // Test the specific endpoint that's failing
    const response = await fetch('http://localhost:8080/api/announcements/stats/count/active', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json',
      },
    });
    
    console.log('📊 Response status:', response.status);
    console.log('📊 Response headers:', Object.fromEntries(response.headers.entries()));
    
    if (response.ok) {
      const data = await response.json();
      console.log('✅ Success! Response data:', data);
      return { success: true, data };
    } else {
      const errorText = await response.text();
      console.error('❌ Error response:', errorText);
      return { success: false, status: response.status, error: errorText };
    }
  } catch (error: any) {
    console.error('❌ Network error:', error);
    return { success: false, error: error.message };
  }
};

export const testAllAnnouncementsEndpoints = async () => {
  console.log('🔍 Testing all announcements endpoints...');
  
  const endpoints = [
    '/announcements',
    '/announcements/active',
    '/announcements/public',
    '/announcements/stats/count/active',
    '/announcements/stats/count/urgent',
    '/announcements/stats/count/pinned',
  ];
  
  const results: Record<string, any> = {};
  
  for (const endpoint of endpoints) {
    console.log(`\n🔍 Testing ${endpoint}...`);
    try {
      const response = await fetch(`http://localhost:8080/api${endpoint}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });
      
      results[endpoint] = {
        status: response.status,
        ok: response.ok,
        statusText: response.statusText,
      };
      
      if (response.ok) {
        const data = await response.json();
        results[endpoint].data = data;
        console.log(`✅ ${endpoint}: ${response.status} - Success`);
      } else {
        const errorText = await response.text();
        results[endpoint].error = errorText;
        console.log(`❌ ${endpoint}: ${response.status} - ${errorText}`);
      }
    } catch (error: any) {
      results[endpoint] = {
        error: error.message,
        networkError: true,
      };
      console.log(`❌ ${endpoint}: Network error - ${error.message}`);
    }
  }
  
  console.log('\n📊 All announcements endpoints test results:', results);
  return results;
};

export const testDebugEndpoint = async () => {
  console.log('🔍 Testing /api/auth/debug endpoint...');
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/debug', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json',
      },
    });
    
    console.log('📊 Response status:', response.status);
    console.log('📊 Response headers:', Object.fromEntries(response.headers.entries()));
    
    if (response.ok) {
      const data = await response.json();
      console.log('✅ Debug endpoint success! Response data:', data);
      return { success: true, data };
    } else {
      const errorText = await response.text();
      console.error('❌ Debug endpoint error response:', errorText);
      return { success: false, status: response.status, error: errorText };
    }
  } catch (error: any) {
    console.error('❌ Debug endpoint network error:', error);
    return { success: false, error: error.message };
  }
};

// Make functions available globally for debugging
if (typeof window !== 'undefined') {
  (window as any).debugAuthState = debugAuthState;
  (window as any).testAllEndpoints = testAllEndpoints;
  (window as any).testAnnouncementsEndpoint = testAnnouncementsEndpoint;
  (window as any).testAllAnnouncementsEndpoints = testAllAnnouncementsEndpoints;
  (window as any).testDebugEndpoint = testDebugEndpoint;
  
  console.log('🔧 Debug functions available globally:');
  console.log('  - debugAuthState()');
  console.log('  - testAllEndpoints()');
  console.log('  - testAnnouncementsEndpoint()');
  console.log('  - testAllAnnouncementsEndpoints()');
  console.log('  - testDebugEndpoint()');
} 