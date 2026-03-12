package com.example.kunworld.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity for storing user notes and highlights from books.
 */
@Entity(
    tableName = "notes",
    indices = {
        @Index("userId"),
        @Index(value = {"userId", "bookId"})
    }
)
public class Note {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String userId;

    @NonNull
    private String bookId;

    private String bookTitle;
    private String highlightedText;
    private String userNote;
    private int pageNumber;
    private int highlightColor; // Color resource ID
    private long createdAt;
    private long updatedAt;

    public Note() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getBookId() { return bookId; }
    public void setBookId(@NonNull String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getHighlightedText() { return highlightedText; }
    public void setHighlightedText(String highlightedText) { this.highlightedText = highlightedText; }

    public String getUserNote() { return userNote; }
    public void setUserNote(String userNote) { this.userNote = userNote; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getHighlightColor() { return highlightColor; }
    public void setHighlightColor(int highlightColor) { this.highlightColor = highlightColor; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
