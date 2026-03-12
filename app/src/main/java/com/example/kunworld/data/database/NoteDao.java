package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Note;

import java.util.List;

/**
 * Data Access Object for notes and highlights.
 */
@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Note>> getAllNotes(String userId);

    @Query("SELECT * FROM notes WHERE userId = :userId AND bookId = :bookId ORDER BY pageNumber, createdAt")
    LiveData<List<Note>> getNotesForBook(String userId, String bookId);

    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(String noteId);

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId")
    int getNoteCount(String userId);

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId AND bookId = :bookId")
    int getNoteCountForBook(String userId, String bookId);

    @Query("DELETE FROM notes WHERE userId = :userId AND bookId = :bookId")
    void deleteAllNotesForBook(String userId, String bookId);

    @Query("DELETE FROM notes WHERE userId = :userId")
    void deleteAllNotes(String userId);
}
