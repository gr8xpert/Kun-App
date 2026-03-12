package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Chapter;

import java.util.List;

@Dao
public interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chapter chapter);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Chapter> chapters);

    @Update
    void update(Chapter chapter);

    @Delete
    void delete(Chapter chapter);

    @Query("SELECT * FROM chapters WHERE parentId = :parentId AND parentType = :parentType ORDER BY orderIndex ASC")
    LiveData<List<Chapter>> getChaptersByParent(String parentId, String parentType);

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    LiveData<Chapter> getChapterById(String chapterId);

    @Query("DELETE FROM chapters WHERE parentId = :parentId")
    void deleteByParentId(String parentId);

    @Query("DELETE FROM chapters")
    void deleteAll();
}
