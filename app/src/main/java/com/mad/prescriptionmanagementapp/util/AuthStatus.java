package com.mad.prescriptionmanagementapp.util;

public enum AuthStatus {
    IDLE, // Pending
    GOOGLE_LOADING, // Fetching id token from Google SDK
    GOOGLE_SUCCESS, // Successfully fetched Google id token
    GOOGLE_FAILED, // Failed to fetch Google id token
    VERIFYING,  // Calling to backend to verify id token
    LOGIN_SUCCESS,      // Backend confirmed successful login
    REGISTRATION_REQUIRED, // Backend requires registration/completion of information
    VERIFY_FAILED,      // Verify API call failed (network, server error...)
    REGISTERING,        // Calling register API on the backend
    REGISTRATION_SUCCESS, // Registration successful
    REGISTRATION_FAILED // Registration failed
}