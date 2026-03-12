package com.example.kunworld.ui.consultancy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kunworld.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class BookingDialogFragment extends DialogFragment {

    private static final String EMAIL_ADDRESS = "info@kunworld.com";

    private TextInputLayout tilName, tilEmail, tilPhone, tilMessage;
    private TextInputEditText etName, etEmail, etPhone, etMessage;
    private MaterialButton btnCancel, btnSubmit;
    private TextView tvServiceName;

    private String serviceName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Theme_KunWorld_Dialog);

        if (getArguments() != null) {
            serviceName = getArguments().getString("serviceName", "Consultation");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();

        // Set service name
        tvServiceName.setText(serviceName);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void initViews(View view) {
        tilName = view.findViewById(R.id.tilName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilMessage = view.findViewById(R.id.tilMessage);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etMessage = view.findViewById(R.id.etMessage);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvServiceName = view.findViewById(R.id.tvServiceName);
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> dismiss());

        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitBooking();
            }
        });

        // Clear errors on focus
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilName.setError(null);
        });
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilEmail.setError(null);
        });
        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilPhone.setError(null);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = getText(etName);
        String email = getText(etEmail);
        String phone = getText(etPhone);

        // Validate name
        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.error_name_required));
            isValid = false;
        } else if (name.length() < 2) {
            tilName.setError("Name must be at least 2 characters");
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
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

        return isValid;
    }

    private void submitBooking() {
        String name = getText(etName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String message = getText(etMessage);

        // Build email body
        StringBuilder body = new StringBuilder();
        body.append("New Consultation Request\n");
        body.append("========================\n\n");
        body.append("Service: ").append(serviceName).append("\n\n");
        body.append("Contact Information:\n");
        body.append("Name: ").append(name).append("\n");
        body.append("Email: ").append(email).append("\n");
        body.append("Phone: ").append(phone).append("\n\n");

        if (!TextUtils.isEmpty(message)) {
            body.append("Message:\n").append(message).append("\n");
        }

        body.append("\n---\n");
        body.append("Sent from KunWorld App");

        // Create email intent
        String subject = "Consultation Request: " + serviceName;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + EMAIL_ADDRESS));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body.toString());

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
            dismiss();
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
                dismiss();
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
