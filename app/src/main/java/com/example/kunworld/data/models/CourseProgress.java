package com.example.kunworld.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.kunworld.data.database.Converters;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity for tracking course completion progress.
 */
@Entity(
    tableName = "course_progress",
    indices = {
        @Index("userId"),
        @Index(value = {"userId", "courseId"}, unique = true)
    }
)
@TypeConverters(Converters.class)
public class CourseProgress {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String userId;

    @NonNull
    private String courseId;

    private String courseTitle;
    private int totalModules;
    private Set<Integer> completedModules; // Set of module indices that are completed
    private int progressPercent;
    private boolean isCompleted;
    private long startedAt;
    private long lastAccessedAt;
    private long completedAt;

    public CourseProgress() {
        this.id = java.util.UUID.randomUUID().toString();
        this.completedModules = new HashSet<>();
        this.startedAt = System.currentTimeMillis();
        this.lastAccessedAt = System.currentTimeMillis();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getCourseId() { return courseId; }
    public void setCourseId(@NonNull String courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getTotalModules() { return totalModules; }
    public void setTotalModules(int totalModules) { this.totalModules = totalModules; }

    public Set<Integer> getCompletedModules() { return completedModules; }
    public void setCompletedModules(Set<Integer> completedModules) { this.completedModules = completedModules; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public long getStartedAt() { return startedAt; }
    public void setStartedAt(long startedAt) { this.startedAt = startedAt; }

    public long getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(long lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    /**
     * Mark a module as completed.
     */
    public void markModuleCompleted(int moduleIndex) {
        if (completedModules == null) {
            completedModules = new HashSet<>();
        }
        completedModules.add(moduleIndex);
        updateProgress();
    }

    /**
     * Mark a module as incomplete.
     */
    public void markModuleIncomplete(int moduleIndex) {
        if (completedModules != null) {
            completedModules.remove(moduleIndex);
            updateProgress();
        }
    }

    /**
     * Check if a module is completed.
     */
    public boolean isModuleCompleted(int moduleIndex) {
        return completedModules != null && completedModules.contains(moduleIndex);
    }

    /**
     * Get number of completed modules.
     */
    public int getCompletedModuleCount() {
        return completedModules != null ? completedModules.size() : 0;
    }

    /**
     * Update progress percentage based on completed modules.
     */
    private void updateProgress() {
        if (totalModules > 0 && completedModules != null) {
            progressPercent = (completedModules.size() * 100) / totalModules;
            isCompleted = completedModules.size() >= totalModules;
            if (isCompleted && completedAt == 0) {
                completedAt = System.currentTimeMillis();
            }
        }
        lastAccessedAt = System.currentTimeMillis();
    }
}
