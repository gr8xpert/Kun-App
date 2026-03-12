package com.example.kunworld.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages search history using SharedPreferences.
 */
public class SearchHistoryManager {

    private static final String PREF_NAME = "search_history";
    private static final String KEY_HISTORY = "history_list";
    private static final int MAX_HISTORY_SIZE = 10;

    private final SharedPreferences prefs;
    private final Gson gson;

    public SearchHistoryManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Add a search query to history.
     * Duplicates are moved to the top.
     */
    public void addSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        String trimmedQuery = query.trim();
        LinkedList<String> history = getHistoryList();

        // Remove if already exists (will be added to top)
        history.remove(trimmedQuery);

        // Add to the beginning
        history.addFirst(trimmedQuery);

        // Limit size
        while (history.size() > MAX_HISTORY_SIZE) {
            history.removeLast();
        }

        saveHistory(history);
    }

    /**
     * Get all search history items.
     */
    public List<String> getHistory() {
        return new ArrayList<>(getHistoryList());
    }

    /**
     * Get search suggestions based on current input.
     */
    public List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return getHistory();
        }

        String lowerQuery = query.toLowerCase().trim();
        for (String item : getHistoryList()) {
            if (item.toLowerCase().contains(lowerQuery)) {
                suggestions.add(item);
            }
        }
        return suggestions;
    }

    /**
     * Remove a specific item from history.
     */
    public void removeItem(String query) {
        LinkedList<String> history = getHistoryList();
        history.remove(query);
        saveHistory(history);
    }

    /**
     * Clear all search history.
     */
    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    /**
     * Check if history is empty.
     */
    public boolean isEmpty() {
        return getHistoryList().isEmpty();
    }

    private LinkedList<String> getHistoryList() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) {
            return new LinkedList<>();
        }

        Type type = new TypeToken<LinkedList<String>>() {}.getType();
        LinkedList<String> history = gson.fromJson(json, type);
        return history != null ? history : new LinkedList<>();
    }

    private void saveHistory(LinkedList<String> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }
}
