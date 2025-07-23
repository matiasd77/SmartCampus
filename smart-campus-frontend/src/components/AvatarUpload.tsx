import React, { useState, useRef } from 'react';
import { 
  User, 
  Upload, 
  Trash2, 
  Camera,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

interface AvatarUploadProps {
  currentAvatar?: string;
  onUpload: (file: File) => Promise<void>;
  onDelete: () => Promise<void>;
  isLoading?: boolean;
}

export const AvatarUpload: React.FC<AvatarUploadProps> = ({
  currentAvatar,
  onUpload,
  onDelete,
  isLoading = false,
}) => {
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      handleFile(e.target.files[0]);
    }
  };

  const handleFile = async (file: File) => {
    setError(null);
    setSuccess(null);

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      setError('File size must be less than 5MB');
      return;
    }

    try {
      await onUpload(file);
      setSuccess('Avatar updated successfully!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to upload avatar');
    }
  };

  const handleDelete = async () => {
    if (!currentAvatar) return;

    if (window.confirm('Are you sure you want to remove your avatar?')) {
      setError(null);
      setSuccess(null);

      try {
        await onDelete();
        setSuccess('Avatar removed successfully!');
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to remove avatar');
      }
    }
  };

  const openFileDialog = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-gray-200 overflow-hidden">
      <div className="px-6 py-4 border-b border-gray-200 bg-gradient-to-r from-gray-50 to-slate-50">
        <div className="flex items-center">
          <Camera className="w-5 h-5 text-gray-600 mr-2" />
          <h3 className="text-xl font-semibold text-gray-900">Profile Picture</h3>
        </div>
        <p className="mt-1 text-sm text-gray-600">
          Upload a profile picture to personalize your account.
        </p>
      </div>

      <div className="p-6">
        {/* Error Message */}
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 rounded-xl p-4">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <AlertCircle className="w-5 h-5 text-red-400" />
              </div>
              <div className="ml-3">
                <p className="text-sm text-red-600">{error}</p>
              </div>
            </div>
          </div>
        )}

        {/* Success Message */}
        {success && (
          <div className="mb-6 bg-green-50 border border-green-200 rounded-xl p-4">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CheckCircle className="w-5 h-5 text-green-400" />
              </div>
              <div className="ml-3">
                <p className="text-sm text-green-600">{success}</p>
              </div>
            </div>
          </div>
        )}

        <div className="flex flex-col items-center space-y-6">
          {/* Current Avatar */}
          <div className="relative">
            <div className="w-32 h-32 rounded-full overflow-hidden bg-gradient-to-br from-gray-100 to-slate-100 border-4 border-white shadow-xl">
              {currentAvatar ? (
                <img
                  src={currentAvatar}
                  alt="Profile"
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <User className="w-16 h-16 text-gray-400" />
                </div>
              )}
            </div>
            
            {/* Loading Overlay */}
            {isLoading && (
              <div className="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
              </div>
            )}
          </div>

          {/* Upload Area */}
          <div
            className={`w-full max-w-md p-8 border-2 border-dashed rounded-2xl text-center cursor-pointer transition-all duration-300 ${
              dragActive
                ? 'border-blue-400 bg-blue-50 scale-105'
                : 'border-gray-300 hover:border-gray-400 hover:bg-gray-50'
            }`}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            onClick={openFileDialog}
          >
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleFileInput}
              className="hidden"
              disabled={isLoading}
            />
            
            <div className="w-16 h-16 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
              <Upload className="w-8 h-8 text-blue-500" />
            </div>
            
            <p className="text-sm text-gray-600 mb-2">
              <span className="font-semibold text-blue-600 hover:text-blue-500">
                Click to upload
              </span>{' '}
              or drag and drop
            </p>
            <p className="text-xs text-gray-500">
              PNG, JPG, GIF up to 5MB
            </p>
          </div>

          {/* Action Buttons */}
          <div className="flex space-x-4">
            <button
              onClick={openFileDialog}
              disabled={isLoading}
              className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
            >
              <Camera className="w-4 h-4 mr-2" />
              {currentAvatar ? 'Change Picture' : 'Upload Picture'}
            </button>
            
            {currentAvatar && (
              <button
                onClick={handleDelete}
                disabled={isLoading}
                className="inline-flex items-center px-6 py-3 border border-red-300 text-red-600 font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50 hover:bg-red-50"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                Remove
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}; 