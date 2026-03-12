package com.example.kunworld.api;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TransliterationService - Converts English text to Urdu using Google Input Tools API.
 * Provides suggestions for Urdu spellings of English-typed names.
 */
public class TransliterationService {

    private static final String API_URL = "https://inputtools.google.com/request";
    private static final String LANGUAGE_CODE = "ur-t-i0-und"; // Urdu transliteration
    private static final int NUM_SUGGESTIONS = 5;

    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface TransliterationCallback {
        void onSuccess(List<String> suggestions);
        void onError(String error);
    }

    public TransliterationService() {
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Transliterate an English word to Urdu.
     *
     * @param englishWord The word to transliterate
     * @param callback    Callback for results
     */
    public void transliterate(String englishWord, TransliterationCallback callback) {
        if (englishWord == null || englishWord.trim().isEmpty()) {
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            return;
        }

        executor.execute(() -> {
            try {
                List<String> suggestions = fetchTransliteration(englishWord.trim());
                mainHandler.post(() -> callback.onSuccess(suggestions));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Transliterate the last word in a sentence.
     * Useful for real-time input as user types.
     *
     * @param text     The full text input
     * @param callback Callback for results
     */
    public void transliterateLastWord(String text, TransliterationCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            return;
        }

        String[] words = text.split("\\s+");
        if (words.length > 0) {
            String lastWord = words[words.length - 1];
            transliterate(lastWord, callback);
        } else {
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
        }
    }

    private List<String> fetchTransliteration(String word) throws IOException, JSONException {
        List<String> suggestions = new ArrayList<>();

        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8.name());
        String urlString = String.format("%s?text=%s&itc=%s&num=%d&cp=0&cs=1&ie=utf-8&oe=utf-8&app=demopage",
                API_URL, encodedWord, LANGUAGE_CODE, NUM_SUGGESTIONS);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                suggestions = parseResponse(response.toString());
            }
        } finally {
            connection.disconnect();
        }

        return suggestions;
    }

    private List<String> parseResponse(String jsonResponse) throws JSONException {
        List<String> suggestions = new ArrayList<>();

        // Response format: ["SUCCESS",[["word",[suggestion1,suggestion2,...]]]]
        JSONArray rootArray = new JSONArray(jsonResponse);

        if (rootArray.length() > 1) {
            JSONArray dataArray = rootArray.getJSONArray(1);
            if (dataArray.length() > 0) {
                JSONArray wordData = dataArray.getJSONArray(0);
                if (wordData.length() > 1) {
                    JSONArray suggestionArray = wordData.getJSONArray(1);
                    for (int i = 0; i < suggestionArray.length(); i++) {
                        suggestions.add(suggestionArray.getString(i));
                    }
                }
            }
        }

        return suggestions;
    }

    /**
     * Cancel pending transliteration requests.
     */
    public void cancel() {
        // The executor will finish current task but won't process new ones
        // For now, we don't interrupt running tasks
    }

    /**
     * Shutdown the service.
     * Call this when the activity/fragment is destroyed.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Check if a string contains Urdu/Arabic characters.
     */
    public static boolean containsUrdu(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        // Unicode ranges for Arabic script (includes Urdu)
        return text.matches(".*[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF].*");
    }

    /**
     * Check if a string contains only English characters (and spaces/numbers).
     */
    public static boolean isEnglishOnly(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.matches("[a-zA-Z0-9\\s]+");
    }
}
