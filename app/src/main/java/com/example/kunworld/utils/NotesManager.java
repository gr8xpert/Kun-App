package com.example.kunworld.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.kunworld.data.database.AppDatabase;
import com.example.kunworld.data.database.NoteDao;
import com.example.kunworld.data.models.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manager class for handling notes and highlights.
 */
public class NotesManager {

    private final NoteDao noteDao;
    private final SessionManager sessionManager;
    private final ExecutorService executor;

    public NotesManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.noteDao = db.noteDao();
        this.sessionManager = SessionManager.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Save a new note or update existing one.
     */
    public void saveNote(String bookId, String bookTitle, String highlightedText,
                         String userNote, int pageNumber, int highlightColor,
                         SaveCallback callback) {
        executor.execute(() -> {
            try {
                String userId = sessionManager.getCurrentUserId();
                if (userId == null || userId.isEmpty()) {
                    if (callback != null) callback.onError("User not logged in");
                    return;
                }

                Note note = new Note();
                note.setUserId(userId);
                note.setBookId(bookId);
                note.setBookTitle(bookTitle);
                note.setHighlightedText(highlightedText);
                note.setUserNote(userNote);
                note.setPageNumber(pageNumber);
                note.setHighlightColor(highlightColor);

                noteDao.insert(note);

                if (callback != null) callback.onSuccess(note);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Update an existing note.
     */
    public void updateNote(Note note, SaveCallback callback) {
        executor.execute(() -> {
            try {
                note.setUpdatedAt(System.currentTimeMillis());
                noteDao.update(note);
                if (callback != null) callback.onSuccess(note);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Delete a note.
     */
    public void deleteNote(Note note, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                noteDao.delete(note);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get all notes for current user.
     */
    public LiveData<List<Note>> getAllNotes() {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return noteDao.getAllNotes(userId);
    }

    /**
     * Get notes for a specific book.
     */
    public LiveData<List<Note>> getNotesForBook(String bookId) {
        String userId = sessionManager.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        return noteDao.getNotesForBook(userId, bookId);
    }

    /**
     * Get note count for a book.
     */
    public void getNoteCount(String bookId, CountCallback callback) {
        executor.execute(() -> {
            String userId = sessionManager.getCurrentUserId();
            if (userId == null || userId.isEmpty()) {
                callback.onResult(0);
                return;
            }
            int count = noteDao.getNoteCountForBook(userId, bookId);
            callback.onResult(count);
        });
    }

    public interface SaveCallback {
        void onSuccess(Note note);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface CountCallback {
        void onResult(int count);
    }
}
