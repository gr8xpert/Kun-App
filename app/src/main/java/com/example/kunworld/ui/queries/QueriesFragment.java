package com.example.kunworld.ui.queries;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class QueriesFragment extends Fragment {

    private static final String EMAIL_ADDRESS = "info@kunworld.com";

    private NavController navController;

    private TextInputLayout tilName, tilEmail, tilPhone, tilQuery;
    private TextInputEditText etName, etEmail, etPhone, etQuery;
    private MaterialButton btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        tilName = view.findViewById(R.id.tilName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilQuery = view.findViewById(R.id.tilQuery);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etQuery = view.findViewById(R.id.etQuery);

        btnSubmit = view.findViewById(R.id.btnSubmit);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitQuery();
            }
        });

        // Clear errors on focus
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && tilName != null) tilName.setError(null);
        });
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && tilEmail != null) tilEmail.setError(null);
        });
        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && tilPhone != null) tilPhone.setError(null);
        });
        etQuery.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && tilQuery != null) tilQuery.setError(null);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = getText(etName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String query = getText(etQuery);

        // Validate name
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name is required");
            isValid = false;
        } else if (name.length() < 2) {
            tilName.setError("Name must be at least 2 characters");
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            tilPhone.setError("Please enter a valid phone number");
            isValid = false;
        }

        // Validate query
        if (TextUtils.isEmpty(query)) {
            tilQuery.setError("Please enter your query");
            isValid = false;
        } else if (query.length() < 10) {
            tilQuery.setError("Please provide more details (at least 10 characters)");
            isValid = false;
        }

        return isValid;
    }

    private void submitQuery() {
        String name = getText(etName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String query = getText(etQuery);

        // Build email body
        StringBuilder body = new StringBuilder();
        body.append("New Query from KunWorld App\n");
        body.append("===========================\n\n");
        body.append("Contact Information:\n");
        body.append("Name: ").append(name).append("\n");
        body.append("Email: ").append(email).append("\n");
        body.append("Phone: ").append(phone).append("\n\n");
        body.append("Query:\n").append(query).append("\n");
        body.append("\n---\n");
        body.append("Sent from KunWorld App");

        // Create email intent
        String subject = "Query from " + name + " - KunWorld App";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + EMAIL_ADDRESS));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body.toString());

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
            Toast.makeText(getContext(), "Opening email app...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Fallback: try opening any email app
            Intent fallbackIntent = new Intent(Intent.ACTION_SEND);
            fallbackIntent.setType("message/rfc822");
            fallbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_ADDRESS});
            fallbackIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            fallbackIntent.putExtra(Intent.EXTRA_TEXT, body.toString());

            try {
                startActivity(Intent.createChooser(fallbackIntent, "Send Email"));
            } catch (Exception ex) {
                Toast.makeText(getContext(),
                        "No email app found. Please email us at " + EMAIL_ADDRESS,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
