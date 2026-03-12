package com.example.kunworld.notifications;

import android.content.Context;
import android.util.Log;

import com.example.kunworld.api.QuoteApiService;
import com.example.kunworld.api.QuoteResponse;
import com.example.kunworld.utils.SessionManager;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteRepository {
    private static final String TAG = "QuoteRepository";
    private static final String BASE_URL = "https://kunworld.com/";

    // Fallback quotes for offline first-install scenario
    private static final String[] FALLBACK_QUOTES = {
            "Every day is a new beginning. Take a deep breath and start again.",
            "Verily, with hardship comes ease. - Quran 94:6",
            "The journey of a thousand miles begins with a single step.",
            "Indeed, Allah is with the patient. - Quran 2:153",
            "Success is not final, failure is not fatal: it is the courage to continue that counts.",
            "And whoever puts their trust in Allah, He will be enough for them. - Quran 65:3",
            "The only way to do great work is to love what you do.",
            "Seek knowledge from the cradle to the grave.",
            "Be the change you wish to see in the world.",
            "In the remembrance of Allah do hearts find rest. - Quran 13:28"
    };

    private final SessionManager sessionManager;
    private final QuoteApiService apiService;
    private final Gson gson;

    public QuoteRepository(Context context) {
        this.sessionManager = SessionManager.getInstance(context);
        this.gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(QuoteApiService.class);
    }

    /**
     * Get the daily quote. Uses cached quotes if available, falls back to hardcoded quotes.
     * Same quote is shown all day, different quote next day.
     */
    public String getDailyQuote() {
        List<String> quotes = getCachedQuotes();

        if (quotes == null || quotes.isEmpty()) {
            quotes = Arrays.asList(FALLBACK_QUOTES);
        }

        // Use day of year to ensure same quote all day, different next day
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int index = dayOfYear % quotes.size();

        return quotes.get(index);
    }

    /**
     * Fetch quotes from server and cache them. Call this periodically.
     * Returns true if new quotes were fetched.
     */
    public boolean refreshQuotes() {
        try {
            Response<QuoteResponse> response = apiService.getQuotes().execute();

            if (response.isSuccessful() && response.body() != null) {
                QuoteResponse quoteResponse = response.body();

                // Only update if version is newer
                int currentVersion = sessionManager.getQuotesVersion();
                if (quoteResponse.getVersion() > currentVersion &&
                    quoteResponse.getQuotes() != null &&
                    !quoteResponse.getQuotes().isEmpty()) {

                    String quotesJson = gson.toJson(quoteResponse.getQuotes());
                    sessionManager.setCachedQuotes(quotesJson);
                    sessionManager.setQuotesVersion(quoteResponse.getVersion());

                    Log.d(TAG, "Quotes updated to version " + quoteResponse.getVersion());
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch quotes: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get cached quotes from SharedPreferences.
     */
    private List<String> getCachedQuotes() {
        String cachedJson = sessionManager.getCachedQuotes();

        if (cachedJson != null && !cachedJson.isEmpty()) {
            try {
                String[] quotesArray = gson.fromJson(cachedJson, String[].class);
                return Arrays.asList(quotesArray);
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse cached quotes: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Check if we have any cached quotes.
     */
    public boolean hasCachedQuotes() {
        return sessionManager.getCachedQuotes() != null;
    }
}
