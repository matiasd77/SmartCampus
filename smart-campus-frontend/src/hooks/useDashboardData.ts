import { useState, useEffect } from 'react';
import { dashboardAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import type { DashboardStats, RecentActivity, DashboardErrors } from '../types/dashboard';

export const useDashboardData = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({
    studentsCount: null,
    professorsCount: null,
    announcementsCount: null,
    notificationsCount: null,
  });
  const [recentActivity, setRecentActivity] = useState<RecentActivity[]>([]);
  const [errors, setErrors] = useState<DashboardErrors>({});
  const [isLoading, setIsLoading] = useState(true);
  const [overallError, setOverallError] = useState<string | null>(null);

  const { showError } = useToast();

  const fetchData = async () => {
    console.log('useDashboardData - Starting to fetch dashboard data...');
    console.log('useDashboardData - User role:', user?.role);
    setIsLoading(true);
    setErrors({});
    setOverallError(null);

    // Determine which API calls to make based on user role
    const isAdmin = user?.role === 'ADMIN';
    const isProfessor = user?.role === 'PROFESSOR';
    const isStudent = user?.role === 'STUDENT';

    try {
      // Base API calls that all users can access
      const baseCalls = [
        dashboardAPI.getAnnouncementsCount(),
        dashboardAPI.getNotificationsCount(),
        dashboardAPI.getRecentActivity(),
      ];

      // Admin-specific calls
      const adminCalls = isAdmin ? [
        dashboardAPI.getStudentsCount(),
        dashboardAPI.getProfessorsCount(),
      ] : [];

      // Professor-specific calls (if any)
      const professorCalls = isProfessor ? [
        // Add any professor-specific calls here if needed
      ] : [];

      // Student-specific calls (if any)
      const studentCalls = isStudent ? [
        // Add any student-specific calls here if needed
      ] : [];

      // Combine all calls based on user role
      const allCalls = [...baseCalls, ...adminCalls, ...professorCalls, ...studentCalls];
      
      console.log('useDashboardData - Making API calls:', allCalls.length, 'calls for role:', user?.role);

      const results = await Promise.allSettled(allCalls);

      const newStats: DashboardStats = {
        studentsCount: null,
        professorsCount: null,
        announcementsCount: null,
        notificationsCount: null,
      };

      const newErrors: DashboardErrors = {};

      // Process results based on user role
      let resultIndex = 0;

      // Process base calls (announcements, notifications, activity)
      if (results[resultIndex]?.status === 'fulfilled') {
        const result = results[resultIndex] as PromiseFulfilledResult<any>;
        console.log('useDashboardData - Announcements count loaded successfully:', result.value);
        newStats.announcementsCount = result.value.count;
      } else {
        const result = results[resultIndex] as PromiseRejectedResult;
        console.error('useDashboardData - Failed to load announcements count:', result.reason);
        newErrors.announcementsCount = 'Failed to load announcements count';
        showError('Failed to load announcements count');
      }
      resultIndex++;

      if (results[resultIndex]?.status === 'fulfilled') {
        const result = results[resultIndex] as PromiseFulfilledResult<any>;
        console.log('useDashboardData - Notifications count loaded successfully:', result.value);
        newStats.notificationsCount = result.value.count;
      } else {
        const result = results[resultIndex] as PromiseRejectedResult;
        console.error('useDashboardData - Failed to load notifications count:', result.reason);
        newErrors.notificationsCount = 'Failed to load notifications count';
        showError('Failed to load notifications count');
      }
      resultIndex++;

      if (results[resultIndex]?.status === 'fulfilled') {
        const result = results[resultIndex] as PromiseFulfilledResult<any>;
        console.log('useDashboardData - Recent activity loaded successfully:', result.value);
        setRecentActivity(result.value.activities || []);
      } else {
        const result = results[resultIndex] as PromiseRejectedResult;
        console.error('useDashboardData - Failed to load recent activity:', result.reason);
        setRecentActivity([]);
      }
      resultIndex++;

      // Process admin-specific calls
      if (isAdmin) {
        if (results[resultIndex]?.status === 'fulfilled') {
          const result = results[resultIndex] as PromiseFulfilledResult<any>;
          console.log('useDashboardData - Students count loaded successfully:', result.value);
          newStats.studentsCount = result.value.count;
        } else {
          const result = results[resultIndex] as PromiseRejectedResult;
          console.error('useDashboardData - Failed to load students count:', result.reason);
          newErrors.studentsCount = 'Failed to load students count';
          showError('Failed to load students count');
        }
        resultIndex++;

        if (results[resultIndex]?.status === 'fulfilled') {
          const result = results[resultIndex] as PromiseFulfilledResult<any>;
          console.log('useDashboardData - Professors count loaded successfully:', result.value);
          newStats.professorsCount = result.value.count;
        } else {
          const result = results[resultIndex] as PromiseRejectedResult;
          console.error('useDashboardData - Failed to load professors count:', result.reason);
          newErrors.professorsCount = 'Failed to load professors count';
          showError('Failed to load professors count');
        }
        resultIndex++;
      } else {
        // For non-admin users, set counts to 0 or null to indicate no access
        newStats.studentsCount = 0;
        newStats.professorsCount = 0;
      }

      setStats(newStats);
      setErrors(newErrors);

      // Check if there are any critical errors
      const hasCriticalErrors = Object.keys(newErrors).length > 0;
      if (hasCriticalErrors) {
        setOverallError('Some dashboard data could not be loaded');
      }

    } catch (error) {
      console.error('useDashboardData - Unexpected error:', error);
      setOverallError('Failed to load dashboard data');
      showError('Failed to load dashboard data');
    } finally {
      setIsLoading(false);
    }
  };

  const refetch = () => {
    fetchData();
  };

  useEffect(() => {
    if (user) {
      fetchData();
    }
  }, [user]);

  return {
    stats,
    recentActivity,
    errors,
    isLoading,
    overallError,
    refetch,
  };
}; 