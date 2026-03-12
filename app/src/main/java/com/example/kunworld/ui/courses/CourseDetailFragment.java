package com.example.kunworld.ui.courses;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.example.kunworld.data.models.CourseData;
import com.example.kunworld.data.repository.UserRepository;
import com.example.kunworld.ui.consultancy.BookingDialogFragment;
import com.example.kunworld.utils.CourseProgressManager;
import com.example.kunworld.utils.LastReadManager;
import com.example.kunworld.utils.ShareUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseDetailFragment extends Fragment {

    private NavController navController;
    private CourseData course;
    private UserRepository userRepository;
    private LastReadManager lastReadManager;
    private CourseProgressManager courseProgressManager;
    private boolean isBookmarked = false;
    private String courseId;
    private Toolbar toolbar;

    // Section icons mapping
    private static final int[] SECTION_ICONS = {
            R.drawable.ic_info,
            R.drawable.ic_activity,
            R.drawable.ic_book,
            R.drawable.ic_consultancy,
            R.drawable.ic_star,
            R.drawable.ic_courses,
            R.drawable.ic_video,
            R.drawable.ic_person
    };

    // Section colors
    private static final int[] SECTION_COLORS = {
            R.color.secondary,
            R.color.primary,
            R.color.tertiary,
            R.color.success,
            R.color.accent,
            R.color.primary_dark,
            R.color.error,
            R.color.warning
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        userRepository = UserRepository.getInstance(requireContext());
        lastReadManager = new LastReadManager(requireContext());
        courseProgressManager = new CourseProgressManager(requireContext());

        courseId = null;
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }

        if (courseId == null) {
            navController.popBackStack();
            return;
        }

        course = CourseData.getCourseById(courseId);
        if (course == null) {
            navController.popBackStack();
            return;
        }

        // Save as last read for Continue Reading feature
        lastReadManager.saveLastRead(
            courseId,
            "course",
            course.getTitle(),
            course.getImageRes(),
            0 // Courses don't have page-based progress
        );

        // Initialize course progress tracking
        if (userRepository.isLoggedIn()) {
            courseProgressManager.getOrCreateProgress(
                courseId,
                course.getTitle(),
                course.getModuleCount(),
                null
            );
        }

        setupViews(view);
        checkBookmarkStatus();
    }

    private void checkBookmarkStatus() {
        if (course == null || userRepository == null) return;

        try {
            // Only check bookmark status if user is logged in
            if (userRepository.isLoggedIn() && !userRepository.isGuest()) {
                LiveData<Boolean> bookmarkLiveData = userRepository.isBookmarked(courseId, "course");
                if (bookmarkLiveData != null) {
                    bookmarkLiveData.observe(getViewLifecycleOwner(), bookmarked -> {
                        isBookmarked = bookmarked != null && bookmarked;
                        updateBookmarkIcon();
                    });
                }
            }
        } catch (Exception e) {
            // Ignore bookmark check errors
        }
    }

    private void updateBookmarkIcon() {
        try {
            if (toolbar != null && toolbar.getMenu() != null) {
                MenuItem bookmarkItem = toolbar.getMenu().findItem(R.id.action_bookmark);
                if (bookmarkItem != null) {
                    bookmarkItem.setIcon(isBookmarked ?
                        R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
                }
            }
        } catch (Exception e) {
            // Ignore icon update errors
        }
    }

    private void toggleBookmark() {
        if (course == null) return;

        try {
            // Check if user is logged in
            if (userRepository == null || !userRepository.isLoggedIn() || userRepository.isGuest()) {
                // Navigate to login
                Snackbar.make(requireView(), R.string.login_to_bookmark, Snackbar.LENGTH_LONG)
                    .setAction("Login", v -> {
                        try {
                            navController.navigate(R.id.loginFragment);
                        } catch (Exception e) {
                            // Ignore navigation errors
                        }
                    })
                    .show();
                return;
            }

            userRepository.toggleBookmark(
                courseId,
                "course",
                course.getTitle(),
                String.valueOf(course.getImageRes())
            );

            isBookmarked = !isBookmarked;
            updateBookmarkIcon();

            Snackbar.make(requireView(),
                isBookmarked ? R.string.bookmark_added : R.string.bookmark_removed,
                Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error saving bookmark", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setupViews(View view) {
        // Toolbar
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // Add bookmark and share menu
        toolbar.inflateMenu(R.menu.menu_book_reader);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_bookmark) {
                toggleBookmark();
                return true;
            } else if (item.getItemId() == R.id.action_share) {
                ShareUtils.shareCourse(requireContext(), course.getTitle());
                return true;
            }
            return false;
        });

        // Collapsing Toolbar
        CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(course.getTitle());

        // Course Image
        ImageView ivCourseImage = view.findViewById(R.id.ivCourseImage);
        ivCourseImage.setImageResource(course.getImageRes());

        // Tagline
        TextView tvTagline = view.findViewById(R.id.tvTagline);
        tvTagline.setText(course.getTagline());

        // Stats
        TextView tvModuleCount = view.findViewById(R.id.tvModuleCount);
        tvModuleCount.setText(String.valueOf(course.getModuleCount()));

        TextView tvDuration = view.findViewById(R.id.tvDuration);
        tvDuration.setText(course.getDuration().replace(" Weeks", "W"));

        TextView tvFeatureCount = view.findViewById(R.id.tvFeatureCount);
        tvFeatureCount.setText(String.valueOf(course.getFeatures().size()));

        // Description
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        tvDescription.setText(course.getDescription());

        // Features/Key Topics
        LinearLayout featuresContainer = view.findViewById(R.id.featuresContainer);
        featuresContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < course.getFeatures().size(); i++) {
            String feature = course.getFeatures().get(i);
            View featureView = createFeatureItem(inflater, featuresContainer, feature, i);
            featuresContainer.addView(featureView);
        }

        // Parse sections from HTML
        List<Section> sections = parseHtmlSections(course.getHtmlContent());

        // Build section cards
        LinearLayout sectionsContainer = view.findViewById(R.id.sectionsContainer);
        sectionsContainer.removeAllViews();

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            View sectionCard = createSectionCard(inflater, sectionsContainer, section, i);
            sectionsContainer.addView(sectionCard);
        }

        // Enroll Button
        MaterialButton btnEnroll = view.findViewById(R.id.btnEnroll);
        btnEnroll.setOnClickListener(v -> showEnrollmentDialog());
    }

    private View createFeatureItem(LayoutInflater inflater, ViewGroup parent, String feature, int index) {
        View itemView = inflater.inflate(R.layout.item_content_bullet, parent, false);

        TextView tvText = itemView.findViewById(R.id.tvBulletText);
        tvText.setText(feature);

        // Use primary color for all bullet points
        View bullet = itemView.findViewById(R.id.bulletPoint);
        GradientDrawable bulletBg = new GradientDrawable();
        bulletBg.setShape(GradientDrawable.OVAL);
        bulletBg.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
        bullet.setBackground(bulletBg);

        return itemView;
    }

    private View createSectionCard(LayoutInflater inflater, ViewGroup parent, Section section, int index) {
        View card = inflater.inflate(R.layout.item_section_card, parent, false);

        // Section number
        TextView tvSectionNumber = card.findViewById(R.id.tvSectionNumber);
        tvSectionNumber.setText(String.format("%02d", index + 1));

        // Section title
        TextView tvSectionTitle = card.findViewById(R.id.tvSectionTitle);
        tvSectionTitle.setText(section.title);

        // Section subtitle (item count)
        TextView tvSectionSubtitle = card.findViewById(R.id.tvSectionSubtitle);
        if (section.items.isEmpty() && section.description != null) {
            tvSectionSubtitle.setText("Overview");
        } else {
            tvSectionSubtitle.setText(section.items.size() + " items");
        }
        tvSectionSubtitle.setVisibility(View.VISIBLE);

        // Completion checkbox
        MaterialCheckBox cbComplete = card.findViewById(R.id.cbComplete);
        if (userRepository != null && userRepository.isLoggedIn()) {
            cbComplete.setVisibility(View.VISIBLE);
            final int moduleIndex = index;

            // Check if module is already completed
            courseProgressManager.isModuleCompleted(courseId, moduleIndex, completed -> {
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        cbComplete.setChecked(completed);
                        if (completed) {
                            tvSectionTitle.setAlpha(0.6f);
                        }
                    });
                }
            });

            // Handle checkbox changes
            cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    courseProgressManager.markModuleComplete(courseId, moduleIndex, null);
                    tvSectionTitle.setAlpha(0.6f);
                } else {
                    courseProgressManager.markModuleIncomplete(courseId, moduleIndex, null);
                    tvSectionTitle.setAlpha(1.0f);
                }
            });
        }

        // Section icon with color
        ImageView ivSectionIcon = card.findViewById(R.id.ivSectionIcon);
        FrameLayout iconContainer = card.findViewById(R.id.iconContainer);

        int iconIndex = index % SECTION_ICONS.length;
        int colorIndex = index % SECTION_COLORS.length;

        ivSectionIcon.setImageResource(SECTION_ICONS[iconIndex]);
        ivSectionIcon.setColorFilter(ContextCompat.getColor(requireContext(), SECTION_COLORS[colorIndex]));

        // Update icon container background color
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.RECTANGLE);
        iconBg.setCornerRadius(dpToPx(12));
        iconBg.setColor(ContextCompat.getColor(requireContext(), SECTION_COLORS[colorIndex]));
        iconBg.setAlpha(30); // Low opacity
        iconContainer.setBackground(iconBg);

        // Add content items
        LinearLayout contentContainer = card.findViewById(R.id.contentContainer);
        contentContainer.removeAllViews();

        // Add description if present
        if (section.description != null && !section.description.isEmpty()) {
            TextView descView = new TextView(requireContext());
            descView.setText(section.description);
            descView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            descView.setTextSize(13);
            descView.setLineSpacing(0, 1.3f);
            descView.setPadding(0, dpToPx(8), 0, dpToPx(8));
            contentContainer.addView(descView);
        }

        for (int i = 0; i < section.items.size(); i++) {
            String item = section.items.get(i);
            View itemView;

            if (section.isOrdered) {
                // Numbered item
                itemView = inflater.inflate(R.layout.item_content_numbered, contentContainer, false);
                TextView tvNumber = itemView.findViewById(R.id.tvNumber);
                TextView tvText = itemView.findViewById(R.id.tvNumberedText);
                tvNumber.setText(String.valueOf(i + 1));
                tvText.setText(item);

                // Use consistent green color for all number badges
                GradientDrawable badgeBg = new GradientDrawable();
                badgeBg.setShape(GradientDrawable.RECTANGLE);
                badgeBg.setCornerRadius(dpToPx(10));
                badgeBg.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
                tvNumber.setBackground(badgeBg);

            } else {
                // Bullet item
                itemView = inflater.inflate(R.layout.item_content_bullet, contentContainer, false);
                TextView tvText = itemView.findViewById(R.id.tvBulletText);
                tvText.setText(item);

                // Use consistent green color for all bullet points
                View bullet = itemView.findViewById(R.id.bulletPoint);
                GradientDrawable bulletBg = new GradientDrawable();
                bulletBg.setShape(GradientDrawable.OVAL);
                bulletBg.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
                bullet.setBackground(bulletBg);
            }

            contentContainer.addView(itemView);
        }

        return card;
    }

    private List<Section> parseHtmlSections(String html) {
        List<Section> sections = new ArrayList<>();

        // Pattern to match h2 headers and following content
        Pattern sectionPattern = Pattern.compile("<h2>(.*?)</h2>([\\s\\S]*?)(?=<h2>|$)");
        Matcher matcher = sectionPattern.matcher(html);

        while (matcher.find()) {
            String title = matcher.group(1).trim();
            String content = matcher.group(2);

            Section section = new Section();
            section.title = title;
            section.items = new ArrayList<>();

            // Check if content has ordered list
            if (content.contains("<ol>")) {
                section.isOrdered = true;
                Pattern liPattern = Pattern.compile("<li>(.*?)</li>");
                Matcher liMatcher = liPattern.matcher(content);
                while (liMatcher.find()) {
                    String item = liMatcher.group(1).trim();
                    // Clean up HTML entities
                    item = cleanHtmlEntities(item);
                    section.items.add(item);
                }
            } else if (content.contains("<ul>")) {
                section.isOrdered = false;
                Pattern liPattern = Pattern.compile("<li>(.*?)</li>");
                Matcher liMatcher = liPattern.matcher(content);
                while (liMatcher.find()) {
                    String item = liMatcher.group(1).trim();
                    item = cleanHtmlEntities(item);
                    section.items.add(item);
                }
            }

            // Also extract paragraph if present (for description)
            Pattern pPattern = Pattern.compile("<p>(.*?)</p>");
            Matcher pMatcher = pPattern.matcher(content);
            if (pMatcher.find() && section.items.isEmpty()) {
                String paragraph = pMatcher.group(1).trim();
                paragraph = cleanHtmlEntities(paragraph);
                section.description = paragraph;
            }

            if (!section.items.isEmpty() || section.description != null) {
                sections.add(section);
            }
        }

        return sections;
    }

    private String cleanHtmlEntities(String text) {
        return text.replace("&ldquo;", "\"")
                   .replace("&rdquo;", "\"")
                   .replace("&amp;", "&")
                   .replace("&rsquo;", "'")
                   .replace("&lsquo;", "'")
                   .replace("&hellip;", "...")
                   .replace("&mdash;", "—")
                   .replace("&ndash;", "–");
    }

    private void showEnrollmentDialog() {
        BookingDialogFragment dialog = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putString("serviceName", "Course: " + course.getTitle());
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "EnrollmentDialog");
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Section data class
    private static class Section {
        String title;
        String description;
        List<String> items;
        boolean isOrdered;
    }
}
