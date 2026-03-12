package com.example.kunworld.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.data.models.ConsultancyService;
import com.example.kunworld.data.models.CourseData;
import com.example.kunworld.utils.SearchHistoryManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private TextInputEditText etSearch;
    private RecyclerView rvResults;
    private LinearLayout emptyState;
    private TextView tvEmptyMessage;
    private LinearLayout historyContainer;
    private ChipGroup chipGroupHistory;
    private NavController navController;

    private SearchResultAdapter adapter;
    private SearchHistoryManager searchHistoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        searchHistoryManager = new SearchHistoryManager(requireContext());

        initViews(view);
        setupSearch();
        setupRecyclerView();
        setupSearchHistory();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        rvResults = view.findViewById(R.id.rvResults);
        emptyState = view.findViewById(R.id.emptyState);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        historyContainer = view.findViewById(R.id.historyContainer);
        chipGroupHistory = view.findViewById(R.id.chipGroupHistory);

        // Clear history button
        TextView tvClearHistory = view.findViewById(R.id.tvClearHistory);
        tvClearHistory.setOnClickListener(v -> {
            searchHistoryManager.clearHistory();
            updateHistoryDisplay();
        });
    }

    private void setupSearchHistory() {
        updateHistoryDisplay();
    }

    private void updateHistoryDisplay() {
        List<String> history = searchHistoryManager.getHistory();
        chipGroupHistory.removeAllViews();

        if (history.isEmpty()) {
            historyContainer.setVisibility(View.GONE);
        } else {
            historyContainer.setVisibility(View.VISIBLE);
            for (String query : history) {
                Chip chip = new Chip(requireContext());
                chip.setText(query);
                chip.setCloseIconVisible(true);
                chip.setOnClickListener(v -> {
                    etSearch.setText(query);
                    etSearch.setSelection(query.length());
                    performSearch(query);
                });
                chip.setOnCloseIconClickListener(v -> {
                    searchHistoryManager.removeItem(query);
                    updateHistoryDisplay();
                });
                chipGroupHistory.addView(chip);
            }
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString();
                if (!query.trim().isEmpty()) {
                    searchHistoryManager.addSearch(query);
                }
                performSearch(query);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(new ArrayList<>(), item -> {
            // Save search query when result is clicked
            String currentQuery = etSearch.getText() != null ? etSearch.getText().toString() : "";
            if (!currentQuery.trim().isEmpty()) {
                searchHistoryManager.addSearch(currentQuery);
            }

            if (item.type == SearchItem.TYPE_COURSE) {
                Bundle args = new Bundle();
                args.putString("courseId", item.id);
                navController.navigate(R.id.action_search_to_courseDetail, args);
            } else if (item.type == SearchItem.TYPE_CONSULTANCY) {
                Bundle args = new Bundle();
                args.putString("serviceId", item.id);
                navController.navigate(R.id.action_search_to_consultancyDetail, args);
            }
        });

        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(adapter);
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            showEmptyState("Start typing to search courses and consultancy services");
            historyContainer.setVisibility(searchHistoryManager.isEmpty() ? View.GONE : View.VISIBLE);
            return;
        }

        // Hide history when searching
        historyContainer.setVisibility(View.GONE);

        List<SearchItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        // Search courses
        for (CourseData course : CourseData.getAllCourses()) {
            if (course.getTitle().toLowerCase().contains(lowerQuery) ||
                course.getTagline().toLowerCase().contains(lowerQuery) ||
                course.getDescription().toLowerCase().contains(lowerQuery)) {
                results.add(new SearchItem(
                        course.getId(),
                        course.getTitle(),
                        course.getTagline(),
                        course.getImageRes(),
                        SearchItem.TYPE_COURSE
                ));
            }
        }

        // Search consultancy services
        for (ConsultancyService service : ConsultancyService.getAllServices()) {
            if (service.getTitle().toLowerCase().contains(lowerQuery) ||
                service.getTagline().toLowerCase().contains(lowerQuery)) {
                results.add(new SearchItem(
                        service.getId(),
                        service.getTitle(),
                        service.getTagline(),
                        service.getImageRes(),
                        SearchItem.TYPE_CONSULTANCY
                ));
            }
        }

        if (results.isEmpty()) {
            showEmptyState("No results found for \"" + query + "\"");
        } else {
            hideEmptyState();
            adapter.updateResults(results);
        }
    }

    private void showEmptyState(String message) {
        emptyState.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);
        tvEmptyMessage.setText(message);
    }

    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
        rvResults.setVisibility(View.VISIBLE);
    }

    // Search Item data class
    static class SearchItem {
        static final int TYPE_COURSE = 1;
        static final int TYPE_CONSULTANCY = 2;

        String id;
        String title;
        String subtitle;
        int imageRes;
        int type;

        SearchItem(String id, String title, String subtitle, int imageRes, int type) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.imageRes = imageRes;
            this.type = type;
        }
    }

    // Search Result Adapter
    static class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

        private List<SearchItem> items;
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onClick(SearchItem item);
        }

        SearchResultAdapter(List<SearchItem> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        void updateResults(List<SearchItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchItem item = items.get(position);
            holder.bind(item, listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivImage;
            private final TextView tvTitle;
            private final TextView tvSubtitle;
            private final TextView tvType;

            ViewHolder(View view) {
                super(view);
                ivImage = view.findViewById(R.id.ivImage);
                tvTitle = view.findViewById(R.id.tvTitle);
                tvSubtitle = view.findViewById(R.id.tvSubtitle);
                tvType = view.findViewById(R.id.tvType);
            }

            void bind(SearchItem item, OnItemClickListener listener) {
                ivImage.setImageResource(item.imageRes);
                tvTitle.setText(item.title);
                tvSubtitle.setText(item.subtitle);
                tvType.setText(item.type == SearchItem.TYPE_COURSE ? "Course" : "Consultancy");

                itemView.setOnClickListener(v -> listener.onClick(item));
            }
        }
    }
}
