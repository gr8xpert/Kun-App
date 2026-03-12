package com.example.kunworld.data.repository;

import android.content.Context;
import android.util.Patterns;

import androidx.lifecycle.LiveData;

import com.example.kunworld.data.database.AppDatabase;
import com.example.kunworld.data.database.UserDao;
import com.example.kunworld.data.models.Bookmark;
import com.example.kunworld.data.models.User;
import com.example.kunworld.data.models.UserProgress;
import com.example.kunworld.utils.SessionManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private static volatile UserRepository INSTANCE;
    private final UserDao userDao;
    private final SessionManager sessionManager;
    private final ExecutorService executor;

    private UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.userDao = db.userDao();
        this.sessionManager = SessionManager.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static UserRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    // ==================== Authentication ====================

    public interface AuthCallback {
        void onResult(AuthResult result);
    }

    public void register(String displayName, String email, String password, String confirmPassword,
                        String phone, AuthCallback callback) {
        executor.execute(() -> {
            // Validate inputs
            if (displayName == null || displayName.trim().isEmpty()) {
                callback.onResult(AuthResult.failure("Name is required", AuthResult.AuthError.UNKNOWN_ERROR));
                return;
            }

            if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                callback.onResult(AuthResult.invalidEmail());
                return;
            }

            if (password == null || password.length() < 6) {
                callback.onResult(AuthResult.weakPassword());
                return;
            }

            if (!password.equals(confirmPassword)) {
                callback.onResult(AuthResult.passwordsDontMatch());
                return;
            }

            // Check if email exists
            if (userDao.emailExists(email.toLowerCase().trim())) {
                callback.onResult(AuthResult.emailExists());
                return;
            }

            try {
                // Create user
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setDisplayName(displayName.trim());
                user.setEmail(email.toLowerCase().trim());
                user.setPhone(phone != null ? phone.trim() : null);
                user.setGuest(false);

                // Hash password
                String salt = SessionManager.generateSalt();
                String passwordHash = SessionManager.hashPassword(password, salt);
                user.setSalt(salt);
                user.setPasswordHash(passwordHash);

                // Save to database
                userDao.insertUser(user);

                // Create session
                sessionManager.createLoginSession(
                        user.getId(),
                        user.getEmail(),
                        user.getDisplayName(),
                        false
                );

                callback.onResult(AuthResult.success(user));
            } catch (Exception e) {
                callback.onResult(AuthResult.failure("Registration failed: " + e.getMessage(),
                        AuthResult.AuthError.UNKNOWN_ERROR));
            }
        });
    }

    public void login(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            if (email == null || email.trim().isEmpty()) {
                callback.onResult(AuthResult.invalidEmail());
                return;
            }

            if (password == null || password.isEmpty()) {
                callback.onResult(AuthResult.invalidCredentials());
                return;
            }

            try {
                User user = userDao.getUserByEmail(email.toLowerCase().trim());
                if (user == null) {
                    callback.onResult(AuthResult.userNotFound());
                    return;
                }

                // Verify password
                if (!SessionManager.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                    callback.onResult(AuthResult.invalidCredentials());
                    return;
                }

                // Create session
                sessionManager.createLoginSession(
                        user.getId(),
                        user.getEmail(),
                        user.getDisplayName(),
                        false
                );

                callback.onResult(AuthResult.success(user));
            } catch (Exception e) {
                callback.onResult(AuthResult.failure("Login failed: " + e.getMessage(),
                        AuthResult.AuthError.UNKNOWN_ERROR));
            }
        });
    }

    public void loginAsGuest() {
        String guestId = "guest_" + System.currentTimeMillis();

        // Create session
        sessionManager.createLoginSession(guestId, null, "Guest User", true);

        // Also create a guest user in the database for foreign key constraints
        executor.execute(() -> {
            try {
                User guestUser = new User();
                guestUser.setId(guestId);
                guestUser.setDisplayName("Guest User");
                guestUser.setGuest(true);
                userDao.insertUser(guestUser);
            } catch (Exception e) {
                // Ignore if user already exists or other errors
            }
        });
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public boolean isGuest() {
        return sessionManager.isGuest();
    }

    public String getCurrentUserId() {
        return sessionManager.getCurrentUserId();
    }

    public String getCurrentUserName() {
        return sessionManager.getCurrentUserName();
    }

    public String getCurrentUserEmail() {
        return sessionManager.getCurrentUserEmail();
    }

    // ==================== Profile Operations ====================

    public LiveData<User> getCurrentUser() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null && !sessionManager.isGuest()) {
            return userDao.getUserById(userId);
        }
        return null;
    }

    public void updateProfile(String displayName, String phone, String bio, AuthCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null || sessionManager.isGuest()) {
                callback.onResult(AuthResult.failure("Not logged in", AuthResult.AuthError.UNKNOWN_ERROR));
                return;
            }

            try {
                long now = System.currentTimeMillis();
                if (displayName != null && !displayName.trim().isEmpty()) {
                    userDao.updateDisplayName(userId, displayName.trim(), now);
                    sessionManager.updateUserName(displayName.trim());
                }
                if (phone != null) {
                    userDao.updatePhone(userId, phone.trim(), now);
                }
                if (bio != null) {
                    userDao.updateBio(userId, bio.trim(), now);
                }

                User updatedUser = userDao.getUserByIdSync(userId);
                callback.onResult(AuthResult.success(updatedUser));
            } catch (Exception e) {
                callback.onResult(AuthResult.failure("Update failed: " + e.getMessage(),
                        AuthResult.AuthError.UNKNOWN_ERROR));
            }
        });
    }

    public String getAvatarPath() {
        // Use cached path from SessionManager (safe to call on main thread)
        return sessionManager.getAvatarPath();
    }

    public interface AvatarCallback {
        void onResult(boolean success);
    }

    public void updateAvatarPath(String path, AvatarCallback callback) {
        // Update cache immediately for quick UI access
        sessionManager.updateAvatarPath(path);

        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null || sessionManager.isGuest()) {
                callback.onResult(true); // Still return true since cache was updated
                return;
            }

            try {
                long now = System.currentTimeMillis();
                userDao.updateAvatarUrl(userId, path, now);
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(true); // Cache was still updated
            }
        });
    }

    // ==================== Progress Operations ====================

    public LiveData<List<UserProgress>> getUserProgress() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getProgressByUser(userId);
        }
        return null;
    }

    public LiveData<List<UserProgress>> getInProgressItems() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getInProgressItems(userId);
        }
        return null;
    }

    public LiveData<List<UserProgress>> getCompletedItems() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getCompletedProgress(userId);
        }
        return null;
    }

    public void saveProgress(String contentId, String contentType, int percent, int lastPage, int totalPages) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) return;

            UserProgress existing = userDao.getProgress(userId, contentId, contentType);
            if (existing != null) {
                userDao.updateProgress(userId, contentId, contentType, percent, lastPage, System.currentTimeMillis());
                if (percent >= 100 && !existing.isCompleted()) {
                    existing.setCompleted(true);
                    existing.setCompletedAt(System.currentTimeMillis());
                    userDao.insertOrUpdateProgress(existing);
                }
            } else {
                UserProgress progress = new UserProgress();
                progress.setId(UUID.randomUUID().toString());
                progress.setUserId(userId);
                progress.setContentId(contentId);
                progress.setContentType(contentType);
                progress.setProgressPercent(percent);
                progress.setLastPage(lastPage);
                progress.setTotalPages(totalPages);
                progress.setCompleted(percent >= 100);
                userDao.insertOrUpdateProgress(progress);
            }
        });
    }

    public interface ProgressCallback {
        void onResult(UserProgress progress);
    }

    public void getProgress(String contentId, String contentType, ProgressCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId != null) {
                UserProgress progress = userDao.getProgress(userId, contentId, contentType);
                callback.onResult(progress);
            } else {
                callback.onResult(null);
            }
        });
    }

    // ==================== Bookmark Operations ====================

    public LiveData<List<Bookmark>> getUserBookmarks() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getBookmarksByUser(userId);
        }
        return null;
    }

    public LiveData<List<Bookmark>> getBookmarksByType(String contentType) {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getBookmarksByType(userId, contentType);
        }
        return null;
    }

    public LiveData<Boolean> isBookmarked(String contentId, String contentType) {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.isBookmarkedLive(userId, contentId, contentType);
        }
        return null;
    }

    public void toggleBookmark(String contentId, String contentType, String title, String imageUrl) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) return;

            boolean exists = userDao.isBookmarked(userId, contentId, contentType);
            if (exists) {
                userDao.deleteBookmarkByContent(userId, contentId, contentType);
            } else {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(UUID.randomUUID().toString());
                bookmark.setUserId(userId);
                bookmark.setContentId(contentId);
                bookmark.setContentType(contentType);
                bookmark.setContentTitle(title);
                bookmark.setContentImageUrl(imageUrl);
                userDao.insertOrUpdateBookmark(bookmark);
            }
        });
    }

    public void addBookmark(String contentId, String contentType, String title, String imageUrl, int pageNumber) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null) return;

            Bookmark bookmark = new Bookmark();
            bookmark.setId(UUID.randomUUID().toString());
            bookmark.setUserId(userId);
            bookmark.setContentId(contentId);
            bookmark.setContentType(contentType);
            bookmark.setContentTitle(title);
            bookmark.setContentImageUrl(imageUrl);
            bookmark.setPageNumber(pageNumber);
            userDao.insertOrUpdateBookmark(bookmark);
        });
    }

    public void removeBookmark(String contentId, String contentType) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId != null) {
                userDao.deleteBookmarkByContent(userId, contentId, contentType);
            }
        });
    }

    public LiveData<Integer> getBookmarkCount() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            return userDao.getBookmarkCountLive(userId);
        }
        return null;
    }

    // ==================== Stats ====================

    public interface StatsCallback {
        void onResult(int inProgress, int completed, int bookmarks);
    }

    public void getUserStats(StatsCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId != null) {
                int inProgress = userDao.getInProgressCount(userId);
                int completed = userDao.getCompletedCount(userId);
                int bookmarks = userDao.getBookmarkCount(userId);
                callback.onResult(inProgress, completed, bookmarks);
            } else {
                callback.onResult(0, 0, 0);
            }
        });
    }

    // ==================== Reset Operations ====================

    public interface ResetCallback {
        void onComplete();
    }

    public void resetAllProgress(ResetCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId != null) {
                userDao.deleteAllProgressForUser(userId);
            }
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    public void resetAllBookmarks(ResetCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId != null) {
                userDao.deleteAllBookmarksForUser(userId);
            }
            if (callback != null) {
                callback.onComplete();
            }
        });
    }
}
