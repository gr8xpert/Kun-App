package com.example.kunworld.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.kunworld.data.models.Book;
import com.example.kunworld.data.models.Bookmark;
import com.example.kunworld.data.models.Chapter;
import com.example.kunworld.data.models.Consultant;
import com.example.kunworld.data.models.Course;
import com.example.kunworld.data.models.CourseProgress;
import com.example.kunworld.data.models.Note;
import com.example.kunworld.data.models.User;
import com.example.kunworld.data.models.UserProgress;

@Database(
    entities = {
        Course.class,
        Consultant.class,
        Book.class,
        Chapter.class,
        User.class,
        UserProgress.class,
        Bookmark.class,
        Note.class,
        CourseProgress.class
    },
    version = 3,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "kunworld_db";
    private static volatile AppDatabase INSTANCE;

    public abstract CourseDao courseDao();
    public abstract ConsultantDao consultantDao();
    public abstract BookDao bookDao();
    public abstract ChapterDao chapterDao();
    public abstract UserDao userDao();
    public abstract NoteDao noteDao();
    public abstract CourseProgressDao courseProgressDao();

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create notes table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `notes` (" +
                "`id` TEXT NOT NULL PRIMARY KEY, " +
                "`userId` TEXT NOT NULL, " +
                "`bookId` TEXT NOT NULL, " +
                "`bookTitle` TEXT, " +
                "`highlightedText` TEXT, " +
                "`userNote` TEXT, " +
                "`pageNumber` INTEGER NOT NULL DEFAULT 0, " +
                "`highlightColor` INTEGER NOT NULL DEFAULT 0, " +
                "`createdAt` INTEGER NOT NULL DEFAULT 0, " +
                "`updatedAt` INTEGER NOT NULL DEFAULT 0)"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_userId` ON `notes` (`userId`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_userId_bookId` ON `notes` (`userId`, `bookId`)");

            // Create course_progress table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `course_progress` (" +
                "`id` TEXT NOT NULL PRIMARY KEY, " +
                "`userId` TEXT NOT NULL, " +
                "`courseId` TEXT NOT NULL, " +
                "`courseTitle` TEXT, " +
                "`totalModules` INTEGER NOT NULL DEFAULT 0, " +
                "`completedModules` TEXT, " +
                "`progressPercent` INTEGER NOT NULL DEFAULT 0, " +
                "`isCompleted` INTEGER NOT NULL DEFAULT 0, " +
                "`startedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`lastAccessedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`completedAt` INTEGER NOT NULL DEFAULT 0)"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_course_progress_userId` ON `course_progress` (`userId`)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_course_progress_userId_courseId` ON `course_progress` (`userId`, `courseId`)");
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create users table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `users` (" +
                "`id` TEXT NOT NULL PRIMARY KEY, " +
                "`displayName` TEXT, " +
                "`email` TEXT, " +
                "`passwordHash` TEXT, " +
                "`salt` TEXT, " +
                "`phone` TEXT, " +
                "`avatarUrl` TEXT, " +
                "`bio` TEXT, " +
                "`preferredLanguage` TEXT, " +
                "`createdAt` INTEGER NOT NULL DEFAULT 0, " +
                "`updatedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`isGuest` INTEGER NOT NULL DEFAULT 0)"
            );
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)");

            // Create user_progress table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `user_progress` (" +
                "`id` TEXT NOT NULL PRIMARY KEY, " +
                "`userId` TEXT NOT NULL, " +
                "`contentId` TEXT NOT NULL, " +
                "`contentType` TEXT NOT NULL, " +
                "`progressPercent` INTEGER NOT NULL DEFAULT 0, " +
                "`lastPage` INTEGER NOT NULL DEFAULT 0, " +
                "`totalPages` INTEGER NOT NULL DEFAULT 0, " +
                "`lastChapterId` TEXT, " +
                "`lastAccessedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`startedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`completedAt` INTEGER NOT NULL DEFAULT 0, " +
                "`isCompleted` INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON DELETE CASCADE)"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_progress_userId` ON `user_progress` (`userId`)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_progress_userId_contentId_contentType` ON `user_progress` (`userId`, `contentId`, `contentType`)");

            // Create bookmarks table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `bookmarks` (" +
                "`id` TEXT NOT NULL PRIMARY KEY, " +
                "`userId` TEXT NOT NULL, " +
                "`contentId` TEXT NOT NULL, " +
                "`contentType` TEXT NOT NULL, " +
                "`contentTitle` TEXT, " +
                "`contentImageUrl` TEXT, " +
                "`note` TEXT, " +
                "`pageNumber` INTEGER NOT NULL DEFAULT 0, " +
                "`createdAt` INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON DELETE CASCADE)"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_bookmarks_userId` ON `bookmarks` (`userId`)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bookmarks_userId_contentId_contentType` ON `bookmarks` (`userId`, `contentId`, `contentType`)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
