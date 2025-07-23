import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { ToastProvider } from './contexts/ToastContext';
import { AuthProvider } from './contexts/AuthContext';
  import AppRoutes from './routes/AppRoutes';
import ErrorHandlerSetup from './utils/ErrorHandlerSetup';
import { getAuthStatus } from './utils/authUtils';
import './App.css';

function App() {
  // Debug authentication status on app load
  React.useEffect(() => {
    const authStatus = getAuthStatus();
    console.log('App: Authentication status on load:', authStatus);
    
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
          <div className="App min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
            <ErrorHandlerSetup />
            <AppRoutes />
          </div>
        </Router>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
