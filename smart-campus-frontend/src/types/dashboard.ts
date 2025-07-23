// Student Types
export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  studentId: string;
  major: string;
  yearOfStudy: number;
  status: string;
  createdAt: string;
  updatedAt: string;
  year?: number;
  gpa?: number;
  phone?: string;
  enrollmentDate?: string;
  user?: {
    id: number;
    name: string;
    email: string;
    username: string;
  };
}

export interface StudentRequest {
  firstName: string;
  lastName: string;
  email: string;
  studentId: string;
  major: string;
  yearOfStudy: number;
  status: string;
}

// Professor Types
export interface Professor {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  rank: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  specialization?: string;
  phone?: string;
  office?: string;
  hireDate?: string;
  user?: {
    id: number;
    name: string;
    email: string;
    username: string;
  };
}

export interface ProfessorRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  rank: string;
  status: string;
}

// Course Types
export interface Course {
  id: number;
  name: string;
  code: string;
  description: string;
  credits: number;
  department: string;
  professorId: number;
  professorName?: string;
  maxStudents: number;
  currentEnrollment: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface CourseRequest {
  name: string;
  code: string;
  description: string;
  credits: number;
  department: string;
  professorId: number;
  maxStudents: number;
  status: string;
}

// Enrollment Types
export interface Enrollment {
  id: number;
  studentId: number;
  studentName?: string;
  courseId: number;
  courseName?: string;
  status: string;
  enrollmentDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface EnrollmentRequest {
  studentId: number;
  courseId: number;
}

// Grade Types
export interface Grade {
  id: number;
  enrollmentId: number;
  studentId?: number;
  studentName?: string;
  courseId?: number;
  courseName?: string;
  gradeValue: number;
  maxPoints: number;
  percentage: number;
  gradeType: string;
  assignmentName: string;
  comment?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface GradeRequest {
  enrollmentId: number;
  gradeValue: number;
  maxPoints: number;
  gradeType: string;
  assignmentName: string;
  comment?: string;
}

// Attendance Types
export interface Attendance {
  id: number;
  studentId: number;
  studentName?: string;
  courseId: number;
  courseName?: string;
  date: string;
  status: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AttendanceRequest {
  studentId: number;
  courseId: number;
  date: string;
  status: string;
  notes?: string;
}

// Announcement Types
export interface Announcement {
  id: number;
  title: string;
  content: string;
  courseId?: number;
  courseName?: string;
  postedById: number;
  postedByName?: string;
  postedBy?: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  priority: string;
  type: string;
  isPublic: boolean;
  status: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
  createdByUser?: {
    firstName: string;
    lastName: string;
    email: string;
  };
}

export interface AnnouncementRequest {
  title: string;
  content: string;
  courseId?: number;
  priority: string;
  type: string;
  isPublic: boolean;
}

// Notification Types
export interface Notification {
  id: number;
  title: string;
  message: string;
  userId: number;
  type: string;
  priority: string;
  status: string;
  isRead: boolean;
  read: boolean;
  isArchived: boolean;
  createdAt: string;
  updatedAt: string;
  sender?: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  readAt?: string;
  archivedAt?: string;
  relatedEntityId?: number;
  relatedEntityType?: string;
}

export interface NotificationRequest {
  title: string;
  message: string;
  userId: number;
  type: string;
  priority: string;
}

// User Profile Types
export interface UserProfile {
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
  major?: string;
  year?: number;
  semester?: string;
  academicYear?: string;
  avatar?: string;
  preferences?: {
    theme?: string;
    language?: string;
    notifications?: {
      email?: boolean;
      push?: boolean;
      sms?: boolean;
    };
  };
}

export interface ProfileUpdateRequest {
  name?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  bio?: string;
  department?: string;
  major?: string;
  year?: number;
  semester?: string;
  academicYear?: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// Filter and Search Types
export interface StudentFilters {
  search?: string;
  status?: string;
  major?: string;
  year?: string;
}

export interface ProfessorFilters {
  search?: string;
  status?: string;
  department?: string;
  rank?: string;
}

export interface CourseFilters {
  search?: string;
  status?: string;
  department?: string;
  available?: boolean;
}

export interface EnrollmentFilters {
  search?: string;
  studentId?: number;
  courseId?: number;
  status?: string;
}

export interface GradeFilters {
  search?: string;
  studentId?: number;
  courseId?: number;
  enrollmentId?: number;
  type?: string;
  status?: string;
}

export interface AttendanceFilters {
  search?: string;
  studentId?: number;
  courseId?: number;
  date?: string;
  status?: string;
}

export interface NotificationFilters {
  status?: string;
  priority?: string;
  type?: string;
  read?: boolean;
}

// Pagination Types
export interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

// Response Types for Paginated APIs
export interface StudentsResponse {
  content: Student[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface ProfessorsResponse {
  content: Professor[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface CoursesResponse {
  content: Course[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface EnrollmentsResponse {
  content: Enrollment[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface GradesResponse {
  content: Grade[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface AttendanceResponse {
  content: Attendance[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface AnnouncementsResponse {
  content: Announcement[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface NotificationsResponse {
  content: Notification[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface UnreadCountResponse {
  count: number;
}

export interface CountResponse {
  count: number;
}

export interface RecentActivity {
  id: number;
  type: string;
  description: string;
  userId?: number;
  userName?: string;
  timestamp: string;
  metadata?: Record<string, any>;
}

export interface RecentActivityResponse {
  activities: RecentActivity[];
  totalCount: number;
}

// Dashboard Types
export interface DashboardStats {
  studentsCount: number | null;
  professorsCount: number | null;
  announcementsCount: number | null;
  notificationsCount: number | null;
}

export interface DashboardErrors {
  studentsCount?: string;
  professorsCount?: string;
  announcementsCount?: string;
  notificationsCount?: string;
  recentActivity?: string;
}

// Constants
export const STUDENT_STATUSES = [
  { value: 'ACTIVE', label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'INACTIVE', label: 'Inactive', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'SUSPENDED', label: 'Suspended', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'GRADUATED', label: 'Graduated', color: 'text-blue-600', bgColor: 'bg-blue-100' },
];

export const STUDENT_YEARS = [
  { value: 1, label: 'First Year', color: 'text-blue-600', bgColor: 'bg-blue-100' },
  { value: 2, label: 'Second Year', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 3, label: 'Third Year', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 4, label: 'Fourth Year', color: 'text-purple-600', bgColor: 'bg-purple-100' },
  { value: 5, label: 'Fifth Year', color: 'text-red-600', bgColor: 'bg-red-100' },
];

export const PROFESSOR_STATUSES = [
  { value: 'ACTIVE', label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'INACTIVE', label: 'Inactive', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'ON_LEAVE', label: 'On Leave', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'RETIRED', label: 'Retired', color: 'text-gray-600', bgColor: 'bg-gray-100' },
];

export const PROFESSOR_RANKS = [
  { value: 'ASSISTANT_PROFESSOR', label: 'Assistant Professor', color: 'text-blue-600', bgColor: 'bg-blue-100' },
  { value: 'ASSOCIATE_PROFESSOR', label: 'Associate Professor', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'PROFESSOR', label: 'Professor', color: 'text-purple-600', bgColor: 'bg-purple-100' },
  { value: 'DISTINGUISHED_PROFESSOR', label: 'Distinguished Professor', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'EMERITUS_PROFESSOR', label: 'Emeritus Professor', color: 'text-gray-600', bgColor: 'bg-gray-100' },
];

export const COURSE_STATUSES = [
  { value: 'ACTIVE', label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'INACTIVE', label: 'Inactive', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'DRAFT', label: 'Draft', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'ARCHIVED', label: 'Archived', color: 'text-gray-600', bgColor: 'bg-gray-100' },
];

export const ENROLLMENT_STATUSES = [
  { value: 'ENROLLED', label: 'Enrolled', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'DROPPED', label: 'Dropped', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'WAITLISTED', label: 'Waitlisted', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'COMPLETED', label: 'Completed', color: 'text-blue-600', bgColor: 'bg-blue-100' },
];

export const GRADE_TYPES = [
  { value: 'ASSIGNMENT', label: 'Assignment' },
  { value: 'QUIZ', label: 'Quiz' },
  { value: 'MIDTERM', label: 'Midterm' },
  { value: 'FINAL', label: 'Final' },
  { value: 'PROJECT', label: 'Project' },
  { value: 'PARTICIPATION', label: 'Participation' },
];

export const GRADE_STATUSES = [
  { value: 'DRAFT', label: 'Draft', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'PUBLISHED', label: 'Published', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'DISPUTED', label: 'Disputed', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'FINALIZED', label: 'Finalized', color: 'text-blue-600', bgColor: 'bg-blue-100' },
];

export const ATTENDANCE_STATUSES = [
  { value: 'PRESENT', label: 'Present', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'ABSENT', label: 'Absent', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'LATE', label: 'Late', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'EXCUSED', label: 'Excused', color: 'text-blue-600', bgColor: 'bg-blue-100' },
];

export const ANNOUNCEMENT_PRIORITIES = [
  { value: 'LOW', label: 'Low', color: 'text-gray-600', bgColor: 'bg-gray-100' },
  { value: 'MEDIUM', label: 'Medium', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'HIGH', label: 'High', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'URGENT', label: 'Urgent', color: 'text-red-800', bgColor: 'bg-red-200' },
];

export const ANNOUNCEMENT_TYPES = [
  { value: 'GENERAL', label: 'General', color: 'text-blue-600' },
  { value: 'COURSE', label: 'Course', color: 'text-green-600' },
  { value: 'EVENT', label: 'Event', color: 'text-purple-600' },
  { value: 'DEADLINE', label: 'Deadline', color: 'text-red-600' },
  { value: 'REMINDER', label: 'Reminder', color: 'text-yellow-600' },
];

export const ANNOUNCEMENT_STATUSES = [
  { value: 'ACTIVE', label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'INACTIVE', label: 'Inactive', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'DRAFT', label: 'Draft', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'ARCHIVED', label: 'Archived', color: 'text-gray-600', bgColor: 'bg-gray-100' },
];

export const NOTIFICATION_TYPES = [
  { value: 'GENERAL', label: 'General', color: 'text-blue-600', bgColor: 'bg-blue-100', icon: '📢' },
  { value: 'COURSE', label: 'Course', color: 'text-green-600', bgColor: 'bg-green-100', icon: '📚' },
  { value: 'GRADE', label: 'Grade', color: 'text-yellow-600', bgColor: 'bg-yellow-100', icon: '📊' },
  { value: 'ATTENDANCE', label: 'Attendance', color: 'text-purple-600', bgColor: 'bg-purple-100', icon: '📋' },
  { value: 'DEADLINE', label: 'Deadline', color: 'text-red-600', bgColor: 'bg-red-100', icon: '⏰' },
  { value: 'REMINDER', label: 'Reminder', color: 'text-orange-600', bgColor: 'bg-orange-100', icon: '🔔' },
];

export const NOTIFICATION_STATUSES = [
  { value: 'ACTIVE', label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'INACTIVE', label: 'Inactive', color: 'text-red-600', bgColor: 'bg-red-100' },
  { value: 'ARCHIVED', label: 'Archived', color: 'text-gray-600', bgColor: 'bg-gray-100' },
];

export const NOTIFICATION_PRIORITIES = [
  { value: 'LOW', label: 'Low', color: 'text-gray-600', bgColor: 'bg-gray-100' },
  { value: 'MEDIUM', label: 'Medium', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
  { value: 'HIGH', label: 'High', color: 'text-red-600', bgColor: 'bg-red-100' },
];

export const USER_ROLES = [
  { value: 'STUDENT', label: 'Student', color: 'text-blue-600', bgColor: 'bg-blue-100' },
  { value: 'PROFESSOR', label: 'Professor', color: 'text-green-600', bgColor: 'bg-green-100' },
  { value: 'ADMIN', label: 'Administrator', color: 'text-purple-600', bgColor: 'bg-purple-100' },
];

export const MAJORS = [
  'Computer Science',
  'Mathematics',
  'Physics',
  'Chemistry',
  'Biology',
  'Engineering',
  'Business Administration',
  'Economics',
  'Psychology',
  'Sociology',
  'History',
  'English',
  'Philosophy',
  'Political Science',
  'Art',
  'Music',
  'Theater',
  'Communications',
  'Education',
  'Nursing',
  'Medicine',
  'Law',
  'Architecture',
  'Environmental Science',
  'Information Technology',
];

export const DEPARTMENTS = [
  'Computer Science',
  'Mathematics',
  'Physics',
  'Chemistry',
  'Biology',
  'Engineering',
  'Business',
  'Economics',
  'Psychology',
  'Sociology',
  'History',
  'English',
  'Philosophy',
  'Political Science',
  'Art',
  'Music',
  'Theater',
  'Communications',
  'Education',
  'Nursing',
  'Medicine',
  'Law',
  'Architecture',
  'Environmental Science',
  'Information Technology',
];

export const SEMESTERS = [
  { value: 'FALL', label: 'Fall' },
  { value: 'SPRING', label: 'Spring' },
  { value: 'SUMMER', label: 'Summer' },
  { value: 'WINTER', label: 'Winter' },
]; 