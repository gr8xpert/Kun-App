package com.example.kunworld.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SessionManager {
    private static final String PREF_NAME = "kunworld_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_AVATAR_PATH = "avatar_path";

    // Daily Quote Notification preferences
    private static final String KEY_DAILY_QUOTE_ENABLED = "daily_quote_enabled";
    private static final String KEY_QUOTE_HOUR = "quote_notification_hour";
    private static final String KEY_QUOTE_MINUTE = "quote_notification_minute";
    private static final String KEY_CACHED_QUOTES = "cached_quotes";
    private static final String KEY_QUOTES_VERSION = "quotes_version";

    private static volatile SessionManager INSTANCE;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        SharedPreferences tempPrefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            tempPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Fallback to regular SharedPreferences if encryption fails
            tempPrefs = context.getSharedPreferences(PREF_NAME + "_fallback", Context.MODE_PRIVATE);
        }
        this.prefs = tempPrefs;
    }

    public static SessionManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SessionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public void createLoginSession(String userId, String email, String displayName, boolean isGuest) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, displayName);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_GUEST, isGuest);
        editor.putString(KEY_SESSION_TOKEN, generateSessionToken());
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    public void createGuestSession() {
        String guestId = "guest_" + System.currentTimeMillis();
        createLoginSession(guestId, null, "Guest User", true);
    }

    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isGuest() {
        return prefs.getBoolean(KEY_IS_GUEST, true);
    }

    public String getCurrentUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getCurrentUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getCurrentUserName() {
        return prefs.getString(KEY_USER_NAME, "Guest User");
    }

    public void updateUserName(String displayName) {
        prefs.edit().putString(KEY_USER_NAME, displayName).apply();
    }

    public String getAvatarPath() {
        return prefs.getString(KEY_AVATAR_PATH, null);
    }

    public void updateAvatarPath(String avatarPath) {
        prefs.edit().putString(KEY_AVATAR_PATH, avatarPath).apply();
    }

    public long getLoginTime() {
        return prefs.getLong(KEY_LOGIN_TIME, 0);
    }

    // Daily Quote Notification Methods
    public boolean isDailyQuoteEnabled() {
        return prefs.getBoolean(KEY_DAILY_QUOTE_ENABLED, true); // Enabled by default
    }

    public void setDailyQuoteEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DAILY_QUOTE_ENABLED, enabled).apply();
    }

    public int getQuoteNotificationHour() {
        return prefs.getInt(KEY_QUOTE_HOUR, 9); // Default 9 AM
    }

    public int getQuoteNotificationMinute() {
        return prefs.getInt(KEY_QUOTE_MINUTE, 0); // Default :00
    }

    public void setQuoteNotificationTime(int hour, int minute) {
        prefs.edit()
                .putInt(KEY_QUOTE_HOUR, hour)
                .putInt(KEY_QUOTE_MINUTE, minute)
                .apply();
    }

    public String getCachedQuotes() {
        return prefs.getString(KEY_CACHED_QUOTES, null);
    }

    public void setCachedQuotes(String quotesJson) {
        prefs.edit().putString(KEY_CACHED_QUOTES, quotesJson).apply();
    }

    public int getQuotesVersion() {
        return prefs.getInt(KEY_QUOTES_VERSION, 0);
    }

    public void setQuotesVersion(int version) {
        prefs.edit().putInt(KEY_QUOTES_VERSION, version).apply();
    }

    private String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    // Password hashing utilities using PBKDF2 (secure)
    private static final int PBKDF2_ITERATIONS = 120000; // OWASP recommended minimum
    private static final int HASH_BYTES = 32;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.decode(salt, Base64.NO_WRAP);
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                PBKDF2_ITERATIONS,
                HASH_BYTES * 8
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword(); // Clear sensitive data

            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 hashing failed", e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String storedHash) {
        String computedHash = hashPassword(password, salt);
        // Constant-time comparison to prevent timing attacks
        return constantTimeEquals(computedHash, storedHash);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
