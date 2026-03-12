package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Bookmark;
import com.example.kunworld.data.models.User;
import com.example.kunworld.data.models.UserProgress;

import java.util.List;

@Dao
public interface UserDao {

    // ==================== User Operations ====================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(String userId);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserByIdSync(String userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    User authenticateUser(String email, String passwordHash);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean emailExists(String email);

    @Query("UPDATE users SET displayName = :displayName, updatedAt = :updatedAt WHERE id = :userId")
    void updateDisplayName(String userId, String displayName, long updatedAt);

    @Query("UPDATE users SET phone = :phone, updatedAt = :updatedAt WHERE id = :userId")
    void updatePhone(String userId, String phone, long updatedAt);

    @Query("UPDATE users SET bio = :bio, updatedAt = :updatedAt WHERE id = :userId")
    void updateBio(String userId, String bio, long updatedAt);

    @Query("UPDATE users SET avatarUrl = :avatarUrl, updatedAt = :updatedAt WHERE id = :userId")
    void updateAvatarUrl(String userId, String avatarUrl, long updatedAt);

    @Query("UPDATE users SET preferredLanguage = :language, updatedAt = :updatedAt WHERE id = :userId")
    void updatePreferredLanguage(String userId, String language, long updatedAt);

    // ==================== UserProgress Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateProgress(UserProgress progress);

    @Delete
    void deleteProgress(UserProgress progress);

    @Query("SELECT * FROM user_progress WHERE userId = :userId ORDER BY lastAccessedAt DESC")
    LiveData<List<UserProgress>> getProgressByUser(String userId);

    @Query("SELECT * FROM user_progress WHERE userId = :userId ORDER BY lastAccessedAt DESC")
    List<UserProgress> getProgressByUserSync(String userId);

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    UserProgress getProgress(String userId, String contentId, String contentType);

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND contentType = :contentType ORDER BY lastAccessedAt DESC")
    LiveData<List<UserProgress>> getProgressByType(String userId, String contentType);

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND isCompleted = 1 ORDER BY completedAt DESC")
    LiveData<List<UserProgress>> getCompletedProgress(String userId);

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND isCompleted = 0 ORDER BY lastAccessedAt DESC")
    LiveData<List<UserProgress>> getInProgressItems(String userId);

    @Query("SELECT COUNT(*) FROM user_progress WHERE userId = :userId AND contentType = :contentType")
    int getProgressCount(String userId, String contentType);

    @Query("SELECT COUNT(*) FROM user_progress WHERE userId = :userId AND isCompleted = 1")
    int getCompletedCount(String userId);

    @Query("SELECT COUNT(*) FROM user_progress WHERE userId = :userId AND isCompleted = 0 AND progressPercent > 0")
    int getInProgressCount(String userId);

    @Query("UPDATE user_progress SET progressPercent = :percent, lastPage = :lastPage, lastAccessedAt = :lastAccessedAt WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType")
    void updateProgress(String userId, String contentId, String contentType, int percent, int lastPage, long lastAccessedAt);

    @Query("DELETE FROM user_progress WHERE userId = :userId")
    void deleteAllProgressForUser(String userId);

    // ==================== Bookmark Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateBookmark(Bookmark bookmark);

    @Delete
    void deleteBookmark(Bookmark bookmark);

    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> getBookmarksByUser(String userId);

    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
    List<Bookmark> getBookmarksByUserSync(String userId);

    @Query("SELECT * FROM bookmarks WHERE userId = :userId AND contentType = :contentType ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> getBookmarksByType(String userId, String contentType);

    @Query("SELECT * FROM bookmarks WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    Bookmark getBookmark(String userId, String contentId, String contentType);

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType)")
    boolean isBookmarked(String userId, String contentId, String contentType);

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType)")
    LiveData<Boolean> isBookmarkedLive(String userId, String contentId, String contentType);

    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId")
    int getBookmarkCount(String userId);

    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId")
    LiveData<Integer> getBookmarkCountLive(String userId);

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType")
    void deleteBookmarkByContent(String userId, String contentId, String contentType);

    @Query("DELETE FROM bookmarks WHERE userId = :userId")
    void deleteAllBookmarksForUser(String userId);
}
