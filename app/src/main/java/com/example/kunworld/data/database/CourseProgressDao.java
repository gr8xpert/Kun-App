package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.CourseProgress;

import java.util.List;

/**
 * Data Access Object for course progress tracking.
 */
@Dao
public interface CourseProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CourseProgress progress);

    @Update
    void update(CourseProgress progress);

    @Query("SELECT * FROM course_progress WHERE userId = :userId ORDER BY lastAccessedAt DESC")
    LiveData<List<CourseProgress>> getAllProgress(String userId);

    @Query("SELECT * FROM course_progress WHERE userId = :userId AND courseId = :courseId")
    CourseProgress getProgress(String userId, String courseId);

    @Query("SELECT * FROM course_progress WHERE userId = :userId AND courseId = :courseId")
    LiveData<CourseProgress> getProgressLive(String userId, String courseId);

    @Query("SELECT * FROM course_progress WHERE userId = :userId AND isCompleted = 1 ORDER BY completedAt DESC")
    LiveData<List<CourseProgress>> getCompletedCourses(String userId);

    @Query("SELECT * FROM course_progress WHERE userId = :userId AND isCompleted = 0 ORDER BY lastAccessedAt DESC")
    LiveData<List<CourseProgress>> getInProgressCourses(String userId);

    @Query("SELECT COUNT(*) FROM course_progress WHERE userId = :userId AND isCompleted = 1")
    int getCompletedCourseCount(String userId);

    @Query("DELETE FROM course_progress WHERE userId = :userId AND courseId = :courseId")
    void deleteProgress(String userId, String courseId);

    @Query("DELETE FROM course_progress WHERE userId = :userId")
    void deleteAllProgress(String userId);
}
