package com.example.kunworld.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kunworld.R;
import com.example.kunworld.data.repository.UserRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ActivityFragment extends Fragment {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private UserRepository userRepository;
    private NavController navController;

    private final String[] tabTitles = {"In Progress", "Completed"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        userRepository = UserRepository.getInstance(requireContext());

        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        setupToolbar();
        setupViewPager();
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Add menu for reset option
        toolbar.inflateMenu(R.menu.menu_activity);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_reset_progress) {
                showResetConfirmation();
                return true;
            }
            return false;
        });
    }

    private void setupViewPager() {
        ProgressPagerAdapter adapter = new ProgressPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    private void showResetConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.reset_progress_title)
                .setMessage(R.string.reset_progress_message)
                .setPositiveButton(R.string.btn_reset, (dialog, which) -> {
                    resetAllProgress();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void resetAllProgress() {
        userRepository.resetAllProgress(() -> {
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    Snackbar.make(requireView(), R.string.progress_reset_success, Snackbar.LENGTH_SHORT).show();
                    // Refresh the ViewPager
                    viewPager.setAdapter(new ProgressPagerAdapter(this));
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        tab.setText(tabTitles[position]);
                    }).attach();
                });
            }
        });
    }
}
