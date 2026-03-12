package com.example.kunworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.kunworld.data.repository.UserRepository;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500;
    private boolean keepSplashScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen before super.onCreate()
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Keep the splash screen visible while loading
        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);

        // Auto-create guest session if no session exists
        // This ensures progress tracking works for all users
        UserRepository userRepository = UserRepository.getInstance(this);
        if (!userRepository.isLoggedIn()) {
            userRepository.loginAsGuest();
        }

        // Simulate initialization delay then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            keepSplashScreen = false;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}