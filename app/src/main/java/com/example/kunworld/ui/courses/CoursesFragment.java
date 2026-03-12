package com.example.kunworld.ui.courses;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kunworld.R;
import com.example.kunworld.data.models.CourseData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CoursesFragment extends Fragment {

    private NavController navController;
    private RecyclerView rvCourses;
    private SwipeRefreshLayout swipeRefresh;
    private CourseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        setupToolbar(view);
        setupRecyclerView(view);
        setupSwipeRefresh(view);
    }

    private void setupSwipeRefresh(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::refreshCourses);
        // Set refresh colors to match app theme
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
    }

    private void refreshCourses() {
        // Reload course data
        List<CourseData> courses = CourseData.getAllCourses();
        adapter.updateCourses(courses);

        // Update course count
        TextView tvCourseCount = requireView().findViewById(R.id.tvCourseCount);
        tvCourseCount.setText(courses.size() + " Courses");

        // Stop the refresh animation
        swipeRefresh.setRefreshing(false);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    private void setupRecyclerView(View view) {
        rvCourses = view.findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<CourseData> courses = CourseData.getAllCourses();
        adapter = new CourseAdapter(courses, course -> {
            // Navigate to course detail
            Bundle args = new Bundle();
            args.putString("courseId", course.getId());
            navController.navigate(R.id.action_courses_to_courseDetail, args);
        });

        rvCourses.setAdapter(adapter);

        // Update course count
        TextView tvCourseCount = view.findViewById(R.id.tvCourseCount);
        tvCourseCount.setText(courses.size() + " Courses");
    }

    // Course Adapter
    private static class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        private List<CourseData> courses;
        private final OnCourseClickListener listener;

        void updateCourses(List<CourseData> newCourses) {
            this.courses = newCourses;
            notifyDataSetChanged();
        }

        interface OnCourseClickListener {
            void onCourseClick(CourseData course);
        }

        CourseAdapter(List<CourseData> courses, OnCourseClickListener listener) {
            this.courses = courses;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_list_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CourseData course = courses.get(position);
            holder.bind(course, listener);
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivCourseImage;
            private final TextView tvCourseTitle;
            private final TextView tvCourseTagline;
            private final TextView tvModuleCount;
            private final TextView tvDuration;
            private final TextView tvFeatureCount;
            private final FlexboxLayout chipContainer;
            private final MaterialButton btnViewCourse;
            private final View cardCourse;

            ViewHolder(View itemView) {
                super(itemView);
                cardCourse = itemView.findViewById(R.id.cardCourse);
                ivCourseImage = itemView.findViewById(R.id.ivCourseImage);
                tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
                tvCourseTagline = itemView.findViewById(R.id.tvCourseTagline);
                tvModuleCount = itemView.findViewById(R.id.tvModuleCount);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvFeatureCount = itemView.findViewById(R.id.tvFeatureCount);
                chipContainer = itemView.findViewById(R.id.chipContainer);
                btnViewCourse = itemView.findViewById(R.id.btnViewCourse);
            }

            void bind(CourseData course, OnCourseClickListener listener) {
                ivCourseImage.setImageResource(course.getImageRes());
                tvCourseTitle.setText(course.getTitle());
                tvCourseTagline.setText(course.getTagline());
                tvModuleCount.setText(course.getModuleCount() + " Modules");
                tvDuration.setText(course.getDuration());
                tvFeatureCount.setText(course.getFeatures().size() + " Key Topics");

                // Clear and add feature chips (show max 3)
                chipContainer.removeAllViews();
                List<String> features = course.getFeatures();
                int maxChips = Math.min(3, features.size());

                for (int i = 0; i < maxChips; i++) {
                    TextView chip = new TextView(itemView.getContext());
                    chip.setText(features.get(i));
                    chip.setTextSize(10);
                    chip.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                    chip.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));

                    // Create chip background
                    GradientDrawable chipBg = new GradientDrawable();
                    chipBg.setShape(GradientDrawable.RECTANGLE);
                    chipBg.setCornerRadius(dpToPx(8));
                    chipBg.setColor(ContextCompat.getColor(itemView.getContext(), R.color.surface_variant));
                    chip.setBackground(chipBg);

                    // Add margin
                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, dpToPx(6), dpToPx(6));
                    chip.setLayoutParams(params);

                    chipContainer.addView(chip);
                }

                // Add "+X more" chip if there are more features
                if (features.size() > maxChips) {
                    TextView moreChip = new TextView(itemView.getContext());
                    moreChip.setText("+" + (features.size() - maxChips) + " more");
                    moreChip.setTextSize(10);
                    moreChip.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                    moreChip.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));

                    GradientDrawable moreBg = new GradientDrawable();
                    moreBg.setShape(GradientDrawable.RECTANGLE);
                    moreBg.setCornerRadius(dpToPx(8));
                    moreBg.setStroke(dpToPx(1), ContextCompat.getColor(itemView.getContext(), R.color.primary));
                    moreBg.setColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
                    moreChip.setBackground(moreBg);

                    FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, dpToPx(6), dpToPx(6));
                    moreChip.setLayoutParams(params);

                    chipContainer.addView(moreChip);
                }

                // Click listeners
                cardCourse.setOnClickListener(v -> listener.onCourseClick(course));
                btnViewCourse.setOnClickListener(v -> listener.onCourseClick(course));
            }

            private int dpToPx(int dp) {
                return (int) (dp * itemView.getResources().getDisplayMetrics().density);
            }
        }
    }
}
