package com.example.kunworld.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kunworld.data.models.Bookmark;
import com.example.kunworld.data.models.User;
import com.example.kunworld.data.models.UserProgress;
import com.example.kunworld.data.repository.UserRepository;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Integer> inProgressCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> completedCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> bookmarkCount = new MutableLiveData<>(0);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = UserRepository.getInstance(application);
        loadStats();
    }

    public void loadStats() {
        userRepository.getUserStats((inProgress, completed, bookmarks) -> {
            inProgressCount.postValue(inProgress);
            completedCount.postValue(completed);
            bookmarkCount.postValue(bookmarks);
        });
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

    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<List<UserProgress>> getUserProgress() {
        return userRepository.getUserProgress();
    }

    public LiveData<List<UserProgress>> getInProgressItems() {
        return userRepository.getInProgressItems();
    }

    public LiveData<List<UserProgress>> getCompletedItems() {
        return userRepository.getCompletedItems();
    }

    public LiveData<List<Bookmark>> getUserBookmarks() {
        return userRepository.getUserBookmarks();
    }

    public LiveData<Integer> getInProgressCount() {
        return inProgressCount;
    }

    public LiveData<Integer> getCompletedCount() {
        return completedCount;
    }

    public LiveData<Integer> getBookmarkCount() {
        return bookmarkCount;
    }

    public void logout() {
        userRepository.logout();
    }

    public String getUserInitials() {
        String name = getCurrentUserName();
        if (name == null || name.isEmpty() || name.equals("Guest User")) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public String getAvatarPath() {
        return userRepository.getAvatarPath();
    }

    public interface AvatarUpdateCallback {
        void onResult(boolean success);
    }

    public void updateAvatarPath(String path, AvatarUpdateCallback callback) {
        userRepository.updateAvatarPath(path, callback::onResult);
    }
}
