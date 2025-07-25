import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { ToastProvider } from './contexts/ToastContext';
import { AuthProvider } from './contexts/AuthContext';
import AppRoutes from './routes/AppRoutes';
import ErrorHandlerSetup from './utils/ErrorHandlerSetup';
import { FullScreenLoader } from './components/FullScreenLoader';
import { useAuth } from './contexts/AuthContext';
import './App.css';

// Component to handle auth-dependent rendering
const AuthDependentApp: React.FC = () => {
  const { isLoading, isAuthenticated } = useAuth();

  // Show loading screen while auth is initializing
  if (isLoading) {
    return <FullScreenLoader message="Initializing..." />;
  }

  // If not authenticated, show the app (which will redirect to login via PrivateRoute)
  // If authenticated, show the app with protected routes
  return (
    <div className="App min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      <ErrorHandlerSetup />
      <AppRoutes />
    </div>
  );
};

function App() {
  // Debug authentication status on app load
  React.useEffect(() => {
    console.log('App: Application starting...');
    
    // Log localStorage contents for debugging
    console.log('App: localStorage contents:', {
      token: localStorage.getItem('token') ? 'Present' : 'Not found',
      user: localStorage.getItem('user') ? 'Present' : 'Not found'
    });
  }, []);

  return (
    <ToastProvider>
      <AuthProvider>
        <Router>
          <AuthDependentApp />
        </Router>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
