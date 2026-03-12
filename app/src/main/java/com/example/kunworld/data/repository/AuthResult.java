package com.example.kunworld.data.repository;

import androidx.annotation.Nullable;

import com.example.kunworld.data.models.User;

public class AuthResult {
    private final boolean success;
    private final User user;
    private final String errorMessage;
    private final AuthError errorType;

    public enum AuthError {
        NONE,
        EMAIL_EXISTS,
        INVALID_CREDENTIALS,
        USER_NOT_FOUND,
        INVALID_EMAIL,
        WEAK_PASSWORD,
        PASSWORDS_DONT_MATCH,
        NETWORK_ERROR,
        UNKNOWN_ERROR
    }

    private AuthResult(boolean success, User user, String errorMessage, AuthError errorType) {
        this.success = success;
        this.user = user;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public static AuthResult success(User user) {
        return new AuthResult(true, user, null, AuthError.NONE);
    }

    public static AuthResult failure(String errorMessage, AuthError errorType) {
        return new AuthResult(false, null, errorMessage, errorType);
    }

    public static AuthResult emailExists() {
        return new AuthResult(false, null, "An account with this email already exists", AuthError.EMAIL_EXISTS);
    }

    public static AuthResult invalidCredentials() {
        return new AuthResult(false, null, "Invalid email or password", AuthError.INVALID_CREDENTIALS);
    }

    public static AuthResult userNotFound() {
        return new AuthResult(false, null, "No account found with this email", AuthError.USER_NOT_FOUND);
    }

    public static AuthResult invalidEmail() {
        return new AuthResult(false, null, "Please enter a valid email address", AuthError.INVALID_EMAIL);
    }

    public static AuthResult weakPassword() {
        return new AuthResult(false, null, "Password must be at least 6 characters", AuthError.WEAK_PASSWORD);
    }

    public static AuthResult passwordsDontMatch() {
        return new AuthResult(false, null, "Passwords do not match", AuthError.PASSWORDS_DONT_MATCH);
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public AuthError getErrorType() {
        return errorType;
    }
}
