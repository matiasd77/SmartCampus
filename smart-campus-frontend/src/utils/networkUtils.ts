// Network utilities for health checks and diagnostics

export interface NetworkStatus {
  isOnline: boolean;
  backendReachable: boolean;
  latency: number | null;
  lastChecked: Date;
}

export interface BackendHealth {
  status: 'healthy' | 'unhealthy' | 'unknown';
  responseTime: number;
  error?: string;
}

/**
 * Check if the backend server is reachable
 */
export const checkBackendHealth = async (baseURL: string = 'http://localhost:8080'): Promise<BackendHealth> => {
  const startTime = Date.now();
  
  try {
    // Try to reach the backend health endpoint or root
    const response = await fetch(`${baseURL}/actuator/health`, {
      method: 'GET',
      mode: 'cors',
      headers: {
        'Accept': 'application/json',
      },
      signal: AbortSignal.timeout(5000), // 5 second timeout
    });
    
    const responseTime = Date.now() - startTime;
    
    if (response.ok) {
      return {
        status: 'healthy',
        responseTime,
      };
    } else {
      return {
        status: 'unhealthy',
        responseTime,
        error: `HTTP ${response.status}: ${response.statusText}`,
      };
    }
  } catch (error: any) {
    const responseTime = Date.now() - startTime;
    
    if (error.name === 'AbortError') {
      return {
        status: 'unhealthy',
        responseTime,
        error: 'Request timeout - backend not responding',
      };
    }
    
    return {
      status: 'unhealthy',
      responseTime,
      error: error.message || 'Network error',
    };
  }
};

/**
 * Test network connectivity to the backend
 */
export const testBackendConnectivity = async (): Promise<NetworkStatus> => {
  const isOnline = navigator.onLine;
  const startTime = Date.now();
  
  try {
    const health = await checkBackendHealth();
    
    return {
      isOnline,
      backendReachable: health.status === 'healthy',
      latency: health.responseTime,
      lastChecked: new Date(),
    };
  } catch (error) {
    return {
      isOnline,
      backendReachable: false,
      latency: null,
      lastChecked: new Date(),
    };
  }
};

/**
 * Get detailed network diagnostics
 */
export const getNetworkDiagnostics = async () => {
  const diagnostics = {
    timestamp: new Date().toISOString(),
    userAgent: navigator.userAgent,
    online: navigator.onLine,
    connection: (navigator as any).connection ? {
      effectiveType: (navigator as any).connection.effectiveType,
      downlink: (navigator as any).connection.downlink,
      rtt: (navigator as any).connection.rtt,
    } : null,
    backendHealth: await checkBackendHealth(),
    localStorage: {
      hasToken: !!localStorage.getItem('token'),
      hasUser: !!localStorage.getItem('user'),
    },
  };
  
  console.log('Network Diagnostics:', diagnostics);
  return diagnostics;
};

/**
 * Retry function with exponential backoff
 */
export const retryWithBackoff = async <T>(
  fn: () => Promise<T>,
  maxRetries: number = 3,
  baseDelay: number = 1000
): Promise<T> => {
  let lastError: Error;
  
  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      return await fn();
    } catch (error: any) {
      lastError = error;
      
      if (attempt === maxRetries) {
        throw error;
      }
      
      const delay = baseDelay * Math.pow(2, attempt);
      console.log(`Retry attempt ${attempt + 1}/${maxRetries + 1} in ${delay}ms...`);
      
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }
  
  throw lastError!;
}; 