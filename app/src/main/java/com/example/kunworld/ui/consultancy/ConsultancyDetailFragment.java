package com.example.kunworld.ui.consultancy;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.example.kunworld.data.models.ConsultancyService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsultancyDetailFragment extends Fragment {

    private NavController navController;
    private ConsultancyService service;

    // Section icons mapping
    private static final int[] SECTION_ICONS = {
            R.drawable.ic_info,
            R.drawable.ic_activity,
            R.drawable.ic_book,
            R.drawable.ic_consultancy,
            R.drawable.ic_star,
            R.drawable.ic_courses
    };

    // Section colors
    private static final int[] SECTION_COLORS = {
            R.color.secondary,
            R.color.primary,
            R.color.tertiary,
            R.color.success,
            R.color.accent,
            R.color.primary_dark
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultancy_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        String serviceId = null;
        if (getArguments() != null) {
            serviceId = getArguments().getString("serviceId");
        }

        if (serviceId == null) {
            navController.popBackStack();
            return;
        }

        service = ConsultancyService.getServiceById(serviceId);
        if (service == null) {
            navController.popBackStack();
            return;
        }

        setupViews(view);
    }

    private void setupViews(View view) {
        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // Collapsing Toolbar
        CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(service.getTitle());

        // Hero Image
        ImageView ivHeroImage = view.findViewById(R.id.ivHeroImage);
        ivHeroImage.setImageResource(service.getImageRes());

        // Tagline
        TextView tvTagline = view.findViewById(R.id.tvTagline);
        tvTagline.setText(service.getTagline());

        // Stats
        TextView tvFeatureCount = view.findViewById(R.id.tvFeatureCount);
        tvFeatureCount.setText(String.valueOf(service.getFeatures().size()));

        // Parse sections from HTML
        List<Section> sections = parseHtmlSections(service.getHtmlContent());

        TextView tvSectionCount = view.findViewById(R.id.tvSectionCount);
        tvSectionCount.setText(String.valueOf(sections.size()));

        // Build section cards
        LinearLayout sectionsContainer = view.findViewById(R.id.sectionsContainer);
        sectionsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            View sectionCard = createSectionCard(inflater, sectionsContainer, section, i);
            sectionsContainer.addView(sectionCard);
        }

        // Book Appointment
        MaterialButton btnBookAppointment = view.findViewById(R.id.btnBookAppointment);
        btnBookAppointment.setOnClickListener(v -> showBookingDialog());
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
        tvSectionSubtitle.setText(section.items.size() + " items");
        tvSectionSubtitle.setVisibility(View.VISIBLE);

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
        iconBg.setAlpha(30); // 12% opacity
        iconContainer.setBackground(iconBg);

        // Add content items
        LinearLayout contentContainer = card.findViewById(R.id.contentContainer);
        contentContainer.removeAllViews();

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
                    item = item.replace("&ldquo;", "\"")
                              .replace("&rdquo;", "\"")
                              .replace("&amp;", "&")
                              .replace("&rsquo;", "'")
                              .replace("&hellip;", "...");
                    section.items.add(item);
                }
            } else if (content.contains("<ul>")) {
                section.isOrdered = false;
                Pattern liPattern = Pattern.compile("<li>(.*?)</li>");
                Matcher liMatcher = liPattern.matcher(content);
                while (liMatcher.find()) {
                    String item = liMatcher.group(1).trim();
                    item = item.replace("&ldquo;", "\"")
                              .replace("&rdquo;", "\"")
                              .replace("&amp;", "&")
                              .replace("&rsquo;", "'")
                              .replace("&hellip;", "...");
                    section.items.add(item);
                }
            }

            // Also extract paragraph if present (for description)
            Pattern pPattern = Pattern.compile("<p>(.*?)</p>");
            Matcher pMatcher = pPattern.matcher(content);
            if (pMatcher.find() && section.items.isEmpty()) {
                String paragraph = pMatcher.group(1).trim();
                paragraph = paragraph.replace("&rsquo;", "'")
                                    .replace("&ldquo;", "\"")
                                    .replace("&rdquo;", "\"");
                section.description = paragraph;
            }

            if (!section.items.isEmpty() || section.description != null) {
                sections.add(section);
            }
        }

        return sections;
    }

    private void showBookingDialog() {
        BookingDialogFragment dialog = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putString("serviceName", service.getTitle());
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "BookingDialog");
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
