package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Course> courses);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE featured = 1 ORDER BY rating DESC LIMIT 5")
    LiveData<List<Course>> getFeaturedCourses();

    @Query("SELECT * FROM courses WHERE category = :category ORDER BY createdAt DESC")
    LiveData<List<Course>> getCoursesByCategory(String category);

    @Query("SELECT * FROM courses WHERE id = :courseId")
    LiveData<Course> getCourseById(String courseId);

    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<Course>> searchCourses(String query);

    @Query("DELETE FROM courses")
    void deleteAll();
}
