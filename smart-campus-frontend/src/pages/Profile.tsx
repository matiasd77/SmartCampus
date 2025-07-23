import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useProfile } from '../hooks/useProfile';
import { ProfileForm } from '../components/ProfileForm';
import { PasswordChangeForm } from '../components/PasswordChangeForm';
import { AvatarUpload } from '../components/AvatarUpload';
import { NetworkStatus } from '../components/NetworkStatus';
import { Navbar } from '../components/Navbar';
import { getNetworkDiagnostics } from '../utils/networkUtils';
import { debugAuthState, testAllEndpoints, testAnnouncementsEndpoint, testAllAnnouncementsEndpoints, testDebugEndpoint } from '../utils/tokenDebugger';
import { 
  User, 
  RefreshCw, 
  Shield, 
  Calendar,
  Mail,
  Phone,
  MapPin,
  GraduationCap,
  Settings,
  CheckCircle,
  Clock,
  AlertTriangle,
  WifiOff
} from 'lucide-react';

export default function Profile() {
  const { user: authUser } = useAuth();
  const {
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
  } = useProfile();

  // Use auth user as fallback if profile API fails
  const displayProfile = profile || (authUser ? {
    id: authUser.id,
    name: authUser.firstName && authUser.lastName 
      ? `${authUser.firstName} ${authUser.lastName}` 
      : authUser.username || 'User',
    email: authUser.email,
    role: authUser.role,
    isActive: true,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  } : null);

  const getRoleInfo = (role: string) => {
    const roleConfigs = {
      'STUDENT': { value: 'STUDENT', label: 'Student', color: 'text-blue-600', bgColor: 'bg-blue-100' },
      'PROFESSOR': { value: 'PROFESSOR', label: 'Professor', color: 'text-green-600', bgColor: 'bg-green-100' },
      'ADMIN': { value: 'ADMIN', label: 'Administrator', color: 'text-purple-600', bgColor: 'bg-purple-100' }
    };
    
    return roleConfigs[role as keyof typeof roleConfigs] || 
           { value: role, label: role, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  // Show loading state
  if (isLoading && !displayProfile) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
        <Navbar />
        
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="animate-pulse">
            <div className="h-8 bg-gray-200 rounded w-48 mb-6"></div>
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2 space-y-6">
                <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6">
                  <div className="h-6 bg-gray-200 rounded w-32 mb-4"></div>
                  <div className="space-y-4">
                    <div className="h-4 bg-gray-200 rounded w-full"></div>
                    <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                    <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                  </div>
                </div>
              </div>
              <div className="space-y-6">
                <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6">
                  <div className="h-6 bg-gray-200 rounded w-32 mb-4"></div>
                  <div className="h-4 bg-gray-200 rounded w-full"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show error state with fallback option
  if (error && !displayProfile) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
        <Navbar />
        
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Network Status */}
          <NetworkStatus 
            error={error} 
            onRetry={refresh} 
            isLoading={isLoading} 
          />
          
          <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-12 text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-red-100 to-pink-100 rounded-3xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <AlertTriangle className="w-12 h-12 text-red-500" />
            </div>
            <h3 className="text-3xl font-bold text-gray-900 mb-4">Profile Loading Error</h3>
            <p className="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">{error}</p>
            
            <div className="space-y-4">
              <button
                onClick={refresh}
                className="inline-flex items-center px-8 py-4 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-bold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105"
              >
                <RefreshCw className="w-5 h-5 mr-3" />
                Try Again
              </button>
              
              <button
                onClick={async () => {
                  console.log('Running network diagnostics...');
                  await getNetworkDiagnostics();
                }}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <Settings className="w-4 h-4 mr-2" />
                Network Diagnostics
              </button>
              
              <button
                onClick={() => {
                  console.log('Running authentication debug...');
                  debugAuthState();
                }}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <Shield className="w-4 h-4 mr-2" />
                Debug Auth
              </button>
              
              <button
                onClick={testAllEndpoints}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Test Endpoints
              </button>
              
              <button
                onClick={testAnnouncementsEndpoint}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Test Announcements Endpoint
              </button>
              
              <button
                onClick={testAllAnnouncementsEndpoints}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-purple-500 to-violet-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Test All Announcements
              </button>
              
              <button
                onClick={testDebugEndpoint}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-orange-500 to-red-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Test Debug Endpoint
              </button>
              
              {authUser && (
                <div className="mt-8 p-6 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl border border-blue-200">
                  <h4 className="text-lg font-semibold text-blue-800 mb-2">Quick Profile View</h4>
                  <p className="text-blue-600 mb-4">Showing basic profile from authentication data</p>
                  <div className="text-left space-y-2">
                    <p><strong>Name:</strong> {authUser.firstName && authUser.lastName ? `${authUser.firstName} ${authUser.lastName}` : authUser.username}</p>
                    <p><strong>Email:</strong> {authUser.email}</p>
                    <p><strong>Role:</strong> {authUser.role}</p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show fallback profile if no profile data but we have auth user
  if (!displayProfile && authUser) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
        <Navbar />
        
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-12 text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-amber-100 to-orange-100 rounded-3xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <WifiOff className="w-12 h-12 text-amber-500" />
            </div>
            <h3 className="text-3xl font-bold text-gray-900 mb-4">Limited Profile View</h3>
            <p className="text-lg text-gray-600 mb-8">
              Unable to load full profile from server. Showing basic information from authentication data.
            </p>
            
            <div className="bg-gradient-to-r from-amber-50 to-orange-50 rounded-2xl p-8 border border-amber-200 max-w-md mx-auto">
              <div className="w-20 h-20 bg-gradient-to-br from-amber-500 to-orange-600 rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg">
                <User className="w-10 h-10 text-white" />
              </div>
              <h4 className="text-xl font-bold text-gray-900 mb-2">
                {authUser.firstName && authUser.lastName ? `${authUser.firstName} ${authUser.lastName}` : authUser.username}
              </h4>
              <p className="text-gray-600 mb-3">{authUser.email}</p>
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold ${getRoleInfo(authUser.role).bgColor} ${getRoleInfo(authUser.role).color}`}>
                {getRoleInfo(authUser.role).label}
              </span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show no profile state
  if (!displayProfile) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
        <Navbar />
        
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-12 text-center">
            <div className="w-24 h-24 bg-gradient-to-br from-gray-100 to-slate-100 rounded-3xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <User className="w-12 h-12 text-gray-400" />
            </div>
            <h3 className="text-3xl font-bold text-gray-900 mb-4">Profile Not Found</h3>
            <p className="text-lg text-gray-600 mb-8">Unable to load your profile information. Please try logging in again.</p>
            <button
              onClick={() => window.location.href = '/login'}
              className="inline-flex items-center px-8 py-4 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-bold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105"
            >
              Go to Login
            </button>
          </div>
        </div>
      </div>
    );
  }

  const roleInfo = getRoleInfo(displayProfile.role);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
      <Navbar />
      
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Network Status */}
        <NetworkStatus 
          error={error} 
          onRetry={refresh} 
          isLoading={isLoading} 
        />
        
        {/* Header Section */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-8 mb-8 overflow-hidden relative">
          {/* Background decoration */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-blue-100/50 to-indigo-100/50 rounded-full -translate-y-32 translate-x-32"></div>
          <div className="absolute bottom-0 left-0 w-32 h-32 bg-gradient-to-tr from-purple-100/50 to-pink-100/50 rounded-full translate-y-16 -translate-x-16"></div>
          
          <div className="relative z-10">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
              <div className="flex-1">
                <div className="flex items-center mb-4">
                  <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl mr-6">
                    <User className="w-8 h-8 text-white" />
                  </div>
                  <div>
                    <h1 className="text-4xl lg:text-5xl font-bold bg-gradient-to-r from-gray-900 via-blue-800 to-indigo-800 bg-clip-text text-transparent mb-3">
                      Profile Settings
                    </h1>
                    <p className="text-xl text-gray-600 leading-relaxed">
                      Manage your account settings and personal information
                    </p>
                  </div>
                </div>
              </div>
              
              <div className="mt-6 lg:mt-0 lg:ml-8">
                <button
                  onClick={refresh}
                  disabled={isLoading}
                  className="group relative overflow-hidden bg-gradient-to-r from-gray-500 to-slate-600 text-white font-bold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100 px-6 py-3"
                >
                  <div className="absolute inset-0 bg-gradient-to-r from-gray-600 to-slate-700 opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
                  <div className="relative flex items-center">
                    <RefreshCw className={`w-5 h-5 mr-3 ${isLoading ? 'animate-spin' : ''}`} />
                    {isLoading ? 'Refreshing...' : 'Refresh'}
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Profile Form - Only show if we have full profile data */}
            {profile && (
              <ProfileForm
                profile={profile}
                onSubmit={updateProfile}
                isLoading={isUpdating}
              />
            )}

            {/* Password Change Form - Only show if we have full profile data */}
            {profile && (
              <PasswordChangeForm
                onSubmit={changePassword}
                isLoading={isChangingPassword}
              />
            )}
          </div>

          {/* Sidebar */}
          <div className="space-y-8">
            {/* Profile Card */}
            <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
              <div className="bg-gradient-to-r from-blue-500 to-indigo-600 p-6 text-center">
                <div className="relative inline-block">
                  <div className="w-24 h-24 rounded-full overflow-hidden bg-white border-4 border-white shadow-lg">
                    {displayProfile.avatar ? (
                      <img
                        src={displayProfile.avatar}
                        alt="Profile"
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-100 to-slate-100">
                        <User className="w-12 h-12 text-gray-400" />
                      </div>
                    )}
                  </div>
                  {displayProfile.isActive && (
                    <div className="absolute -bottom-1 -right-1 w-6 h-6 bg-green-500 rounded-full border-2 border-white flex items-center justify-center">
                      <CheckCircle className="w-3 h-3 text-white" />
                    </div>
                  )}
                </div>
                <h2 className="text-xl font-bold text-white mt-4">{displayProfile.name}</h2>
                <p className="text-blue-100">{displayProfile.email}</p>
                <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold mt-2 ${roleInfo.bgColor} ${roleInfo.color}`}>
                  {roleInfo.label}
                </span>
              </div>
              
              <div className="p-6 space-y-4">
                <div className="flex items-center">
                  <Mail className="w-4 h-4 text-gray-400 mr-3" />
                  <span className="text-sm text-gray-600">{displayProfile.email}</span>
                </div>
                
                {displayProfile.phoneNumber && (
                  <div className="flex items-center">
                    <Phone className="w-4 h-4 text-gray-400 mr-3" />
                    <span className="text-sm text-gray-600">{displayProfile.phoneNumber}</span>
                  </div>
                )}
                
                {(displayProfile.city || displayProfile.state || displayProfile.country) && (
                  <div className="flex items-center">
                    <MapPin className="w-4 h-4 text-gray-400 mr-3" />
                    <span className="text-sm text-gray-600">
                      {[displayProfile.city, displayProfile.state, displayProfile.country].filter(Boolean).join(', ')}
                    </span>
                  </div>
                )}
                
                <div className="flex items-center">
                  <Calendar className="w-4 h-4 text-gray-400 mr-3" />
                  <span className="text-sm text-gray-600">Member since {formatDate(displayProfile.createdAt)}</span>
                </div>
                
                {displayProfile.lastLoginAt && (
                  <div className="flex items-center">
                    <Clock className="w-4 h-4 text-gray-400 mr-3" />
                    <span className="text-sm text-gray-600">Last login {formatDate(displayProfile.lastLoginAt)}</span>
                  </div>
                )}
              </div>
            </div>

            {/* Avatar Upload - Only show if we have full profile data */}
            {profile && (
              <AvatarUpload
                currentAvatar={profile.avatar}
                onUpload={uploadAvatar}
                onDelete={deleteAvatar}
                isLoading={isUploadingAvatar}
              />
            )}

            {/* Account Information */}
            <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-200 bg-gradient-to-r from-gray-50 to-slate-50">
                <div className="flex items-center">
                  <Shield className="w-5 h-5 text-gray-600 mr-2" />
                  <h3 className="text-lg font-semibold text-gray-900">Account Information</h3>
                </div>
              </div>
              <div className="p-6 space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">Status</label>
                  <div className="mt-1">
                    {displayProfile.isActive ? (
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                        <CheckCircle className="w-3 h-3 mr-1" />
                        Active
                      </span>
                    ) : (
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-red-100 text-red-800">
                        Inactive
                      </span>
                    )}
                  </div>
                </div>
                
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">Role</label>
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${roleInfo.bgColor} ${roleInfo.color}`}>
                    {roleInfo.label}
                  </span>
                </div>
                
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">Member Since</label>
                  <p className="text-sm text-gray-600">{formatDate(displayProfile.createdAt)}</p>
                </div>
                
                {displayProfile.updatedAt && (
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">Last Updated</label>
                    <p className="text-sm text-gray-600">{formatDate(displayProfile.updatedAt)}</p>
                  </div>
                )}
              </div>
            </div>

            {/* Academic Information (if available) */}
            {(displayProfile.department || displayProfile.major || displayProfile.year) && (
              <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-200 bg-gradient-to-r from-gray-50 to-slate-50">
                  <div className="flex items-center">
                    <GraduationCap className="w-5 h-5 text-gray-600 mr-2" />
                    <h3 className="text-lg font-semibold text-gray-900">Academic Information</h3>
                  </div>
                </div>
                <div className="p-6 space-y-4">
                  {displayProfile.department && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Department</label>
                      <p className="text-sm text-gray-600">{displayProfile.department}</p>
                    </div>
                  )}
                  
                  {displayProfile.major && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Major</label>
                      <p className="text-sm text-gray-600">{displayProfile.major}</p>
                    </div>
                  )}
                  
                  {displayProfile.year && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Year</label>
                      <p className="text-sm text-gray-600">{displayProfile.year}</p>
                    </div>
                  )}
                  
                  {displayProfile.semester && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Semester</label>
                      <p className="text-sm text-gray-600">{displayProfile.semester}</p>
                    </div>
                  )}
                  
                  {displayProfile.academicYear && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Academic Year</label>
                      <p className="text-sm text-gray-600">{displayProfile.academicYear}</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Preferences (if available) */}
            {displayProfile.preferences && (
              <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-200 bg-gradient-to-r from-gray-50 to-slate-50">
                  <div className="flex items-center">
                    <Settings className="w-5 h-5 text-gray-600 mr-2" />
                    <h3 className="text-lg font-semibold text-gray-900">Preferences</h3>
                  </div>
                </div>
                <div className="p-6 space-y-4">
                  {displayProfile.preferences.theme && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Theme</label>
                      <p className="text-sm text-gray-600 capitalize">{displayProfile.preferences.theme}</p>
                    </div>
                  )}
                  
                  {displayProfile.preferences.language && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">Language</label>
                      <p className="text-sm text-gray-600">{displayProfile.preferences.language}</p>
                    </div>
                  )}
                  
                  {displayProfile.preferences.notifications && (
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">Notifications</label>
                      <div className="space-y-2">
                        {displayProfile.preferences.notifications.email !== undefined && (
                          <div className="flex items-center justify-between">
                            <span className="text-sm text-gray-600">Email</span>
                            <span className={`text-sm ${displayProfile.preferences.notifications.email ? 'text-green-600' : 'text-gray-400'}`}>
                              {displayProfile.preferences.notifications.email ? 'On' : 'Off'}
                            </span>
                          </div>
                        )}
                        {displayProfile.preferences.notifications.push !== undefined && (
                          <div className="flex items-center justify-between">
                            <span className="text-sm text-gray-600">Push</span>
                            <span className={`text-sm ${displayProfile.preferences.notifications.push ? 'text-green-600' : 'text-gray-400'}`}>
                              {displayProfile.preferences.notifications.push ? 'On' : 'Off'}
                            </span>
                          </div>
                        )}
                        {displayProfile.preferences.notifications.sms !== undefined && (
                          <div className="flex items-center justify-between">
                            <span className="text-sm text-gray-600">SMS</span>
                            <span className={`text-sm ${displayProfile.preferences.notifications.sms ? 'text-green-600' : 'text-gray-400'}`}>
                              {displayProfile.preferences.notifications.sms ? 'On' : 'Off'}
                            </span>
                          </div>
                        )}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
