// Script to suppress Web3-related errors that may appear in the console
// This is used to prevent "Cannot redefine property: ethereum" errors from external scripts

(function() {
    'use strict';
    
    // Store the original defineProperty method
    const originalDefineProperty = Object.defineProperty;
    
    // Override defineProperty to suppress ethereum property redefinition errors
    Object.defineProperty = function(obj, prop, descriptor) {
        try {
            // If trying to redefine the ethereum property, just return the object
            if (prop === 'ethereum' && obj === window && window.ethereum) {
                console.warn('Suppressed ethereum property redefinition attempt');
                return obj;
            }
            
            // Otherwise, proceed with normal defineProperty behavior
            return originalDefineProperty.call(this, obj, prop, descriptor);
        } catch (error) {
            // If there's an error with ethereum property, suppress it
            if (prop === 'ethereum') {
                console.warn('Suppressed ethereum property error:', error.message);
                return obj;
            }
            
            // Re-throw other errors
            throw error;
        }
    };
    
    // Also handle setProperty attempts
    const originalSetProperty = Object.setPrototypeOf;
    Object.setPrototypeOf = function(obj, prototype) {
        try {
            return originalSetProperty.call(this, obj, prototype);
        } catch (error) {
            if (error.message && error.message.includes('ethereum')) {
                console.warn('Suppressed ethereum prototype error:', error.message);
                return obj;
            }
            throw error;
        }
    };
    
    console.log('Web3 error suppression script loaded');
})(); 