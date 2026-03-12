package com.example.kunworld.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.MainActivity;
import com.example.kunworld.R;
import com.example.kunworld.data.models.ConsultancyService;
import com.example.kunworld.data.models.CourseData;
import com.example.kunworld.ui.auth.AuthViewModel;
import com.example.kunworld.utils.LastReadManager;
import com.example.kunworld.utils.ReadingGoalsManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private ChipGroup chipGroupCategories;
    private RecyclerView rvFeaturedCourses;
    private RecyclerView rvPopularConsultants;
    private MaterialCardView cardVideos, cardConsultancy, cardCourses;
    private MaterialCardView cardBooks, cardQueries, cardIstikhara;
    private MaterialButton btnExplore;
    private NestedScrollView scrollView;

    // Continue Reading
    private MaterialCardView cardContinueReading;
    private ImageView ivContinueImage;
    private TextView tvContinueTitle;
    private TextView tvContinueProgress;
    private TextView tvLastRead;
    private LinearProgressIndicator progressContinue;
    private MaterialButton btnResume;
    private ShimmerFrameLayout shimmerCourses;

    private NavController navController;
    private AuthViewModel authViewModel;
    private LastReadManager lastReadManager;
    private ReadingGoalsManager readingGoalsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        lastReadManager = new LastReadManager(requireContext());
        readingGoalsManager = new ReadingGoalsManager(requireContext());

        initViews(view);
        setupGreeting();
        setupCategoryChips();
        setupQuickActions();
        setupRecyclerViews();
        setupClickListeners();
        setupHeroBanner();
        setupHomeReselection();
        setupContinueReading();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh continue reading card when returning to home
        setupContinueReading();
    }

    private void setupContinueReading() {
        if (!lastReadManager.hasLastRead()) {
            cardContinueReading.setVisibility(View.GONE);
            return;
        }

        // Show the card
        cardContinueReading.setVisibility(View.VISIBLE);

        // Set data
        tvContinueTitle.setText(lastReadManager.getLastReadTitle());
        int progress = lastReadManager.getProgress();
        progressContinue.setProgress(progress);
        tvContinueProgress.setText(progress + "% complete");
        tvLastRead.setText("Last read " + lastReadManager.getTimeAgo());

        int imageRes = lastReadManager.getLastReadImage();
        if (imageRes != 0) {
            ivContinueImage.setImageResource(imageRes);
        }

        // Resume button click
        btnResume.setOnClickListener(v -> {
            String itemId = lastReadManager.getLastReadId();
            String itemType = lastReadManager.getLastReadType();

            Bundle args = new Bundle();
            if ("course".equals(itemType)) {
                args.putString("courseId", itemId);
                navController.navigate(R.id.action_home_to_courseDetail, args);
            } else if ("book".equals(itemType)) {
                args.putString("bookId", itemId);
                navController.navigate(R.id.action_home_to_bookReader, args);
            }
        });

        // Card click also resumes
        cardContinueReading.setOnClickListener(v -> btnResume.performClick());
    }

    private void setupHomeReselection() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnHomeReselectedListener(() -> {
                // Scroll to top when home is reselected
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the listener to avoid memory leaks
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnHomeReselectedListener(null);
        }
    }

    private void initViews(View view) {
        scrollView = view.findViewById(R.id.scrollView);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        rvFeaturedCourses = view.findViewById(R.id.rvFeaturedCourses);
        rvPopularConsultants = view.findViewById(R.id.rvPopularConsultants);

        // Quick Action Cards
        cardVideos = view.findViewById(R.id.cardVideos);
        cardConsultancy = view.findViewById(R.id.cardConsultancy);
        cardCourses = view.findViewById(R.id.cardCourses);
        cardBooks = view.findViewById(R.id.cardBooks);
        cardQueries = view.findViewById(R.id.cardQueries);
        cardIstikhara = view.findViewById(R.id.cardIstikhara);

        // See All buttons
        view.findViewById(R.id.btnSeeAllCourses).setOnClickListener(v ->
                navController.navigate(R.id.action_home_to_courses));
        view.findViewById(R.id.btnSeeAllConsultants).setOnClickListener(v ->
                navController.navigate(R.id.action_home_to_consultancy));

        // Hero banner explore button
        btnExplore = view.findViewById(R.id.btnExplore);

        // Continue Reading card
        cardContinueReading = view.findViewById(R.id.cardContinueReading);
        ivContinueImage = view.findViewById(R.id.ivContinueImage);
        tvContinueTitle = view.findViewById(R.id.tvContinueTitle);
        tvContinueProgress = view.findViewById(R.id.tvContinueProgress);
        tvLastRead = view.findViewById(R.id.tvLastRead);
        progressContinue = view.findViewById(R.id.progressContinue);
        btnResume = view.findViewById(R.id.btnResume);

        // Shimmer
        shimmerCourses = view.findViewById(R.id.shimmerCourses);
    }

    private void setupGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) {
            greeting = getString(R.string.greeting_morning);
        } else if (hour < 17) {
            greeting = getString(R.string.greeting_afternoon);
        } else {
            greeting = getString(R.string.greeting_evening);
        }

        tvGreeting.setText(greeting);
    }

    private void setupCategoryChips() {
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chipCourses) {
                navController.navigate(R.id.action_home_to_courses);
            } else if (checkedId == R.id.chipBooks) {
                navController.navigate(R.id.action_home_to_books);
            } else if (checkedId == R.id.chipConsultancy) {
                navController.navigate(R.id.action_home_to_consultancy);
            } else if (checkedId == R.id.chipVideos) {
                navController.navigate(R.id.action_home_to_videos);
            } else if (checkedId == R.id.chipIstikhara) {
                navController.navigate(R.id.action_home_to_istikhara);
            }
            // chipAll stays on home

            // Reset to All chip after navigation
            if (checkedId != R.id.chipAll) {
                Chip chipAll = group.findViewById(R.id.chipAll);
                if (chipAll != null) {
                    chipAll.setChecked(true);
                }
            }
        });
    }

    private void setupQuickActions() {
        cardVideos.setOnClickListener(v -> openYouTubeChannel());
        cardConsultancy.setOnClickListener(v -> navController.navigate(R.id.action_home_to_consultancy));
        cardCourses.setOnClickListener(v -> navController.navigate(R.id.action_home_to_courses));
        cardBooks.setOnClickListener(v -> navController.navigate(R.id.action_home_to_books));
        cardQueries.setOnClickListener(v -> navController.navigate(R.id.action_home_to_queries));
        cardIstikhara.setOnClickListener(v -> navController.navigate(R.id.action_home_to_istikhara));
    }

    private void setupRecyclerViews() {
        // Setup Featured Courses RecyclerView
        rvFeaturedCourses.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedCourses.setAdapter(new CourseAdapter(this::onCourseClick));

        // Setup Popular Consultants RecyclerView
        rvPopularConsultants.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularConsultants.setAdapter(new ConsultantAdapter(this::onConsultantClick));

        // Show shimmer briefly then reveal content
        if (shimmerCourses != null) {
            shimmerCourses.startShimmer();
            shimmerCourses.postDelayed(() -> {
                if (isAdded() && shimmerCourses != null) {
                    shimmerCourses.stopShimmer();
                    shimmerCourses.setVisibility(View.GONE);
                    rvFeaturedCourses.setVisibility(View.VISIBLE);
                }
            }, 800); // Brief shimmer effect
        } else {
            rvFeaturedCourses.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        // Avatar click - go to profile
        View avatar = requireView().findViewById(R.id.ivAvatar);
        if (avatar != null) {
            avatar.setOnClickListener(v -> navController.navigate(R.id.profileFragment));
        }
    }

    private void setupHeroBanner() {
        if (btnExplore != null) {
            btnExplore.setOnClickListener(v -> {
                // Navigate to login/signup if guest, otherwise explore courses
                if (authViewModel.isLoggedIn() && !authViewModel.isGuest()) {
                    navController.navigate(R.id.action_home_to_courses);
                } else {
                    navController.navigate(R.id.action_home_to_login);
                }
            });
        }
    }

    private void onCourseClick(String courseId) {
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        navController.navigate(R.id.action_home_to_courseDetail, args);
    }

    private void onConsultantClick(String serviceId) {
        Bundle args = new Bundle();
        args.putString("serviceId", serviceId);
        navController.navigate(R.id.action_home_to_consultancyDetail, args);
    }

    private void openYouTubeChannel() {
        String youtubeUrl = getString(R.string.youtube_channel);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
        startActivity(intent);
    }

    // Adapter for courses using CourseData
    private static class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
        private final OnItemClickListener listener;
        private final java.util.List<CourseData> courses;

        interface OnItemClickListener {
            void onClick(String id);
        }

        CourseAdapter(OnItemClickListener listener) {
            this.listener = listener;
            this.courses = CourseData.getAllCourses();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CourseData course = courses.get(position);

            holder.ivImage.setImageResource(course.getImageRes());
            holder.tvTitle.setText(course.getTitle());
            holder.tvDuration.setText(course.getDuration());
            holder.tvRating.setText("4.8");
            holder.tvRatingCount.setText("(256)");
            holder.tvChapterCount.setText(course.getModuleCount() + " modules");
            holder.tvCategory.setText(course.getFeatures().get(0));

            holder.itemView.setOnClickListener(v ->
                    listener.onClick(course.getId()));
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvTitle, tvDuration, tvRating, tvRatingCount, tvChapterCount, tvCategory;

            ViewHolder(View view) {
                super(view);
                ivImage = view.findViewById(R.id.ivCourseImage);
                tvTitle = view.findViewById(R.id.tvCourseTitle);
                tvDuration = view.findViewById(R.id.tvDuration);
                tvRating = view.findViewById(R.id.tvRating);
                tvRatingCount = view.findViewById(R.id.tvRatingCount);
                tvChapterCount = view.findViewById(R.id.tvChapterCount);
                tvCategory = view.findViewById(R.id.tvCourseCategory);
            }
        }
    }

    // Adapter for consultancy services using ConsultancyService data
    private static class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ViewHolder> {
        private final OnItemClickListener listener;
        private final java.util.List<ConsultancyService> services;

        interface OnItemClickListener {
            void onClick(String id);
        }

        ConsultantAdapter(OnItemClickListener listener) {
            this.listener = listener;
            this.services = ConsultancyService.getAllServices();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_consultant_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ConsultancyService service = services.get(position);

            holder.ivImage.setImageResource(service.getImageRes());
            holder.tvName.setText(service.getTitle());
            holder.tvSpecialty.setText(service.getTagline());
            holder.tvRating.setText("4.9");
            holder.tvExperience.setText(service.getFeatures().size() + " services");

            // Set category badge
            String category = service.getId().substring(0, 1).toUpperCase() +
                    service.getId().substring(1);
            holder.tvCategory.setText(category);

            holder.itemView.setOnClickListener(v ->
                    listener.onClick(service.getId()));
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvSpecialty, tvRating, tvExperience, tvCategory;

            ViewHolder(View view) {
                super(view);
                ivImage = view.findViewById(R.id.ivConsultantImage);
                tvName = view.findViewById(R.id.tvConsultantName);
                tvSpecialty = view.findViewById(R.id.tvSpecialty);
                tvRating = view.findViewById(R.id.tvRating);
                tvExperience = view.findViewById(R.id.tvExperience);
                tvCategory = view.findViewById(R.id.tvCategory);
            }
        }
    }
}
