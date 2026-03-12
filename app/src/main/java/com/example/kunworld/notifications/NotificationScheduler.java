package com.example.kunworld.notifications;

import android.content.Context;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.kunworld.utils.SessionManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    private static final String TAG = "NotificationScheduler";
    public static final String WORK_NAME = "daily_quote_notification";

    /**
     * Schedule daily quote notification at the specified time.
     * If already scheduled, it will be replaced with the new time.
     */
    public static void scheduleDailyQuote(Context context, int hour, int minute) {
        // Calculate initial delay to reach target time
        long initialDelay = calculateInitialDelay(hour, minute);

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DailyQuoteWorker.class,
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        workRequest
                );

        Log.d(TAG, "Daily quote scheduled for " + hour + ":" + String.format("%02d", minute));
    }

    /**
     * Schedule using the time stored in preferences.
     */
    public static void scheduleDailyQuote(Context context) {
        SessionManager sessionManager = SessionManager.getInstance(context);
        int hour = sessionManager.getQuoteNotificationHour();
        int minute = sessionManager.getQuoteNotificationMinute();
        scheduleDailyQuote(context, hour, minute);
    }

    /**
     * Cancel scheduled daily quote notifications.
     */
    public static void cancelDailyQuote(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
        Log.d(TAG, "Daily quote notification cancelled");
    }

    /**
     * Calculate delay in milliseconds until the next occurrence of the target time.
     */
    private static long calculateInitialDelay(int targetHour, int targetMinute) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, targetHour);
        target.set(Calendar.MINUTE, targetMinute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        // If target time has already passed today, schedule for tomorrow
        if (target.before(now) || target.equals(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }

        return target.getTimeInMillis() - now.getTimeInMillis();
    }

    /**
     * Initialize notification scheduling based on user preferences.
     * Call this on app startup.
     */
    public static void initializeIfEnabled(Context context) {
        SessionManager sessionManager = SessionManager.getInstance(context);

        if (sessionManager.isDailyQuoteEnabled()) {
            scheduleDailyQuote(context);
        }
    }
}
