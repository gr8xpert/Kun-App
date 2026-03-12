package com.example.kunworld.ui.istikhara;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kunworld.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class IstikharaFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    private static final String[] TAB_TITLES = {
            "Personality Match",
            "Marriage Match",
            "Child Name",
            "Magic",
            "Lost Item"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_istikhara, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        setupToolbar();
        setupViewPager();
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());
    }

    private void setupViewPager() {
        IstikharaPagerAdapter adapter = new IstikharaPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();

        // Reduce offscreen page limit for better performance
        viewPager.setOffscreenPageLimit(1);
    }

    private static class IstikharaPagerAdapter extends FragmentStateAdapter {

        public IstikharaPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PersonalityMatchFragment();
                case 1:
                    return new MarriageMatchFragment();
                case 2:
                    return new ChildNameFragment();
                case 3:
                    return new MagicFragment();
                case 4:
                    return new LostItemFragment();
                default:
                    return new PersonalityMatchFragment();
            }
        }

        @Override
        public int getItemCount() {
            return TAB_TITLES.length;
        }
    }
}
