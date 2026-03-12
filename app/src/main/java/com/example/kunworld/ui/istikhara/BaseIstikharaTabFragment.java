package com.example.kunworld.ui.istikhara;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.api.TransliterationService;
import com.example.kunworld.utils.AbjadCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Base fragment for all Istikhara calculator tabs.
 * Provides common functionality for name input with transliteration.
 */
public abstract class BaseIstikharaTabFragment extends Fragment {

    protected LinearLayout inputContainer;
    protected MaterialButton btnCalculate;
    protected MaterialButton btnReset;
    protected MaterialCardView cardResult;
    protected TextView tvResultTitle;
    protected TextView tvResultMain;
    protected TextView tvResultDetails;
    protected ImageView ivFeaturedImage;
    protected TextView tvTitle;
    protected LinearLayout instructionsHeader;
    protected LinearLayout instructionsContent;
    protected ImageView ivExpandIcon;
    protected boolean isInstructionsExpanded = false;

    protected TransliterationService transliterationService;
    protected List<NameInputHolder> inputHolders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_istikhara_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transliterationService = new TransliterationService();

        initViews(view);
        setupContent();
        setupInputFields();
        setupButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (transliterationService != null) {
            transliterationService.shutdown();
        }
    }

    private void initViews(View view) {
        inputContainer = view.findViewById(R.id.inputContainer);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnReset = view.findViewById(R.id.btnReset);
        cardResult = view.findViewById(R.id.cardResult);
        tvResultTitle = view.findViewById(R.id.tvResultTitle);
        tvResultMain = view.findViewById(R.id.tvResultMain);
        tvResultDetails = view.findViewById(R.id.tvResultDetails);
        ivFeaturedImage = view.findViewById(R.id.ivFeaturedImage);
        tvTitle = view.findViewById(R.id.tvTitle);
        instructionsHeader = view.findViewById(R.id.instructionsHeader);
        instructionsContent = view.findViewById(R.id.instructionsContent);
        ivExpandIcon = view.findViewById(R.id.ivExpandIcon);
    }

    private void setupContent() {
        tvTitle.setText(getTitleRes());
        ivFeaturedImage.setImageResource(getFeaturedImageRes());

        // Setup instructions toggle
        if (instructionsHeader != null) {
            instructionsHeader.setOnClickListener(v -> toggleInstructions());
        }
    }

    private void toggleInstructions() {
        isInstructionsExpanded = !isInstructionsExpanded;

        if (isInstructionsExpanded) {
            instructionsContent.setVisibility(View.VISIBLE);
            ivExpandIcon.setRotation(180f);
        } else {
            instructionsContent.setVisibility(View.GONE);
            ivExpandIcon.setRotation(0f);
        }
    }

    private void setupButtons() {
        btnCalculate.setOnClickListener(v -> onCalculate());
        btnReset.setOnClickListener(v -> onReset());
    }

    protected void onReset() {
        for (NameInputHolder holder : inputHolders) {
            holder.editText.setText("");
            holder.urduPreview.setVisibility(View.GONE);
            holder.urduPreview.setText("");
            holder.urduValue = "";
            holder.suggestionsAdapter.clear();
            holder.rvSuggestions.setVisibility(View.GONE);
        }
        cardResult.setVisibility(View.GONE);
    }

    protected NameInputHolder addNameInput(String hint) {
        View inputView = LayoutInflater.from(getContext())
                .inflate(R.layout.component_name_input, inputContainer, false);

        TextInputLayout textInputLayout = inputView.findViewById(R.id.textInputLayout);
        TextInputEditText editText = inputView.findViewById(R.id.etInput);
        RecyclerView rvSuggestions = inputView.findViewById(R.id.rvSuggestions);
        TextView tvUrduPreview = inputView.findViewById(R.id.tvUrduPreview);

        textInputLayout.setHint(hint);

        NameInputHolder holder = new NameInputHolder(editText, rvSuggestions, tvUrduPreview);
        setupNameInput(holder);

        inputContainer.addView(inputView);
        inputHolders.add(holder);

        return holder;
    }

    private void setupNameInput(NameInputHolder holder) {
        holder.suggestionsAdapter = new SuggestionsAdapter(suggestion -> {
            // Update the input field with selected suggestion
            String currentText = holder.editText.getText() != null ?
                    holder.editText.getText().toString() : "";
            String[] words = currentText.split("\\s+");
            words[words.length - 1] = suggestion;
            String newText = String.join(" ", words) + " ";

            holder.editText.setText(newText);
            holder.editText.setSelection(newText.length());
            holder.urduValue = newText.trim();
            holder.urduPreview.setText(holder.urduValue);
            holder.urduPreview.setVisibility(View.VISIBLE);
            holder.suggestionsAdapter.clear();
            holder.rvSuggestions.setVisibility(View.GONE);
        });

        holder.rvSuggestions.setAdapter(holder.suggestionsAdapter);

        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                // Check if typing in Urdu directly
                if (AbjadCalculator.isUrduText(text)) {
                    holder.urduValue = text;
                    holder.urduPreview.setText(text);
                    holder.urduPreview.setVisibility(View.VISIBLE);
                    holder.suggestionsAdapter.clear();
                    holder.rvSuggestions.setVisibility(View.GONE);
                    return;
                }

                // English - transliterate last word
                if (text.length() > 0) {
                    String[] words = text.split("\\s+");
                    String lastWord = words[words.length - 1];

                    if (lastWord.length() > 0) {
                        transliterationService.transliterate(lastWord,
                                new TransliterationService.TransliterationCallback() {
                                    @Override
                                    public void onSuccess(List<String> suggestions) {
                                        if (suggestions.isEmpty()) {
                                            holder.suggestionsAdapter.clear();
                                            holder.rvSuggestions.setVisibility(View.GONE);
                                        } else {
                                            holder.suggestionsAdapter.setSuggestions(suggestions);
                                            holder.rvSuggestions.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        holder.suggestionsAdapter.clear();
                                        holder.rvSuggestions.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else {
                    holder.suggestionsAdapter.clear();
                    holder.rvSuggestions.setVisibility(View.GONE);
                    holder.urduPreview.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void showResult(String title, String mainResult, String details) {
        tvResultTitle.setText(title);
        tvResultMain.setText(mainResult);
        tvResultDetails.setText(details);
        cardResult.setVisibility(View.VISIBLE);

        // Scroll to result
        cardResult.post(() -> {
            if (getView() != null) {
                getView().findViewById(R.id.cardResult).getParent().requestChildFocus(
                        cardResult, cardResult);
            }
        });
    }

    protected void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected boolean validateInputs(NameInputHolder... holders) {
        for (NameInputHolder holder : holders) {
            if (holder.urduValue == null || holder.urduValue.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Abstract methods to be implemented by subclasses
    @StringRes
    protected abstract int getTitleRes();

    @DrawableRes
    protected abstract int getFeaturedImageRes();

    protected abstract void setupInputFields();

    protected abstract void onCalculate();

    // Helper class to hold input components
    protected static class NameInputHolder {
        TextInputEditText editText;
        RecyclerView rvSuggestions;
        TextView urduPreview;
        SuggestionsAdapter suggestionsAdapter;
        String urduValue = "";

        NameInputHolder(TextInputEditText editText, RecyclerView rvSuggestions, TextView urduPreview) {
            this.editText = editText;
            this.rvSuggestions = rvSuggestions;
            this.urduPreview = urduPreview;
        }
    }

    // Simple adapter for suggestions
    private static class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {
        private List<String> suggestions = new ArrayList<>();
        private final OnSuggestionClickListener listener;

        interface OnSuggestionClickListener {
            void onClick(String suggestion);
        }

        SuggestionsAdapter(OnSuggestionClickListener listener) {
            this.listener = listener;
        }

        void setSuggestions(List<String> newSuggestions) {
            this.suggestions = new ArrayList<>(newSuggestions);
            notifyDataSetChanged();
        }

        void clear() {
            this.suggestions.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_suggestion, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String suggestion = suggestions.get(position);
            holder.tvSuggestion.setText(suggestion);
            holder.itemView.setOnClickListener(v -> listener.onClick(suggestion));
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSuggestion;

            ViewHolder(View view) {
                super(view);
                tvSuggestion = view.findViewById(R.id.tvSuggestion);
            }
        }
    }
}
