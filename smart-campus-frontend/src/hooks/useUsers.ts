import { useState, useEffect, useCallback, useRef } from 'react';
import { usersAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  bio?: string;
  department?: string;
  position?: string;
  studentId?: string;
  employeeId?: string;
  major?: string;
  minor?: string;
  year?: number;
  semester?: string;
  academicYear?: string;
  advisor?: string;
  researchArea?: string;
  researchInterests?: string;
  websiteUrl?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  twitterUrl?: string;
  facebookUrl?: string;
  instagramUrl?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
}

interface UseUsersReturn {
  users: User[];
  isLoading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  updateUser: (id: number, userData: Partial<User>) => Promise<void>;
  deleteUser: (id: number) => Promise<void>;
}

export const useUsers = (): UseUsersReturn => {
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user: currentUser } = useAuth();
  const abortControllerRef = useRef<AbortController | null>(null);

  const fetchUsers = useCallback(async () => {
    // Only allow admin users to fetch all users
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Access denied. Admin privileges required.');
      return;
    }

    // Cancel any ongoing request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // Create new abort controller for this request
    abortControllerRef.current = new AbortController();

    setIsLoading(true);
    setError(null);

    try {
      console.log('useUsers: Fetching users...');
      const response = await usersAPI.getAllUsers();
      
      if (response.success && Array.isArray(response.data)) {
        console.log('useUsers: Successfully fetched', response.data.length, 'users');
        setUsers(response.data);
      } else {
        console.error('useUsers: Invalid response format:', response);
        setError('Invalid response format from server');
        setUsers([]);
      }
    } catch (err: any) {
      console.error('useUsers: Error fetching users:', err);
      
      if (err.name === 'AbortError') {
        console.log('useUsers: Request was aborted');
        return;
      }

      if (err.response?.status === 403) {
        setError('Access denied. Admin privileges required.');
      } else if (err.response?.status === 401) {
        setError('Authentication required. Please log in again.');
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.message) {
        setError(err.message);
      } else {
        setError('Failed to load users. Please check your internet connection and try again.');
      }
      
      setUsers([]);
    } finally {
      setIsLoading(false);
    }
  }, [currentUser]);

  const updateUser = useCallback(async (id: number, userData: Partial<User>) => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      throw new Error('Access denied. Admin privileges required.');
    }

    try {
      console.log('useUsers: Updating user', id, 'with data:', userData);
      const response = await usersAPI.updateUser(id, userData);
      
      if (response.success) {
        console.log('useUsers: User updated successfully');
        // Refresh the users list
        await fetchUsers();
      } else {
        throw new Error(response.message || 'Failed to update user');
      }
    } catch (err: any) {
      console.error('useUsers: Error updating user:', err);
      throw err;
    }
  }, [currentUser, fetchUsers]);

  const deleteUser = useCallback(async (id: number) => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      throw new Error('Access denied. Admin privileges required.');
    }

    try {
      console.log('useUsers: Deleting user', id);
      const response = await usersAPI.deleteUser(id);
      
      if (response.success) {
        console.log('useUsers: User deleted successfully');
        // Remove the user from the local state
        setUsers(prevUsers => prevUsers.filter(user => user.id !== id));
      } else {
        throw new Error(response.message || 'Failed to delete user');
      }
    } catch (err: any) {
      console.error('useUsers: Error deleting user:', err);
      throw err;
    }
  }, [currentUser]);

  // Initial fetch
  useEffect(() => {
    fetchUsers();

    // Cleanup function to abort ongoing requests
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [fetchUsers]);

  return {
    users,
    isLoading,
    error,
    refetch: fetchUsers,
    updateUser,
    deleteUser,
  };
}; 