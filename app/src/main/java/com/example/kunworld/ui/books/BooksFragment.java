package com.example.kunworld.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kunworld.R;
import com.example.kunworld.data.models.BookData;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BooksFragment extends Fragment {

    private NavController navController;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvBooks;
    private BookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        // Setup RecyclerView
        rvBooks = view.findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new BookAdapter(BookData.getAllBooks(), this::onBookClick);
        rvBooks.setAdapter(adapter);

        // Setup SwipeRefresh
        setupSwipeRefresh(view);
    }

    private void setupSwipeRefresh(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::refreshBooks);
        // Set refresh colors to match app theme
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
    }

    private void refreshBooks() {
        // Reload book data
        List<BookData> books = BookData.getAllBooks();
        adapter.updateBooks(books);

        // Stop the refresh animation
        swipeRefresh.setRefreshing(false);
    }

    private void onBookClick(String bookId) {
        Bundle args = new Bundle();
        args.putString("bookId", bookId);
        navController.navigate(R.id.action_books_to_bookReader, args);
    }

    // Book Adapter
    private static class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

        private List<BookData> books;
        private final OnBookClickListener listener;

        void updateBooks(List<BookData> newBooks) {
            this.books = newBooks;
            notifyDataSetChanged();
        }

        interface OnBookClickListener {
            void onClick(String bookId);
        }

        BookAdapter(List<BookData> books, OnBookClickListener listener) {
            this.books = books;
            this.listener = listener;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_book_card, parent, false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            BookData book = books.get(position);

            holder.ivBookCover.setImageResource(book.getImageRes());
            holder.tvBookTitle.setText(book.getTitle());
            holder.tvAuthor.setText("By " + book.getAuthor());
            holder.tvCategory.setText(book.getCategory());
            holder.tvPageCount.setText(book.getPageCount() + " pages");

            holder.cardBook.setOnClickListener(v -> listener.onClick(book.getId()));
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        static class BookViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardBook;
            ImageView ivBookCover;
            TextView tvBookTitle;
            TextView tvAuthor;
            TextView tvCategory;
            TextView tvPageCount;

            BookViewHolder(@NonNull View itemView) {
                super(itemView);
                cardBook = itemView.findViewById(R.id.cardBook);
                ivBookCover = itemView.findViewById(R.id.ivBookCover);
                tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
                tvAuthor = itemView.findViewById(R.id.tvAuthor);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvPageCount = itemView.findViewById(R.id.tvPageCount);
            }
        }
    }
}
