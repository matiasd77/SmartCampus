import { useState, useEffect } from 'react';
import { profileAPI } from '../services/api';
import type { UserProfile, ProfileUpdateRequest, PasswordChangeRequest } from '../types/auth';

export const useProfile = () => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [isUploadingAvatar, setIsUploadingAvatar] = useState(false);

  const fetchProfile = async () => {
    setIsLoading(true);
    setError(null);

    try {
      console.log('useProfile - Fetching user profile...');
      console.log('useProfile - API Base URL:', 'http://localhost:8080/api');
      console.log('useProfile - Endpoint: /users/me');
      
      // Check if we have a token
      const token = localStorage.getItem('token');
      console.log('useProfile - Token available:', !!token);
      
      // Check token validity before making request
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const currentTime = Date.now() / 1000;
          if (payload.exp < currentTime) {
            console.warn('useProfile - Token is expired, clearing auth data');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            setError('Session expired. Please log in again.');
            window.location.href = '/login';
            return;
          }
        } catch (error) {
          console.error('useProfile - Error parsing token:', error);
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          setError('Invalid session. Please log in again.');
          window.location.href = '/login';
          return;
        }
      }
      
      const data = await profileAPI.getProfile();
      console.log('useProfile - Profile data received:', data);
      
      // Validate the profile data structure
      if (!data) {
        throw new Error('No profile data received from server');
      }
      
      if (!data.id || !data.email || !data.name) {
        console.warn('useProfile - Profile data missing required fields:', data);
        throw new Error('Invalid profile data structure received from server');
      }
      
      setProfile(data);
      setError(null); // Clear any previous errors
    } catch (err: any) {
      console.error('useProfile - Error fetching profile:', err);
      console.error('useProfile - Error details:', {
        message: err.message,
        status: err.response?.status,
        statusText: err.response?.statusText,
        data: err.response?.data,
        isNetworkError: !err.response,
        isTimeout: err.code === 'ECONNABORTED',
        url: err.config?.url,
        method: err.config?.method,
      });
      
      // Enhanced error handling with specific messages
      let errorMessage = 'Failed to load profile';
      
      if (!err.response) {
        // Network error or timeout
        if (err.code === 'ECONNABORTED') {
          errorMessage = 'Request timeout - server is not responding. Please check if the backend is running on http://localhost:8080.';
        } else if (err.message.includes('Network Error')) {
          errorMessage = 'Network error - unable to connect to server. Please check your internet connection and ensure the backend is running.';
        } else if (err.message.includes('ERR_NETWORK')) {
          errorMessage = 'Network connection failed. Please check your internet connection and ensure the backend server is running.';
        } else {
          errorMessage = 'Connection error - please check your internet connection and ensure the backend server is running.';
        }
      } else if (err.response.status === 401) {
        errorMessage = 'Authentication required. Please log in again.';
        // Redirect to login after a short delay
        setTimeout(() => {
          window.location.href = '/login';
        }, 2000);
      } else if (err.response.status === 403) {
        errorMessage = 'Access denied. You do not have permission to view this profile.';
      } else if (err.response.status === 404) {
        errorMessage = 'Profile not found. Please contact support.';
      } else if (err.response.status >= 500) {
        errorMessage = 'Server error. Please try again later.';
      } else if (err.response.data?.message) {
        errorMessage = err.response.data.message;
      }
      
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const updateProfile = async (profileData: ProfileUpdateRequest) => {
    setIsUpdating(true);
    setError(null);

    try {
      console.log('useProfile - Updating profile with data:', profileData);
      const updatedProfile = await profileAPI.updateProfile(profileData);
      console.log('useProfile - Profile updated successfully:', updatedProfile);
      setProfile(updatedProfile);
      return updatedProfile;
    } catch (err: any) {
      console.error('useProfile - Error updating profile:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update profile';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsUpdating(false);
    }
  };

  const changePassword = async (passwordData: PasswordChangeRequest) => {
    setIsChangingPassword(true);
    setError(null);

    try {
      console.log('useProfile - Changing password...');
      await profileAPI.changePassword(passwordData);
      console.log('useProfile - Password changed successfully');
    } catch (err: any) {
      console.error('useProfile - Error changing password:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to change password';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsChangingPassword(false);
    }
  };

  const uploadAvatar = async (file: File) => {
    setIsUploadingAvatar(true);
    setError(null);

    try {
      console.log('useProfile - Uploading avatar...');
      const updatedProfile = await profileAPI.uploadAvatar(file);
      console.log('useProfile - Avatar uploaded successfully:', updatedProfile);
      setProfile(updatedProfile);
      return updatedProfile;
    } catch (err: any) {
      console.error('useProfile - Error uploading avatar:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to upload avatar';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsUploadingAvatar(false);
    }
  };

  const deleteAvatar = async () => {
    setIsUploadingAvatar(true);
    setError(null);

    try {
      console.log('useProfile - Deleting avatar...');
      const updatedProfile = await profileAPI.deleteAvatar();
      console.log('useProfile - Avatar deleted successfully:', updatedProfile);
      setProfile(updatedProfile);
      return updatedProfile;
    } catch (err: any) {
      console.error('useProfile - Error deleting avatar:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to delete avatar';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setIsUploadingAvatar(false);
    }
  };

  const refresh = () => {
    fetchProfile();
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  return {
    profile,
    isLoading,
    error,
    isUpdating,
    isChangingPassword,
    isUploadingAvatar,
    updateProfile,
    changePassword,
    uploadAvatar,
    deleteAvatar,
    refresh,
  };
}; 