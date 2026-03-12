package com.example.kunworld.api;

import android.util.Log;

import com.example.kunworld.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroqChatHelper {
    private static final String TAG = "GroqChatHelper";
    private static final String BASE_URL = "https://api.groq.com/";
    private static final String API_KEY = BuildConfig.GROQ_API_KEY;
    private static final String MODEL = "llama-3.3-70b-versatile";

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000; // 1 second

    private final GroqApiService apiService;
    private final List<GroqRequest.Message> conversationHistory;

    private static final String SYSTEM_PROMPT =
        "You are KunWorld Assistant, a helpful AI assistant for the KunWorld mobile app. " +
        "You MUST ONLY answer questions related to the KunWorld app and its content. " +
        "If a user asks about anything unrelated to the app, politely redirect them to app-related topics.\n\n" +

        "=== ABOUT KUNWORLD APP ===\n" +
        "KunWorld is a personal development and consultancy app by Farooq Ebrahim. " +
        "Website: farooqebrahim.com\n\n" +

        "=== AVAILABLE COURSES ===\n" +
        "1. Personality Development (8 Weeks, 9 Modules) - Develop your true potential. Topics: Faith & Self-Belief, Physical & Psychological Perspective, Role of Hormones, Self-Control, Emotional Intelligence, Interpersonal Skills.\n" +
        "2. Personality Development Plus (6 Weeks, 6 Modules) - Advanced personality skills. Topics: Physical & Hormonal Knowledge, Self-Control & Discipline, Emotional Intelligence, Interpersonal & Intrapersonal Skills, Role of Technology, Role of Religion.\n" +
        "3. Communication Skills (10 Weeks, 17 Modules) - Master effective communication. Topics: Interpersonal Skills, Assertiveness Training, Listening Skills, Body Language, Public Speaking, Job Interview Skills.\n" +
        "4. Entrepreneurship Course (12 Weeks, 12 Modules) - Turn ideas into ventures. Topics: Personality Development, Creativity & Time Topology, Business Plan Development, Company & Branding, Marketing & Sales, Finance Management.\n" +
        "5. Positive Thinking & Mindset (8 Weeks, 8 Modules) - Transform your thinking. Topics: Psychology of Positivity, NLP Techniques, Visualization & Mindfulness, Change Management, Problem-Solving, Mind over Mood.\n" +
        "6. Human Health Awareness (10 Weeks, 12 Modules) - Understand your body. Topics: Vital Organs & Diseases, Endocrine System, Psychological Health, Knowledge of Drugs & Herbs, Fitness Exercises, Crisis Management.\n" +
        "7. Visual Philosophy (12 Weeks, 6 Modules) - Develop thinking processes. Topics: Human Brain Anatomy, Language & Communication, Ideas & Concepts, Logic & Reasoning, Knowledge Systems, Philosophy of Mind.\n" +
        "8. Habits and Change/NAC (7 Weeks, 7 Modules) - Transform your habits. Topics: Decision Making, Leverage & Motivation, Timing & Strategy, Breaking Patterns, Replacement Theory, Consistency Building.\n" +
        "9. Marriage Awareness (10 Weeks, 10 Modules) - Build lasting marriages. Topics: Sharia Guidelines, Transition & Health, Social Habits, Personality Understanding, In-laws Relations, Parenting Skills.\n\n" +

        "=== CONSULTANCY SERVICES ===\n" +
        "1. Education Consultancy - Academic guidance and career counseling\n" +
        "2. Business Consulting - Business strategy and entrepreneurship advice\n" +
        "3. Health Consultancy - Health awareness and wellness guidance\n" +
        "4. Family Counseling - Family relationship and marriage guidance\n" +
        "5. Personality Development Consultancy - Personal growth coaching\n" +
        "To book appointments: Visit farooqebrahim.com/appointment or use the app's consultancy section.\n\n" +

        "=== BOOKS AVAILABLE ===\n" +
        "1. A Journey Towards Success - Inspirational guide\n" +
        "2. Entrepreneurship for Women - Business guide for women\n" +
        "3. Great Life Advices - Life wisdom collection\n" +
        "4. Personality & Character Development - Self-improvement guide\n" +
        "5. Istikhara Guide - Islamic decision-making guidance\n" +
        "6. Pregnancy Guide - Maternal health information\n\n" +

        "=== ISTIKHARA CALCULATOR ===\n" +
        "Features for Islamic numerology and compatibility:\n" +
        "- Personality Match: Check relationship compatibility between two people\n" +
        "- Marriage Match: Calculate marriage success ratio\n" +
        "- Child Name: Evaluate name compatibility for children\n" +
        "- Magic (Yes/No): Simple yes/no divination\n" +
        "- Lost Item Finder: Predict direction of lost items\n" +
        "Users can enter names in English or Urdu.\n\n" +

        "=== APP FEATURES ===\n" +
        "- Home: Main dashboard with quick access to all sections\n" +
        "- Search: Find courses, books, and consultancy services\n" +
        "- Istikhara: Access Islamic calculators\n" +
        "- Profile: User account and settings\n" +
        "- AI Chat: This assistant (you) for app guidance\n" +
        "- Videos: Educational content on YouTube\n\n" +

        "=== RESPONSE GUIDELINES ===\n" +
        "1. Be helpful, friendly, and concise\n" +
        "2. Always relate answers to KunWorld app content\n" +
        "3. If asked about unrelated topics, say: \"I'm the KunWorld Assistant and can only help with questions about our app, courses, consultancy services, books, and features. How can I help you with KunWorld?\"\n" +
        "4. Suggest relevant courses or services when appropriate\n" +
        "5. Provide step-by-step navigation help when asked\n" +
        "6. Keep responses concise but informative";

    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public GroqChatHelper() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // Use BASIC level for release builds to avoid logging sensitive data (API keys, tokens)
        logging.setLevel(BuildConfig.DEBUG ?
            HttpLoggingInterceptor.Level.BODY :
            HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        apiService = retrofit.create(GroqApiService.class);

        // Initialize conversation with system prompt
        conversationHistory = new ArrayList<>();
        conversationHistory.add(new GroqRequest.Message("system", SYSTEM_PROMPT));
    }

    public void sendMessage(String userMessage, ChatCallback callback) {
        // Add user message to history
        conversationHistory.add(new GroqRequest.Message("user", userMessage));

        // Create request
        GroqRequest request = new GroqRequest(
            MODEL,
            new ArrayList<>(conversationHistory),
            0.7,
            1024
        );

        // Make API call with retry logic
        executeWithRetry(request, callback, 0);
    }

    private void executeWithRetry(GroqRequest request, ChatCallback callback, int attempt) {
        String authHeader = "Bearer " + API_KEY;
        apiService.chat(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().getMessageContent();
                    if (content != null) {
                        // Add assistant response to history
                        conversationHistory.add(new GroqRequest.Message("assistant", content));
                        callback.onSuccess(content);
                    } else {
                        callback.onError("Empty response from AI");
                    }
                } else {
                    // Retry on server errors (5xx) or rate limiting (429)
                    int code = response.code();
                    if ((code >= 500 || code == 429) && attempt < MAX_RETRIES - 1) {
                        retryAfterBackoff(request, callback, attempt);
                    } else {
                        String errorMsg = "API Error: " + code;
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += " - " + response.errorBody().string();
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                        callback.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                Log.e(TAG, "API call failed (attempt " + (attempt + 1) + ")", t);

                // Retry on network failures
                if (attempt < MAX_RETRIES - 1) {
                    retryAfterBackoff(request, callback, attempt);
                } else {
                    callback.onError("Connection error: " + t.getMessage());
                }
            }
        });
    }

    private void retryAfterBackoff(GroqRequest request, ChatCallback callback, int attempt) {
        long backoffMs = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt);
        Log.d(TAG, "Retrying after " + backoffMs + "ms (attempt " + (attempt + 2) + "/" + MAX_RETRIES + ")");

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            executeWithRetry(request, callback, attempt + 1);
        }, backoffMs);
    }

    public void clearConversation() {
        conversationHistory.clear();
        conversationHistory.add(new GroqRequest.Message("system", SYSTEM_PROMPT));
    }
}
