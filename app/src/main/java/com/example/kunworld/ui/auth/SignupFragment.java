package com.example.kunworld.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.kunworld.R;
import com.example.kunworld.databinding.FragmentSignupBinding;

public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;
    private AuthViewModel viewModel;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupInputValidation();
        setupClickListeners();
        observeViewModel();
    }

    private void setupInputValidation() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilName.setError(null);
                viewModel.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilEmail.setError(null);
                viewModel.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPassword.setError(null);
                viewModel.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilConfirmPassword.setError(null);
                viewModel.clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSignup();
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> navController.popBackStack());

        binding.btnSignUp.setOnClickListener(v -> attemptSignup());

        binding.btnLogin.setOnClickListener(v -> navController.popBackStack());
    }

    private void attemptSignup() {
        String name = binding.etName.getText() != null ?
                binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ?
                binding.etEmail.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ?
                binding.etPhone.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ?
                binding.etPassword.getText().toString() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null ?
                binding.etConfirmPassword.getText().toString() : "";

        // Validate inputs
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.tilName.setError(getString(R.string.error_name_required));
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_password_short));
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError(getString(R.string.error_confirm_password_required));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            isValid = false;
        }

        if (isValid) {
            viewModel.register(name, email, password, confirmPassword, phone.isEmpty() ? null : phone);
        }
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSignUp.setEnabled(!isLoading);
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnBack.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getSignupSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.clearSignupSuccess();
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        navController.navigate(R.id.action_signup_to_home);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
