import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useDashboardData } from '../hooks/useDashboardData';
import { SummaryCard } from '../components/SummaryCard';
import { NetworkStatus } from '../components/NetworkStatus';
import { ActivityFeed } from '../components/ActivityFeed';
import { UsersTable } from '../components/UsersTable';

import { Navbar } from '../components/Navbar';
import { getUserDisplayName } from '../utils/userUtils';
import { hasAnyRole } from '../utils/authUtils';
import { 
  Users, 
  GraduationCap, 
  Bell, 
  Megaphone, 
  BookOpen, 
  Award,
  TrendingUp,
  Sparkles,
  Calendar,
  Clock,
  BarChart3,
  ArrowRight,
  Star,
  Activity,
  Zap,
  Shield
} from 'lucide-react';

export default function Dashboard() {
  const { user } = useAuth();
  const { stats, recentActivity, errors, isLoading, overallError, refetch } = useDashboardData();

  const getRoleDisplayName = (role: string) => {
    const roleConfigs = {
      'STUDENT': 'Student',
      'PROFESSOR': 'Professor', 
      'ADMIN': 'Administrator'
    };
    return roleConfigs[role as keyof typeof roleConfigs] || role;
  };

  const getCurrentTime = () => {
    const now = new Date();
    const hours = now.getHours();
    if (hours < 12) return 'Good morning';
    if (hours < 17) return 'Good afternoon';
    return 'Good evening';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
      <Navbar />
      
      <div className="container-width px-4 sm:px-6 lg:px-8 py-8">
        {/* Network Status */}
        <NetworkStatus 
          error={overallError} 
          onRetry={refetch} 
          isLoading={isLoading} 
        />
        
        {/* Welcome Section */}
        <div className="mb-10">
          <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-white via-white to-indigo-50/30 shadow-2xl border border-indigo-100/50">
            {/* Background decorations */}
            <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-indigo-100/40 to-violet-100/40 rounded-full -translate-y-48 translate-x-48 blur-3xl"></div>
            <div className="absolute bottom-0 left-0 w-80 h-80 bg-gradient-to-tr from-emerald-100/40 to-indigo-100/40 rounded-full translate-y-40 -translate-x-40 blur-3xl"></div>
            <div className="absolute top-1/2 left-1/2 w-64 h-64 bg-gradient-to-br from-violet-100/30 to-purple-100/30 rounded-full -translate-x-32 -translate-y-32 blur-2xl"></div>
            
            <div className="relative z-10 p-8 lg:p-12">
              <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                <div className="flex-1">
                  <div className="flex items-center mb-8">
                    <div className="relative">
                      <div className="w-16 h-16 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-2xl flex items-center justify-center shadow-xl mr-6">
                        <Sparkles className="w-8 h-8 text-white" />
                      </div>
                      <div className="absolute -top-2 -right-2 w-6 h-6 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center">
                        <Star className="w-3 h-3 text-white" />
                      </div>
                    </div>
                    <div>
                      <h1 className="text-4xl lg:text-5xl font-bold bg-gradient-to-r from-gray-900 via-indigo-800 to-violet-800 bg-clip-text text-transparent mb-3">
                        {getCurrentTime()}, {getUserDisplayName(user)}! 👋
                      </h1>
                      <p className="text-xl text-gray-600 max-w-2xl">
                        Welcome to your {getRoleDisplayName(user?.role || '')} dashboard. Here's what's happening today.
                      </p>
                    </div>
                  </div>
                  
                  {/* Enhanced stats row */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mt-10">
                    <div className="group relative overflow-hidden bg-gradient-to-br from-indigo-50 to-violet-50 rounded-2xl border border-indigo-200/50 p-6 hover:shadow-xl transition-all duration-300 hover:scale-105">
                      <div className="absolute inset-0 bg-gradient-to-br from-indigo-100/20 to-violet-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative z-10 text-center">
                        <div className="text-4xl font-bold text-indigo-700 mb-2">{new Date().getDate()}</div>
                        <div className="text-lg font-semibold text-indigo-600 mb-1">Today</div>
                        <div className="text-sm text-indigo-500 font-medium">Date</div>
                      </div>
                    </div>
                    <div className="group relative overflow-hidden bg-gradient-to-br from-emerald-50 to-green-50 rounded-2xl border border-emerald-200/50 p-6 hover:shadow-xl transition-all duration-300 hover:scale-105">
                      <div className="absolute inset-0 bg-gradient-to-br from-emerald-100/20 to-green-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative z-10 text-center">
                        <div className="text-2xl font-bold text-emerald-700 mb-2 leading-tight">
                          {new Date().toLocaleDateString('en-US', { month: 'long' })}
                        </div>
                        <div className="text-lg font-semibold text-emerald-600 mb-1">Month</div>
                        <div className="text-sm text-emerald-500 font-medium">Current</div>
                      </div>
                    </div>
                    <div className="group relative overflow-hidden bg-gradient-to-br from-violet-50 to-purple-50 rounded-2xl border border-violet-200/50 p-6 hover:shadow-xl transition-all duration-300 hover:scale-105">
                      <div className="absolute inset-0 bg-gradient-to-br from-violet-100/20 to-purple-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative z-10 text-center">
                        <div className="text-3xl font-bold text-violet-700 mb-2">{new Date().getFullYear()}</div>
                        <div className="text-lg font-semibold text-violet-600 mb-1">Year</div>
                        <div className="text-sm text-violet-500 font-medium">Current</div>
                      </div>
                    </div>
                    <div className="group relative overflow-hidden bg-gradient-to-br from-amber-50 to-orange-50 rounded-2xl border border-amber-200/50 p-6 hover:shadow-xl transition-all duration-300 hover:scale-105">
                      <div className="absolute inset-0 bg-gradient-to-br from-amber-100/20 to-orange-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative z-10 text-center">
                        <div className="text-2xl font-bold text-amber-700 mb-2 leading-tight">
                          {new Date().toLocaleDateString('en-US', { weekday: 'long' })}
                        </div>
                        <div className="text-lg font-semibold text-amber-600 mb-1">Day</div>
                        <div className="text-sm text-amber-500 font-medium">Week</div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div className="mt-8 lg:mt-0 lg:ml-12">
                  <button
                    onClick={refetch}
                    disabled={isLoading}
                    className="group relative overflow-hidden bg-gradient-to-r from-indigo-500 to-violet-600 text-white font-semibold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100 px-8 py-4 flex items-center"
                  >
                    <div className="absolute inset-0 bg-gradient-to-r from-indigo-600 to-violet-700 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                    <div className="relative flex items-center">
                      <TrendingUp className="w-5 h-5 mr-3" />
                      {isLoading ? 'Refreshing...' : 'Refresh Data'}
                    </div>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Enhanced Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          {/* Admin-only cards */}
          {user?.role === 'ADMIN' && (
            <>
              <SummaryCard
                title="Total Students"
                value={stats.studentsCount ?? 0}
                icon={<Users className="w-6 h-6" />}
                color="blue"
                isLoading={isLoading}
                error={errors.studentsCount}
              />
              
              <SummaryCard
                title="Total Professors"
                value={stats.professorsCount ?? 0}
                icon={<GraduationCap className="w-6 h-6" />}
                color="green"
                isLoading={isLoading}
                error={errors.professorsCount}
              />
            </>
          )}
          
          {/* Professor-only cards */}
          {user?.role === 'PROFESSOR' && (
            <SummaryCard
              title="My Students"
              value="View"
              icon={<Users className="w-6 h-6" />}
              color="blue"
              isLoading={false}
              error={undefined}
              isLink={true}
              linkTo="/students"
            />
          )}
          
          {/* Cards for all users */}
          <SummaryCard
            title="Announcements"
            value={stats.announcementsCount ?? 0}
            icon={<Megaphone className="w-6 h-6" />}
            color="yellow"
            isLoading={isLoading}
            error={errors.announcementsCount}
          />
          
          <SummaryCard
            title="Notifications"
            value={stats.notificationsCount ?? 0}
            icon={<Bell className="w-6 h-6" />}
            color="purple"
            isLoading={isLoading}
            error={errors.notificationsCount}
          />
          
          {/* Student-specific cards */}
          {user?.role === 'STUDENT' && (
            <>
              <SummaryCard
                title="My Courses"
                value="View"
                icon={<BookOpen className="w-6 h-6" />}
                color="indigo"
                isLoading={false}
                error={undefined}
                isLink={true}
                linkTo="/courses"
              />
              
              <SummaryCard
                title="My Grades"
                value="View"
                icon={<Award className="w-6 h-6" />}
                color="purple"
                isLoading={false}
                error={undefined}
                isLink={true}
                linkTo="/grades"
              />
            </>
          )}
          
          {/* Professor-specific cards */}
          {user?.role === 'PROFESSOR' && (
            <>
              <SummaryCard
                title="My Courses"
                value="View"
                icon={<BookOpen className="w-6 h-6" />}
                color="indigo"
                isLoading={false}
                error={undefined}
                isLink={true}
                linkTo="/courses"
              />
              
              <SummaryCard
                title="Grade Management"
                value="View"
                icon={<Award className="w-6 h-6" />}
                color="purple"
                isLoading={false}
                error={undefined}
                isLink={true}
                linkTo="/grades"
              />
            </>
          )}
        </div>

        {/* Enhanced Quick Actions & Recent Activity Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 mb-12">
          {/* Enhanced Quick Actions */}
          <div className="lg:col-span-1">
            <div className="relative overflow-hidden rounded-3xl bg-white shadow-2xl border border-gray-100">
              {/* Header with gradient */}
              <div className="relative overflow-hidden px-8 py-6 bg-gradient-to-r from-emerald-500 to-emerald-600">
                <div className="absolute inset-0 bg-gradient-to-r from-emerald-600 to-emerald-700 opacity-0 hover:opacity-100 transition-opacity duration-300"></div>
                <div className="relative flex items-center">
                  <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg mr-4">
                    <BarChart3 className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="text-2xl font-bold text-white">Quick Actions</h3>
                </div>
              </div>
              
              <div className="p-8">
                <div className="space-y-6">
                  <Link to="/announcements" className="group block">
                    <div className="relative overflow-hidden bg-gradient-to-r from-indigo-50 to-violet-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-indigo-200/50">
                      <div className="absolute inset-0 bg-gradient-to-r from-indigo-100/20 to-violet-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative flex items-center justify-between">
                        <div className="flex items-center">
                          <div className="w-14 h-14 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                            <Megaphone className="w-7 h-7 text-white" />
                          </div>
                          <div className="ml-5">
                            <h4 className="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition-colors duration-300">
                              Announcements
                            </h4>
                            <p className="text-sm text-gray-600">View and manage announcements</p>
                          </div>
                        </div>
                        <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-indigo-500 group-hover:translate-x-1 transition-all duration-300" />
                      </div>
                    </div>
                  </Link>
                  
                  <Link to="/notifications" className="group block">
                    <div className="relative overflow-hidden bg-gradient-to-r from-violet-50 to-purple-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-violet-200/50">
                      <div className="absolute inset-0 bg-gradient-to-r from-violet-100/20 to-purple-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative flex items-center justify-between">
                        <div className="flex items-center">
                          <div className="w-14 h-14 bg-gradient-to-br from-violet-500 to-purple-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                            <Bell className="w-7 h-7 text-white" />
                          </div>
                          <div className="ml-5">
                            <h4 className="text-lg font-bold text-gray-900 group-hover:text-violet-600 transition-colors duration-300">
                              Notifications
                            </h4>
                            <p className="text-sm text-gray-600">Check your notifications</p>
                          </div>
                        </div>
                        <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-violet-500 group-hover:translate-x-1 transition-all duration-300" />
                      </div>
                    </div>
                  </Link>
                  
                  <Link to="/profile" className="group block">
                    <div className="relative overflow-hidden bg-gradient-to-r from-emerald-50 to-green-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-emerald-200/50">
                      <div className="absolute inset-0 bg-gradient-to-r from-emerald-100/20 to-green-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      <div className="relative flex items-center justify-between">
                        <div className="flex items-center">
                          <div className="w-14 h-14 bg-gradient-to-br from-emerald-500 to-green-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                            <Users className="w-7 h-7 text-white" />
                          </div>
                          <div className="ml-5">
                            <h4 className="text-lg font-bold text-gray-900 group-hover:text-emerald-600 transition-colors duration-300">
                              Profile
                            </h4>
                            <p className="text-sm text-gray-600">Manage your profile</p>
                          </div>
                        </div>
                        <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-emerald-500 group-hover:translate-x-1 transition-all duration-300" />
                      </div>
                    </div>
                  </Link>
                  
                  {user?.role === 'ADMIN' && (
                    <>
                      <Link to="/students" className="group block">
                        <div className="relative overflow-hidden bg-gradient-to-r from-indigo-50 to-violet-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-indigo-200/50">
                          <div className="absolute inset-0 bg-gradient-to-r from-indigo-100/20 to-violet-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                          <div className="relative flex items-center justify-between">
                            <div className="flex items-center">
                              <div className="w-14 h-14 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                                <Users className="w-7 h-7 text-white" />
                              </div>
                              <div className="ml-5">
                                <h4 className="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition-colors duration-300">
                                  Students
                                </h4>
                                <p className="text-sm text-gray-600">Manage students</p>
                              </div>
                            </div>
                            <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-indigo-500 group-hover:translate-x-1 transition-all duration-300" />
                          </div>
                        </div>
                      </Link>
                      
                      <Link to="/professors" className="group block">
                        <div className="relative overflow-hidden bg-gradient-to-r from-emerald-50 to-green-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-emerald-200/50">
                          <div className="absolute inset-0 bg-gradient-to-r from-emerald-100/20 to-green-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                          <div className="relative flex items-center justify-between">
                            <div className="flex items-center">
                              <div className="w-14 h-14 bg-gradient-to-br from-emerald-500 to-green-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                                <GraduationCap className="w-7 h-7 text-white" />
                              </div>
                              <div className="ml-5">
                                <h4 className="text-lg font-bold text-gray-900 group-hover:text-emerald-600 transition-colors duration-300">
                                  Professors
                                </h4>
                                <p className="text-sm text-gray-600">Manage professors</p>
                              </div>
                            </div>
                            <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-emerald-500 group-hover:translate-x-1 transition-all duration-300" />
                          </div>
                        </div>
                      </Link>
                    </>
                  )}
                  
                  {/* Professor-specific actions */}
                  {user?.role === 'PROFESSOR' && (
                    <Link to="/students" className="group block">
                      <div className="relative overflow-hidden bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 hover:shadow-xl transition-all duration-300 group-hover:scale-[1.02] border border-blue-200/50">
                        <div className="absolute inset-0 bg-gradient-to-r from-blue-100/20 to-indigo-100/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                        <div className="relative flex items-center justify-between">
                          <div className="flex items-center">
                            <div className="w-14 h-14 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                              <Users className="w-7 h-7 text-white" />
                            </div>
                            <div className="ml-5">
                              <h4 className="text-lg font-bold text-gray-900 group-hover:text-blue-600 transition-colors duration-300">
                                My Students
                              </h4>
                              <p className="text-sm text-gray-600">View your students</p>
                            </div>
                          </div>
                          <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-blue-500 group-hover:translate-x-1 transition-all duration-300" />
                        </div>
                      </div>
                    </Link>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Enhanced Recent Activity */}
          <div className="lg:col-span-2">
            <div className="relative overflow-hidden rounded-3xl bg-white shadow-2xl border border-gray-100">
              <div className="relative overflow-hidden px-8 py-6 bg-gradient-to-r from-violet-500 to-purple-600">
                <div className="absolute inset-0 bg-gradient-to-r from-violet-600 to-purple-700 opacity-0 hover:opacity-100 transition-opacity duration-300"></div>
                <div className="relative flex items-center justify-between">
                  <div className="flex items-center">
                    <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg mr-4">
                      <Activity className="w-6 h-6 text-white" />
                    </div>
                    <h3 className="text-2xl font-bold text-white">Recent Activity</h3>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Zap className="w-5 h-5 text-white/80" />
                    <span className="text-white/80 text-sm font-medium">Live Updates</span>
                  </div>
                </div>
              </div>
              <div className="p-8">
                <ActivityFeed 
                  activities={recentActivity}
                  isLoading={isLoading}
                  error={errors.recentActivity}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Admin Users Table */}
        {user?.role === 'ADMIN' && (
          <div className="mb-12">
            <div className="relative overflow-hidden rounded-3xl bg-white shadow-2xl border border-gray-100">
              <div className="relative overflow-hidden px-8 py-6 bg-gradient-to-r from-blue-500 to-indigo-600">
                <div className="absolute inset-0 bg-gradient-to-r from-blue-600 to-indigo-700 opacity-0 hover:opacity-100 transition-opacity duration-300"></div>
                <div className="relative flex items-center justify-between">
                  <div className="flex items-center">
                    <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg mr-4">
                      <Users className="w-6 h-6 text-white" />
                    </div>
                    <h3 className="text-2xl font-bold text-white">All Users</h3>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Shield className="w-5 h-5 text-white/80" />
                    <span className="text-white/80 text-sm font-medium">Admin View</span>
                  </div>
                </div>
              </div>
              <div className="p-8">
                <UsersTable />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
