package com.example.kunworld.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProgressPagerAdapter extends FragmentStateAdapter {

    public ProgressPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ProgressListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2; // In Progress and Completed tabs
    }
}
