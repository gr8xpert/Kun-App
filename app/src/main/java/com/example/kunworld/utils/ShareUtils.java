package com.example.kunworld.utils;

import android.content.Context;
import android.content.Intent;

import com.example.kunworld.R;

/**
 * Utility class for sharing content from the app.
 */
public class ShareUtils {

    /**
     * Share a course with other apps.
     */
    public static void shareCourse(Context context, String courseTitle) {
        String shareText = context.getString(R.string.share_course, courseTitle);
        shareText += "\n\n" + context.getString(R.string.share_app);
        share(context, shareText, courseTitle);
    }

    /**
     * Share a book with other apps.
     */
    public static void shareBook(Context context, String bookTitle) {
        String shareText = context.getString(R.string.share_book, bookTitle);
        shareText += "\n\n" + context.getString(R.string.share_app);
        share(context, shareText, bookTitle);
    }

    /**
     * Share a quote with other apps.
     */
    public static void shareQuote(Context context, String quote) {
        String shareText = context.getString(R.string.share_quote, quote);
        shareText += "\n\n" + context.getString(R.string.share_app);
        share(context, shareText, "KunWorld Quote");
    }

    /**
     * Share the app link.
     */
    public static void shareApp(Context context) {
        String shareText = "Check out KunWorld - your personal development companion!\n\n";
        shareText += context.getString(R.string.share_app);
        share(context, shareText, "KunWorld");
    }

    /**
     * Generic share method.
     */
    private static void share(Context context, String text, String subject) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooser = Intent.createChooser(shareIntent, context.getString(R.string.share));
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(chooser);
    }
}
