package com.example.kunworld.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the last read content for "Continue Reading" feature.
 */
public class LastReadManager {

    private static final String PREF_NAME = "last_read";
    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_ITEM_TYPE = "item_type";
    private static final String KEY_ITEM_TITLE = "item_title";
    private static final String KEY_ITEM_IMAGE = "item_image";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_TIMESTAMP = "timestamp";

    private final SharedPreferences prefs;

    public LastReadManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save the last read item.
     */
    public void saveLastRead(String itemId, String itemType, String title, int imageRes, int progressPercent) {
        prefs.edit()
            .putString(KEY_ITEM_ID, itemId)
            .putString(KEY_ITEM_TYPE, itemType)
            .putString(KEY_ITEM_TITLE, title)
            .putInt(KEY_ITEM_IMAGE, imageRes)
            .putInt(KEY_PROGRESS, progressPercent)
            .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            .apply();
    }

    /**
     * Get the last read item ID.
     */
    public String getLastReadId() {
        return prefs.getString(KEY_ITEM_ID, null);
    }

    /**
     * Get the last read item type (course/book).
     */
    public String getLastReadType() {
        return prefs.getString(KEY_ITEM_TYPE, null);
    }

    /**
     * Get the last read item title.
     */
    public String getLastReadTitle() {
        return prefs.getString(KEY_ITEM_TITLE, null);
    }

    /**
     * Get the last read item image resource.
     */
    public int getLastReadImage() {
        return prefs.getInt(KEY_ITEM_IMAGE, 0);
    }

    /**
     * Get the progress percentage.
     */
    public int getProgress() {
        return prefs.getInt(KEY_PROGRESS, 0);
    }

    /**
     * Get the timestamp of last read.
     */
    public long getTimestamp() {
        return prefs.getLong(KEY_TIMESTAMP, 0);
    }

    /**
     * Check if there's a last read item.
     */
    public boolean hasLastRead() {
        return getLastReadId() != null && !getLastReadId().isEmpty();
    }

    /**
     * Get formatted time ago string.
     */
    public String getTimeAgo() {
        long timestamp = getTimestamp();
        if (timestamp == 0) return "";

        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + "m ago";
        } else if (hours < 24) {
            return hours + "h ago";
        } else if (days == 1) {
            return "Yesterday";
        } else {
            return days + "d ago";
        }
    }

    /**
     * Clear the last read item.
     */
    public void clear() {
        prefs.edit().clear().apply();
    }
}
