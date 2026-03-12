package com.example.kunworld.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Manages reading goals and streaks.
 */
public class ReadingGoalsManager {

    private static final String PREF_NAME = "reading_goals";
    private static final String KEY_DAILY_GOAL_MINUTES = "daily_goal_minutes";
    private static final String KEY_TODAY_READING_MINUTES = "today_reading_minutes";
    private static final String KEY_LAST_READ_DATE = "last_read_date";
    private static final String KEY_CURRENT_STREAK = "current_streak";
    private static final String KEY_BEST_STREAK = "best_streak";
    private static final String KEY_TOTAL_READING_MINUTES = "total_reading_minutes";
    private static final String KEY_WEEK_READING_MINUTES = "week_reading_minutes";
    private static final String KEY_WEEK_START_DATE = "week_start_date";

    private static final int DEFAULT_DAILY_GOAL = 15; // 15 minutes

    private final SharedPreferences prefs;
    private final SimpleDateFormat dateFormat;

    public ReadingGoalsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    /**
     * Get the daily reading goal in minutes.
     */
    public int getDailyGoalMinutes() {
        return prefs.getInt(KEY_DAILY_GOAL_MINUTES, DEFAULT_DAILY_GOAL);
    }

    /**
     * Set the daily reading goal in minutes.
     */
    public void setDailyGoalMinutes(int minutes) {
        prefs.edit().putInt(KEY_DAILY_GOAL_MINUTES, Math.max(1, minutes)).apply();
    }

    /**
     * Add reading time for today.
     */
    public void addReadingTime(int minutes) {
        checkAndResetDaily();
        int currentMinutes = getTodayReadingMinutes();
        int totalMinutes = getTotalReadingMinutes();
        int weekMinutes = getWeekReadingMinutes();

        prefs.edit()
            .putInt(KEY_TODAY_READING_MINUTES, currentMinutes + minutes)
            .putInt(KEY_TOTAL_READING_MINUTES, totalMinutes + minutes)
            .putInt(KEY_WEEK_READING_MINUTES, weekMinutes + minutes)
            .putString(KEY_LAST_READ_DATE, getTodayDate())
            .apply();

        // Update streak if goal was achieved
        if (currentMinutes < getDailyGoalMinutes() &&
            currentMinutes + minutes >= getDailyGoalMinutes()) {
            updateStreak();
        }
    }

    /**
     * Get today's reading time in minutes.
     */
    public int getTodayReadingMinutes() {
        checkAndResetDaily();
        return prefs.getInt(KEY_TODAY_READING_MINUTES, 0);
    }

    /**
     * Get remaining minutes to reach daily goal.
     */
    public int getRemainingMinutes() {
        int remaining = getDailyGoalMinutes() - getTodayReadingMinutes();
        return Math.max(0, remaining);
    }

    /**
     * Check if today's goal is achieved.
     */
    public boolean isGoalAchievedToday() {
        return getTodayReadingMinutes() >= getDailyGoalMinutes();
    }

    /**
     * Get the current reading streak in days.
     */
    public int getCurrentStreak() {
        checkAndResetStreak();
        return prefs.getInt(KEY_CURRENT_STREAK, 0);
    }

    /**
     * Get the best reading streak ever achieved.
     */
    public int getBestStreak() {
        return prefs.getInt(KEY_BEST_STREAK, 0);
    }

    /**
     * Get total reading time in minutes.
     */
    public int getTotalReadingMinutes() {
        return prefs.getInt(KEY_TOTAL_READING_MINUTES, 0);
    }

    /**
     * Get this week's reading time in minutes.
     */
    public int getWeekReadingMinutes() {
        checkAndResetWeekly();
        return prefs.getInt(KEY_WEEK_READING_MINUTES, 0);
    }

    /**
     * Get progress percentage for today (0-100).
     */
    public int getTodayProgressPercent() {
        int goal = getDailyGoalMinutes();
        int done = getTodayReadingMinutes();
        return Math.min(100, (done * 100) / goal);
    }

    /**
     * Format minutes as a readable string.
     */
    public static String formatMinutes(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            if (mins == 0) {
                return hours + "h";
            }
            return hours + "h " + mins + "m";
        }
    }

    private void updateStreak() {
        int currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0) + 1;
        int bestStreak = prefs.getInt(KEY_BEST_STREAK, 0);

        prefs.edit()
            .putInt(KEY_CURRENT_STREAK, currentStreak)
            .putInt(KEY_BEST_STREAK, Math.max(currentStreak, bestStreak))
            .apply();
    }

    private void checkAndResetDaily() {
        String lastDate = prefs.getString(KEY_LAST_READ_DATE, "");
        String today = getTodayDate();

        if (!today.equals(lastDate)) {
            prefs.edit()
                .putInt(KEY_TODAY_READING_MINUTES, 0)
                .apply();
        }
    }

    private void checkAndResetStreak() {
        String lastDate = prefs.getString(KEY_LAST_READ_DATE, "");
        String yesterday = getYesterdayDate();
        String today = getTodayDate();

        // If last read was not today or yesterday, reset streak
        if (!lastDate.equals(today) && !lastDate.equals(yesterday)) {
            prefs.edit().putInt(KEY_CURRENT_STREAK, 0).apply();
        }
    }

    private void checkAndResetWeekly() {
        String weekStart = prefs.getString(KEY_WEEK_START_DATE, "");
        String currentWeekStart = getWeekStartDate();

        if (!currentWeekStart.equals(weekStart)) {
            prefs.edit()
                .putInt(KEY_WEEK_READING_MINUTES, 0)
                .putString(KEY_WEEK_START_DATE, currentWeekStart)
                .apply();
        }
    }

    private String getTodayDate() {
        return dateFormat.format(new Date());
    }

    private String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return dateFormat.format(cal.getTime());
    }

    private String getWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return dateFormat.format(cal.getTime());
    }
}
