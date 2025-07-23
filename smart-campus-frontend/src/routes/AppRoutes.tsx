import { Routes, Route, Navigate } from 'react-router-dom';
import { PrivateRoute } from '../components/PrivateRoute';
import Home from '../pages/Home';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Dashboard from '../pages/Dashboard';
import Profile from '../pages/Profile';
import Courses from '../pages/Courses';
import Grades from '../pages/Grades';
import Announcements from '../pages/Announcements';
import Notifications from '../pages/Notifications';
import Students from '../pages/Students';
import Professors from '../pages/Professors';

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      {/* Protected Routes */}
      <Route 
        path="/dashboard" 
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/profile" 
        element={
          <PrivateRoute>
            <Profile />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/courses" 
        element={
          <PrivateRoute>
            <Courses />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/grades" 
        element={
          <PrivateRoute>
            <Grades />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/announcements" 
        element={
          <PrivateRoute>
            <Announcements />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/notifications" 
        element={
          <PrivateRoute>
            <Notifications />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/students" 
        element={
          <PrivateRoute requiredRole="ADMIN">
            <Students />
          </PrivateRoute>
        } 
      />
      <Route 
        path="/professors" 
        element={
          <PrivateRoute requiredRole="ADMIN">
            <Professors />
          </PrivateRoute>
        } 
      />
      
      {/* Catch all route - redirect to home */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes;
