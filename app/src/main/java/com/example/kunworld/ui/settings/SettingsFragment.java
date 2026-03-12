package com.example.kunworld.ui.settings;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.example.kunworld.databinding.FragmentSettingsBinding;
import com.example.kunworld.notifications.NotificationScheduler;
import com.example.kunworld.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;

    // Permission launcher for Android 13+
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableDailyQuotes();
                } else {
                    // Permission denied - disable the toggle
                    binding.switchDailyQuotes.setChecked(false);
                    sessionManager.setDailyQuoteEnabled(false);
                    Snackbar.make(binding.getRoot(),
                            R.string.notification_permission_required,
                            Snackbar.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupToolbar();
        setupDailyQuotesToggle();
        setupNotificationTime();
        loadCurrentSettings();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void setupDailyQuotesToggle() {
        binding.switchDailyQuotes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Check for notification permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Request permission
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return;
                    }
                }
                enableDailyQuotes();
            } else {
                disableDailyQuotes();
            }
        });
    }

    private void enableDailyQuotes() {
        sessionManager.setDailyQuoteEnabled(true);
        NotificationScheduler.scheduleDailyQuote(requireContext());
        updateTimeRowVisibility(true);
    }

    private void disableDailyQuotes() {
        sessionManager.setDailyQuoteEnabled(false);
        NotificationScheduler.cancelDailyQuote(requireContext());
        updateTimeRowVisibility(false);
    }

    private void updateTimeRowVisibility(boolean visible) {
        binding.dividerTime.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.rowNotificationTime.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setupNotificationTime() {
        binding.rowNotificationTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        int currentHour = sessionManager.getQuoteNotificationHour();
        int currentMinute = sessionManager.getQuoteNotificationMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    // Save new time
                    sessionManager.setQuoteNotificationTime(hourOfDay, minute);

                    // Update UI
                    updateNotificationTimeDisplay(hourOfDay, minute);

                    // Reschedule notification with new time
                    if (sessionManager.isDailyQuoteEnabled()) {
                        NotificationScheduler.scheduleDailyQuote(requireContext(), hourOfDay, minute);
                    }
                },
                currentHour,
                currentMinute,
                false // Use 12-hour format with AM/PM
        );

        timePickerDialog.show();
    }

    private void loadCurrentSettings() {
        // Load daily quotes toggle state
        boolean isEnabled = sessionManager.isDailyQuoteEnabled();
        binding.switchDailyQuotes.setChecked(isEnabled);
        updateTimeRowVisibility(isEnabled);

        // Load notification time
        int hour = sessionManager.getQuoteNotificationHour();
        int minute = sessionManager.getQuoteNotificationMinute();
        updateNotificationTimeDisplay(hour, minute);
    }

    private void updateNotificationTimeDisplay(int hour, int minute) {
        String amPm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;

        String timeText = String.format(getString(R.string.settings_notification_time_format),
                displayHour, minute, amPm);
        binding.tvNotificationTime.setText(timeText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
