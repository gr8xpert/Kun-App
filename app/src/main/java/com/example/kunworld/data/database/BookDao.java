package com.example.kunworld.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kunworld.data.models.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Book> books);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE category = :category ORDER BY createdAt DESC")
    LiveData<List<Book>> getBooksByCategory(String category);

    @Query("SELECT * FROM books WHERE id = :bookId")
    LiveData<Book> getBookById(String bookId);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    LiveData<List<Book>> searchBooks(String query);

    @Query("DELETE FROM books")
    void deleteAll();
}
