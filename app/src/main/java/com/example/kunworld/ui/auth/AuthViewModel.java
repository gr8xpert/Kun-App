package com.example.kunworld.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kunworld.data.models.User;
import com.example.kunworld.data.repository.AuthResult;
import com.example.kunworld.data.repository.UserRepository;

public class AuthViewModel extends AndroidViewModel {

    public enum AuthState {
        LOGGED_OUT,
        GUEST,
        LOGGED_IN,
        LOADING
    }

    private final UserRepository userRepository;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = UserRepository.getInstance(application);
        updateAuthState();
    }

    private void updateAuthState() {
        if (userRepository.isLoggedIn()) {
            if (userRepository.isGuest()) {
                authState.setValue(AuthState.GUEST);
            } else {
                authState.setValue(AuthState.LOGGED_IN);
            }
        } else {
            authState.setValue(AuthState.LOGGED_OUT);
        }
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        authState.setValue(AuthState.LOADING);

        userRepository.login(email, password, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                currentUser.postValue(result.getUser());
                authState.postValue(AuthState.LOGGED_IN);
                loginSuccess.postValue(true);
            } else {
                errorMessage.postValue(result.getErrorMessage());
                authState.postValue(AuthState.LOGGED_OUT);
                loginSuccess.postValue(false);
            }
        });
    }

    public void register(String displayName, String email, String password,
                        String confirmPassword, String phone) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        authState.setValue(AuthState.LOADING);

        userRepository.register(displayName, email, password, confirmPassword, phone, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                currentUser.postValue(result.getUser());
                authState.postValue(AuthState.LOGGED_IN);
                signupSuccess.postValue(true);
            } else {
                errorMessage.postValue(result.getErrorMessage());
                authState.postValue(AuthState.LOGGED_OUT);
                signupSuccess.postValue(false);
            }
        });
    }

    public void continueAsGuest() {
        userRepository.loginAsGuest();
        authState.setValue(AuthState.GUEST);
        loginSuccess.setValue(true);
    }

    public void logout() {
        userRepository.logout();
        currentUser.setValue(null);
        authState.setValue(AuthState.LOGGED_OUT);
    }

    public boolean isLoggedIn() {
        return userRepository.isLoggedIn();
    }

    public boolean isGuest() {
        return userRepository.isGuest();
    }

    public String getCurrentUserName() {
        return userRepository.getCurrentUserName();
    }

    public String getCurrentUserEmail() {
        return userRepository.getCurrentUserEmail();
    }

    public String getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }

    public LiveData<AuthState> getAuthState() {
        return authState;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Boolean> getSignupSuccess() {
        return signupSuccess;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void clearLoginSuccess() {
        loginSuccess.setValue(null);
    }

    public void clearSignupSuccess() {
        signupSuccess.setValue(null);
    }
}
