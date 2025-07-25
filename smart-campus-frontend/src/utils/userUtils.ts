import type { User } from '../types/auth';

/**
 * Get the display name for a user, with fallbacks in order of preference:
 * 1. Full name (name property)
 * 2. First + Last name combination
 * 3. Username
 * 4. Default fallback
 */
export const getUserDisplayName = (user: User | null, fallback: string = 'User'): string => {
  if (!user) return fallback;
  
  // First try the name property
  if (user.name) return user.name;
  
  // Then try firstName + lastName combination
  if (user.firstName && user.lastName) {
    return `${user.firstName} ${user.lastName}`;
  }
  
  // Then try just firstName
  if (user.firstName) return user.firstName;
  
  // Then try username
  if (user.username) return user.username;
  
  // Finally fallback
  return fallback;
};

/**
 * Get the user's first name or display name for informal greetings
 */
export const getUserFirstName = (user: User | null, fallback: string = 'User'): string => {
  if (!user) return fallback;
  
  // Try firstName first
  if (user.firstName) return user.firstName;
  
  // Then try extracting first name from full name
  if (user.name) {
    const firstName = user.name.split(' ')[0];
    if (firstName) return firstName;
  }
  
  // Then try username
  if (user.username) return user.username;
  
  return fallback;
};

/**
 * Get a compact display name for the navbar (first name only or shortened version)
 */
export const getCompactDisplayName = (user: User | null, fallback: string = 'User'): string => {
  if (!user) return fallback;
  
  // For "System Administrator", show just "Admin"
  if (user.name === 'System Administrator' || user.role === 'ADMIN') {
    return 'Admin';
  }
  
  // Try firstName first
  if (user.firstName) return user.firstName;
  
  // Then try extracting first name from full name
  if (user.name) {
    const firstName = user.name.split(' ')[0];
    if (firstName) return firstName;
  }
  
  // Then try username (but limit length more aggressively)
  if (user.username) {
    return user.username.length > 10 ? user.username.substring(0, 10) + '...' : user.username;
  }
  
  return fallback;
}; 