package com.example.kunworld.ui.bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.data.models.Bookmark;
import com.example.kunworld.data.models.BookData;
import com.example.kunworld.data.models.CourseData;
import com.example.kunworld.data.repository.UserRepository;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment {

    private RecyclerView rvBookmarks;
    private LinearLayout emptyState;
    private UserRepository userRepository;
    private NavController navController;
    private BookmarkAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        userRepository = UserRepository.getInstance(requireContext());

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Setup views
        rvBookmarks = view.findViewById(R.id.rvBookmarks);
        emptyState = view.findViewById(R.id.emptyState);

        rvBookmarks.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookmarkAdapter(new ArrayList<>(), this::onBookmarkClick);
        rvBookmarks.setAdapter(adapter);

        // Observe bookmarks
        loadBookmarks();
    }

    private void loadBookmarks() {
        if (userRepository.getUserBookmarks() != null) {
            userRepository.getUserBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
                if (bookmarks == null || bookmarks.isEmpty()) {
                    showEmptyState();
                } else {
                    showBookmarks(bookmarks);
                }
            });
        } else {
            showEmptyState();
        }
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvBookmarks.setVisibility(View.GONE);
    }

    private void showBookmarks(List<Bookmark> bookmarks) {
        emptyState.setVisibility(View.GONE);
        rvBookmarks.setVisibility(View.VISIBLE);
        adapter.updateData(bookmarks);
    }

    private void onBookmarkClick(Bookmark bookmark) {
        Bundle args = new Bundle();

        if ("course".equals(bookmark.getContentType())) {
            args.putString("courseId", bookmark.getContentId());
            navController.navigate(R.id.action_bookmarks_to_courseDetail, args);
        } else if ("book".equals(bookmark.getContentType())) {
            args.putString("bookId", bookmark.getContentId());
            navController.navigate(R.id.action_bookmarks_to_bookReader, args);
        }
    }

    // Adapter
    private static class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

        private List<Bookmark> bookmarks;
        private final OnBookmarkClickListener listener;

        interface OnBookmarkClickListener {
            void onClick(Bookmark bookmark);
        }

        BookmarkAdapter(List<Bookmark> bookmarks, OnBookmarkClickListener listener) {
            this.bookmarks = bookmarks;
            this.listener = listener;
        }

        void updateData(List<Bookmark> newBookmarks) {
            this.bookmarks = newBookmarks;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bookmark, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Bookmark bookmark = bookmarks.get(position);

            holder.tvTitle.setText(bookmark.getContentTitle());

            // Set type badge
            if ("course".equals(bookmark.getContentType())) {
                holder.tvType.setText("Course");
                holder.tvType.setBackgroundResource(R.drawable.badge_background);

                // Get course image
                CourseData course = CourseData.getCourseById(bookmark.getContentId());
                if (course != null) {
                    holder.ivImage.setImageResource(course.getImageRes());
                }
            } else if ("book".equals(bookmark.getContentType())) {
                holder.tvType.setText("Book");
                holder.tvType.setBackgroundResource(R.drawable.badge_secondary);

                // Get book image
                BookData book = BookData.getBookById(bookmark.getContentId());
                if (book != null) {
                    holder.ivImage.setImageResource(book.getImageRes());
                }
            }

            holder.cardBookmark.setOnClickListener(v -> listener.onClick(bookmark));
        }

        @Override
        public int getItemCount() {
            return bookmarks.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardBookmark;
            ImageView ivImage;
            TextView tvTitle;
            TextView tvType;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardBookmark = itemView.findViewById(R.id.cardBookmark);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvType = itemView.findViewById(R.id.tvType);
            }
        }
    }
}
