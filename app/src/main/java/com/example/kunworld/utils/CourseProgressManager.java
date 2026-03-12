package com.example.kunworld.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.kunworld.data.database.AppDatabase;
import com.example.kunworld.data.database.CourseProgressDao;
import com.example.kunworld.data.models.CourseProgress;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manager class for handling course completion progress.
 */
public class CourseProgressManager {

    private final CourseProgressDao progressDao;
    private final SessionManager sessionManager;
    private final ExecutorService executor;

    public CourseProgressManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.progressDao = db.courseProgressDao();
        this.sessionManager = SessionManager.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Get or create progress for a course.
     */
    public void getOrCreateProgress(String courseId, String courseTitle, int totalModules,
                                     ProgressCallback callback) {
        executor.execute(() -> {
            try {
                String userId = sessionManager.getCurrentUserId();
                if (userId == null || userId.isEmpty()) {
                    if (callback != null) callback.onError("User not logged in");
                    return;
                }

                CourseProgress progress = progressDao.getProgress(userId, courseId);

                if (progress == null) {
                    progress = new CourseProgress();
                    progress.setUserId(userId);
                    progress.setCourseId(courseId);
                    progress.setCourseTitle(courseTitle);
                    progress.setTotalModules(totalModules);
                    progressDao.insert(progress);
                }

                if (callback != null) callback.onSuccess(progress);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Mark a module as completed.
     */
    public void markModuleComplete(String courseId, int moduleIndex, ProgressCallback callback) {
        executor.execute(() -> {
            try {
                String userId = sessionManager.getCurrentUserId();
                if (userId == null || userId.isEmpty()) {
                    if (callback != null) callback.onError("User not logged in");
                    return;
                }

                CourseProgress progress = progressDao.getProgress(userId, courseId);
                if (progress != null) {
                    progress.markModuleCompleted(moduleIndex);
                    progressDao.update(progress);
                    if (callback != null) callback.onSuccess(progress);
                } else {
                    if (callback != null) callback.onError("Progress not found");
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Mark a module as incomplete.
     */
    public void markModuleIncomplete(String courseId, int moduleIndex, ProgressCallback callback) {
        executor.execute(() -> {
            try {
                String userId = sessionManager.getCurrentUserId();
                if (userId == null || userId.isEmpty()) {
                    if (callback != null) callback.onError("User not logged in");
                    return;
                }

                CourseProgress progress = progressDao.getProgress(userId, courseId);
                if (progress != null) {
                    progress.markModuleIncomplete(moduleIndex);
                    progressDao.update(progress);
                    if (callback != null) callback.onSuccess(progress);
                } else {
                    if (callback != null) callback.onError("Progress not found");
                }
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get progress for a course.
     */
    public LiveData<CourseProgress> getProgressLive(String courseId) {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return progressDao.getProgressLive(userId, courseId);
    }

    /**
     * Get all course progress for current user.
     */
    public LiveData<List<CourseProgress>> getAllProgress() {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return progressDao.getAllProgress(userId);
    }

    /**
     * Get completed courses.
     */
    public LiveData<List<CourseProgress>> getCompletedCourses() {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return progressDao.getCompletedCourses(userId);
    }

    /**
     * Get in-progress courses.
     */
    public LiveData<List<CourseProgress>> getInProgressCourses() {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return progressDao.getInProgressCourses(userId);
    }

    /**
     * Check if a module is completed.
     */
    public void isModuleCompleted(String courseId, int moduleIndex, BooleanCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null || userId.isEmpty()) {
                callback.onResult(false);
                return;
            }

            CourseProgress progress = progressDao.getProgress(userId, courseId);
            boolean completed = progress != null && progress.isModuleCompleted(moduleIndex);
            callback.onResult(completed);
        });
    }

    public interface ProgressCallback {
        void onSuccess(CourseProgress progress);
        void onError(String error);
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }
}
