package com.example.kunworld.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.data.models.BookData;
import com.example.kunworld.data.models.CourseData;
import com.example.kunworld.data.models.UserProgress;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ProgressAdapter extends ListAdapter<UserProgress, ProgressAdapter.ProgressViewHolder> {

    private final OnProgressClickListener listener;

    public interface OnProgressClickListener {
        void onProgressClick(UserProgress progress);
    }

    public ProgressAdapter(OnProgressClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<UserProgress> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserProgress>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserProgress oldItem, @NonNull UserProgress newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserProgress oldItem, @NonNull UserProgress newItem) {
            return oldItem.getProgressPercent() == newItem.getProgressPercent() &&
                   oldItem.getLastPage() == newItem.getLastPage();
        }
    };

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_card, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumbnail;
        private final TextView tvType;
        private final TextView tvTitle;
        private final TextView tvProgressInfo;
        private final LinearProgressIndicator progressBar;
        private final TextView tvPercent;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvType = itemView.findViewById(R.id.tvType);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvProgressInfo = itemView.findViewById(R.id.tvProgressInfo);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvPercent = itemView.findViewById(R.id.tvPercent);
        }

        public void bind(UserProgress progress) {
            String contentType = progress.getContentType();
            String contentId = progress.getContentId();

            // Set type badge
            tvType.setText(contentType.toUpperCase());

            // Get content details based on type
            String title = contentId;
            int imageRes = R.drawable.ic_book;

            if ("book".equals(contentType)) {
                BookData book = BookData.getBookById(contentId);
                if (book != null) {
                    title = book.getTitle();
                    imageRes = book.getImageRes();
                }
            } else if ("course".equals(contentType)) {
                CourseData course = CourseData.getCourseById(contentId);
                if (course != null) {
                    title = course.getTitle();
                    imageRes = course.getImageRes();
                }
            }

            tvTitle.setText(title);
            ivThumbnail.setImageResource(imageRes);

            // Progress info
            int lastPage = progress.getLastPage() + 1; // 0-indexed to 1-indexed
            int totalPages = progress.getTotalPages();
            if (totalPages > 0) {
                tvProgressInfo.setText(String.format("Page %d of %d", lastPage, totalPages));
            } else {
                tvProgressInfo.setText("Started");
            }

            // Progress bar
            int percent = progress.getProgressPercent();
            progressBar.setProgress(percent);

            // Percent text
            if (progress.isCompleted()) {
                tvPercent.setText("Completed");
            } else {
                tvPercent.setText(String.format("%d%% Complete", percent));
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProgressClick(progress);
                }
            });
        }
    }
}
