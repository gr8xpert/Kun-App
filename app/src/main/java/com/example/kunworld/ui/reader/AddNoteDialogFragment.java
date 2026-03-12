package com.example.kunworld.ui.reader;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kunworld.R;
import com.example.kunworld.utils.NotesManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Dialog for adding notes while reading a book.
 */
public class AddNoteDialogFragment extends DialogFragment {

    private static final String ARG_BOOK_ID = "bookId";
    private static final String ARG_BOOK_TITLE = "bookTitle";
    private static final String ARG_PAGE_NUMBER = "pageNumber";

    private String bookId;
    private String bookTitle;
    private int pageNumber;
    private int selectedColor = R.color.highlight_yellow;

    private NotesManager notesManager;
    private OnNoteSavedListener listener;

    public interface OnNoteSavedListener {
        void onNoteSaved();
    }

    public static AddNoteDialogFragment newInstance(String bookId, String bookTitle, int pageNumber) {
        AddNoteDialogFragment fragment = new AddNoteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        args.putString(ARG_BOOK_TITLE, bookTitle);
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnNoteSavedListener(OnNoteSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
            bookTitle = getArguments().getString(ARG_BOOK_TITLE);
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER, 0);
        }
        notesManager = new NotesManager(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_note, null);

        TextView tvPageNumber = view.findViewById(R.id.tvPageNumber);
        TextInputEditText etNote = view.findViewById(R.id.etNote);

        tvPageNumber.setText(getString(R.string.page_label, pageNumber + 1));

        // Color selection
        MaterialCardView colorYellow = view.findViewById(R.id.colorYellow);
        MaterialCardView colorGreen = view.findViewById(R.id.colorGreen);
        MaterialCardView colorBlue = view.findViewById(R.id.colorBlue);
        MaterialCardView colorPink = view.findViewById(R.id.colorPink);

        View.OnClickListener colorClickListener = v -> {
            // Reset all strokes
            colorYellow.setStrokeWidth(0);
            colorGreen.setStrokeWidth(0);
            colorBlue.setStrokeWidth(0);
            colorPink.setStrokeWidth(0);

            // Set selected
            ((MaterialCardView) v).setStrokeWidth(dpToPx(2));

            if (v.getId() == R.id.colorYellow) {
                selectedColor = R.color.highlight_yellow;
            } else if (v.getId() == R.id.colorGreen) {
                selectedColor = R.color.highlight_green;
            } else if (v.getId() == R.id.colorBlue) {
                selectedColor = R.color.highlight_blue;
            } else if (v.getId() == R.id.colorPink) {
                selectedColor = R.color.highlight_pink;
            }
        };

        colorYellow.setOnClickListener(colorClickListener);
        colorGreen.setOnClickListener(colorClickListener);
        colorBlue.setOnClickListener(colorClickListener);
        colorPink.setOnClickListener(colorClickListener);

        // Buttons
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String noteText = etNote.getText() != null ? etNote.getText().toString().trim() : "";

            if (noteText.isEmpty()) {
                etNote.setError("Please enter a note");
                return;
            }

            notesManager.saveNote(bookId, bookTitle, null, noteText, pageNumber, selectedColor,
                    new NotesManager.SaveCallback() {
                        @Override
                        public void onSuccess(com.example.kunworld.data.models.Note note) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.onNoteSaved();
                                    }
                                    dismiss();
                                });
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    });
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
