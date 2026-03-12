package com.example.kunworld;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kunworld.notifications.NotificationHelper;
import com.example.kunworld.notifications.NotificationScheduler;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabChat;

    // Callback interface for scroll to top
    public interface OnHomeReselectedListener {
        void onHomeReselected();
    }

    private OnHomeReselectedListener homeReselectedListener;

    // Destinations where bottom nav should be hidden
    private final Set<Integer> hideBottomNavDestinations = new HashSet<>(Arrays.asList(
            R.id.aiChatFragment,
            R.id.bookReaderFragment,
            R.id.contentReaderFragment,
            R.id.settingsFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize daily quote notifications
        initializeNotifications();

        setupNavigation();
        setupFab();
    }

    private void initializeNotifications() {
        // Create notification channel (required for Android 8.0+)
        NotificationHelper.createNotificationChannel(this);

        // Schedule daily quote if enabled
        NotificationScheduler.initializeIfEnabled(this);
    }

    private void setupNavigation() {
        bottomNav = findViewById(R.id.bottomNav);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        fabChat = findViewById(R.id.fabChat);

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Handle bottom navigation item clicks
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                // For home, use global action to navigate from anywhere
                if (itemId == R.id.homeFragment) {
                    // Check if already on home
                    if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.homeFragment) {
                        // Already on home, trigger scroll to top
                        if (homeReselectedListener != null) {
                            homeReselectedListener.onHomeReselected();
                        }
                        return true;
                    }
                    // Use global action to navigate to home from any screen
                    navController.navigate(R.id.action_global_home);
                    return true;
                }

                // For other destinations, navigate directly
                try {
                    navController.navigate(itemId);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            // Handle reselection (when tab is tapped but already selected)
            bottomNav.setOnItemReselectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.homeFragment) {
                    // Check if actually on home fragment or on a child page
                    if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.homeFragment) {
                        // Actually on home, scroll to top
                        if (homeReselectedListener != null) {
                            homeReselectedListener.onHomeReselected();
                        }
                    } else {
                        // On a child page (detail page), navigate back to home
                        navController.navigate(R.id.action_global_home);
                    }
                }
                // For other tabs, do nothing on reselection
            });

            // Sync bottom nav selection with current destination
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();

                // Update bottom nav selection
                if (destId == R.id.homeFragment || destId == R.id.searchFragment ||
                    destId == R.id.istikharaFragment || destId == R.id.profileFragment) {
                    bottomNav.getMenu().findItem(destId).setChecked(true);
                }

                // Hide/show bottom nav
                if (hideBottomNavDestinations.contains(destId)) {
                    hideBottomNav();
                } else {
                    showBottomNav();
                }
            });

        }
    }

    private void setupFab() {
        fabChat.setOnClickListener(v -> {
            if (navController != null) {
                // Navigate to AI Chat
                navController.navigate(R.id.aiChatFragment);
            }
        });
    }

    private void hideBottomNav() {
        bottomAppBar.setVisibility(View.GONE);
        fabChat.setVisibility(View.GONE);
    }

    private void showBottomNav() {
        bottomAppBar.setVisibility(View.VISIBLE);
        fabChat.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void setOnHomeReselectedListener(OnHomeReselectedListener listener) {
        this.homeReselectedListener = listener;
    }

    public NavController getNavController() {
        return navController;
    }
}
