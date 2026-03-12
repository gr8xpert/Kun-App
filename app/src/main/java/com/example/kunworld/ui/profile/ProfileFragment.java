package com.example.kunworld.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.kunworld.BuildConfig;
import com.example.kunworld.R;
import com.example.kunworld.databinding.FragmentProfileBinding;
import com.example.kunworld.ui.auth.AuthViewModel;
import com.example.kunworld.utils.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private AuthViewModel authViewModel;
    private NavController navController;

    // Legacy image picker launcher (works on all Android versions)
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        handleSelectedImage(uri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupUI();
        setupClickListeners();
        observeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh stats when returning to profile
        profileViewModel.loadStats();
        updateAuthUI();
    }

    private void setupUI() {
        // Set version
        binding.tvVersion.setText(getString(R.string.profile_version, BuildConfig.VERSION_NAME));
        updateAuthUI();
    }

    private void updateAuthUI() {
        boolean isLoggedIn = profileViewModel.isLoggedIn();
        boolean isGuest = profileViewModel.isGuest();

        if (isLoggedIn && !isGuest) {
            // Logged in user
            String userName = profileViewModel.getCurrentUserName();
            String userEmail = profileViewModel.getCurrentUserEmail();
            String initials = profileViewModel.getUserInitials();

            binding.tvUserName.setText(userName != null ? userName : "User");
            binding.tvInitials.setText(initials);
            binding.tvUserEmail.setText(userEmail);
            binding.tvUserEmail.setVisibility(View.VISIBLE);
            binding.btnLoginPrompt.setVisibility(View.GONE);
            binding.fabEditProfile.setVisibility(View.VISIBLE);
            binding.btnLogout.setVisibility(View.VISIBLE);

            // Load profile photo
            loadProfilePhoto();
        } else {
            // Guest user or not logged in
            binding.tvUserName.setText(R.string.guest_user);
            binding.tvInitials.setText("?");
            binding.tvUserEmail.setVisibility(View.GONE);
            binding.btnLoginPrompt.setVisibility(View.VISIBLE);
            binding.fabEditProfile.setVisibility(View.GONE);
            binding.btnLogout.setVisibility(View.GONE);
            // Hide photo, show initials for guests
            binding.ivProfilePhoto.setVisibility(View.GONE);
            binding.initialsContainer.setVisibility(View.VISIBLE);
        }
    }

    private void loadProfilePhoto() {
        try {
            String avatarPath = profileViewModel.getAvatarPath();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    // Use Glide for proper image loading (works on all Android versions)
                    Glide.with(this)
                        .load(avatarFile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .signature(new ObjectKey(avatarFile.lastModified()))
                        .circleCrop()
                        .into(binding.ivProfilePhoto);
                    binding.ivProfilePhoto.setVisibility(View.VISIBLE);
                    binding.initialsContainer.setVisibility(View.GONE);
                    return;
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        // No photo, show initials
        binding.ivProfilePhoto.setVisibility(View.GONE);
        binding.initialsContainer.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        // Login prompt for guests
        binding.btnLoginPrompt.setOnClickListener(v -> {
            navController.navigate(R.id.action_profile_to_login);
        });

        // Edit profile photo (for logged in users)
        binding.fabEditProfile.setOnClickListener(v -> {
            showPhotoOptions();
        });

        // Menu items
        binding.menuBookmarks.setOnClickListener(v -> {
            navController.navigate(R.id.action_profile_to_bookmarks);
        });

        binding.menuProgress.setOnClickListener(v -> {
            // Navigate to progress/activity
            navController.navigate(R.id.activityFragment);
        });

        binding.menuNotifications.setOnClickListener(v -> {
            navController.navigate(R.id.action_profile_to_settings);
        });

        binding.menuAbout.setOnClickListener(v -> {
            showAboutDialog();
        });

        // Logout
        binding.btnLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }

    private void showPhotoOptions() {
        try {
            String[] options;
            String avatarPath = profileViewModel.getAvatarPath();
            boolean hasPhoto = avatarPath != null && !avatarPath.isEmpty() && new File(avatarPath).exists();

            if (hasPhoto) {
                options = new String[]{
                    getString(R.string.change_photo),
                    getString(R.string.remove_photo)
                };
            } else {
                options = new String[]{
                    getString(R.string.choose_photo)
                };
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.profile_photo)
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Choose/Change photo
                            launchImagePicker();
                        } else if (which == 1 && hasPhoto) {
                            // Remove photo
                            removeProfilePhoto();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), R.string.error_unknown, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void launchImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), R.string.error_unknown, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void handleSelectedImage(Uri uri) {
        String userId = profileViewModel.getCurrentUserId();
        if (userId == null) {
            Snackbar.make(binding.getRoot(), R.string.photo_update_failed, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        binding.fabEditProfile.setEnabled(false);

        // Process image on background thread to prevent ANR
        new Thread(() -> {
            try {
                String savedPath = ImageUtils.copyImageToInternalStorage(requireContext(), uri,
                        "profile_" + userId);

                if (getActivity() == null) return;

                if (savedPath != null) {
                    // Update database
                    profileViewModel.updateAvatarPath(savedPath, result -> {
                        if (getActivity() != null) {
                            requireActivity().runOnUiThread(() -> {
                                binding.fabEditProfile.setEnabled(true);
                                if (result) {
                                    loadProfilePhoto();
                                    Snackbar.make(binding.getRoot(), R.string.photo_updated, Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(binding.getRoot(), R.string.photo_update_failed, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        binding.fabEditProfile.setEnabled(true);
                        Snackbar.make(binding.getRoot(), R.string.photo_update_failed, Snackbar.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        binding.fabEditProfile.setEnabled(true);
                        Snackbar.make(binding.getRoot(), R.string.photo_update_failed, Snackbar.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void removeProfilePhoto() {
        try {
            String avatarPath = profileViewModel.getAvatarPath();
            if (avatarPath != null) {
                // Delete the file
                File file = new File(avatarPath);
                if (file.exists()) {
                    file.delete();
                }
            }

            // Update database
            profileViewModel.updateAvatarPath(null, result -> {
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        loadProfilePhoto();
                        Snackbar.make(binding.getRoot(), R.string.photo_removed, Snackbar.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), R.string.error_unknown, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void observeData() {
        // Observe stats
        profileViewModel.getInProgressCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvInProgressCount.setText(String.valueOf(count != null ? count : 0));
        });

        profileViewModel.getCompletedCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvCompletedCount.setText(String.valueOf(count != null ? count : 0));
        });

        profileViewModel.getBookmarkCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvBookmarkCount.setText(String.valueOf(count != null ? count : 0));
        });

        // Observe current user for real-time updates
        try {
            if (profileViewModel.getCurrentUser() != null) {
                profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        binding.tvUserName.setText(user.getDisplayName());
                        binding.tvInitials.setText(user.getInitials());
                        if (user.getEmail() != null) {
                            binding.tvUserEmail.setText(user.getEmail());
                            binding.tvUserEmail.setVisibility(View.VISIBLE);
                        }
                        // Load avatar if available
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                            loadProfilePhoto();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Ignore observation errors
        }

        // Observe auth state changes
        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            updateAuthUI();
            profileViewModel.loadStats();
        });
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.btn_logout, (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void performLogout() {
        authViewModel.logout();
        Snackbar.make(binding.getRoot(), R.string.logout_success, Snackbar.LENGTH_SHORT).show();
        updateAuthUI();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.about_message, BuildConfig.VERSION_NAME))
                .setPositiveButton(R.string.btn_ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
