package com.example.kunworld.ui.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.example.kunworld.data.models.BookData;
import com.example.kunworld.data.repository.UserRepository;
import com.example.kunworld.utils.LastReadManager;
import com.example.kunworld.utils.ReadingGoalsManager;
import com.example.kunworld.utils.ShareUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

public class BookReaderFragment extends Fragment implements OnPageChangeListener, OnLoadCompleteListener, OnErrorListener {

    private PDFView pdfView;
    private FrameLayout loadingContainer;
    private LinearLayout errorContainer;
    private MaterialCardView pageIndicatorCard;
    private TextView tvPageIndicator;
    private Toolbar toolbar;
    private MaterialButton btnRetry;
    private NavController navController;

    private String bookId;
    private BookData currentBook;
    private int totalPages = 0;
    private int currentPage = 0;
    private int startPage = 0;

    private UserRepository userRepository;
    private LastReadManager lastReadManager;
    private ReadingGoalsManager readingGoalsManager;
    private boolean isBookmarked = false;
    private long readingStartTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        userRepository = UserRepository.getInstance(requireContext());
        lastReadManager = new LastReadManager(requireContext());
        readingGoalsManager = new ReadingGoalsManager(requireContext());
        readingStartTime = System.currentTimeMillis();

        // Get book ID from arguments
        if (getArguments() != null) {
            bookId = getArguments().getString("bookId");
        }

        // Find views
        toolbar = view.findViewById(R.id.toolbar);
        pdfView = view.findViewById(R.id.pdfView);
        loadingContainer = view.findViewById(R.id.loadingContainer);
        errorContainer = view.findViewById(R.id.errorContainer);
        pageIndicatorCard = view.findViewById(R.id.pageIndicatorCard);
        tvPageIndicator = view.findViewById(R.id.tvPageIndicator);
        btnRetry = view.findViewById(R.id.btnRetry);

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> {
            saveProgress();
            navController.navigateUp();
        });

        // Inflate menu on toolbar
        toolbar.inflateMenu(R.menu.menu_book_reader);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_bookmark) {
                toggleBookmark();
                return true;
            } else if (item.getItemId() == R.id.action_share) {
                if (currentBook != null) {
                    ShareUtils.shareBook(requireContext(), currentBook.getTitle());
                }
                return true;
            } else if (item.getItemId() == R.id.action_add_note) {
                showAddNoteDialog();
                return true;
            }
            return false;
        });

        // Get book data
        currentBook = BookData.getBookById(bookId);
        if (currentBook != null) {
            toolbar.setTitle(currentBook.getTitle());
            checkBookmarkStatus();
            loadSavedProgress();
        } else {
            showError();
        }

        // Retry button
        btnRetry.setOnClickListener(v -> {
            if (currentBook != null) {
                loadPdf();
            }
        });
    }

    private void checkBookmarkStatus() {
        if (currentBook == null || userRepository == null) return;

        try {
            // Only check bookmark status if user is logged in
            if (userRepository.isLoggedIn() && !userRepository.isGuest()) {
                LiveData<Boolean> bookmarkLiveData = userRepository.isBookmarked(bookId, "book");
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
        if (currentBook == null) return;

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
                bookId,
                "book",
                currentBook.getTitle(),
                String.valueOf(currentBook.getImageRes())
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

    private void loadSavedProgress() {
        if (userRepository == null) {
            loadPdf();
            return;
        }

        try {
            userRepository.getProgress(bookId, "book", progress -> {
                if (progress != null && progress.getLastPage() > 0) {
                    startPage = progress.getLastPage();
                }
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(this::loadPdf);
                }
            });
        } catch (Exception e) {
            loadPdf();
        }
    }

    private void loadPdf() {
        if (currentBook == null) {
            showError();
            return;
        }

        showLoading();

        try {
            pdfView.fromAsset(currentBook.getPdfFileName())
                    .defaultPage(startPage)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .onPageChange(this)
                    .onLoad(this)
                    .onError(this)
                    .scrollHandle(new DefaultScrollHandle(requireContext()))
                    .spacing(8)
                    .load();
        } catch (Exception e) {
            showError();
        }
    }

    private void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        pageIndicatorCard.setVisibility(View.GONE);
    }

    private void showContent() {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        pageIndicatorCard.setVisibility(View.VISIBLE);
    }

    private void showError() {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        pageIndicatorCard.setVisibility(View.GONE);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPage = page;
        totalPages = pageCount;
        tvPageIndicator.setText(String.format("Page %d of %d", page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        totalPages = nbPages;
        showContent();
        tvPageIndicator.setText(String.format("Page %d of %d", currentPage + 1, nbPages));
    }

    @Override
    public void onError(Throwable t) {
        showError();
    }

    private void saveProgress() {
        if (currentBook == null || totalPages == 0) return;

        try {
            int percent = (int) (((float) (currentPage + 1) / totalPages) * 100);

            // Save to last read for Continue Reading feature
            lastReadManager.saveLastRead(
                bookId,
                "book",
                currentBook.getTitle(),
                currentBook.getImageRes(),
                percent
            );

            // Track reading time for reading goals
            long readingTimeMinutes = (System.currentTimeMillis() - readingStartTime) / (60 * 1000);
            if (readingTimeMinutes > 0) {
                readingGoalsManager.addReadingTime((int) readingTimeMinutes);
                readingStartTime = System.currentTimeMillis(); // Reset for next interval
            }

            // Save progress for any user with a valid session (logged in OR guest)
            if (userRepository != null && userRepository.isLoggedIn()) {
                userRepository.saveProgress(bookId, "book", percent, currentPage, totalPages);
            }
        } catch (Exception e) {
            // Ignore save errors
        }
    }

    private void showAddNoteDialog() {
        if (currentBook == null) return;

        AddNoteDialogFragment dialog = AddNoteDialogFragment.newInstance(
                bookId,
                currentBook.getTitle(),
                currentPage
        );
        dialog.setOnNoteSavedListener(() -> {
            // Note saved - could show a badge or update UI
        });
        dialog.show(getParentFragmentManager(), "AddNoteDialog");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveProgress();
    }

    @Override
    public void onDestroyView() {
        saveProgress();
        super.onDestroyView();
    }
}
