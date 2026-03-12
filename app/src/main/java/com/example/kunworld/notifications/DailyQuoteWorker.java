package com.example.kunworld.notifications;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kunworld.utils.SessionManager;

public class DailyQuoteWorker extends Worker {
    private static final String TAG = "DailyQuoteWorker";

    public DailyQuoteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SessionManager sessionManager = SessionManager.getInstance(context);

        // Check if feature is enabled
        if (!sessionManager.isDailyQuoteEnabled()) {
            Log.d(TAG, "Daily quotes disabled, skipping notification");
            return Result.success();
        }

        try {
            QuoteRepository quoteRepository = new QuoteRepository(context);

            // Try to refresh quotes from server (will use cached if fails)
            quoteRepository.refreshQuotes();

            // Get today's quote
            String quote = quoteRepository.getDailyQuote();

            // Show notification
            NotificationHelper.showDailyQuoteNotification(context, quote);

            Log.d(TAG, "Daily quote notification shown");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error showing daily quote: " + e.getMessage());
            return Result.failure();
        }
    }
}
