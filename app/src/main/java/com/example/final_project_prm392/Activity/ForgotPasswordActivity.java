package com.example.final_project_prm392.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_prm392.Repository.UserRepository;
import com.example.final_project_prm392.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();

        setupUI();
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.resetPasswordButton.setOnClickListener(v -> {
            if (validateInput()) {
                resetPassword();
            }
        });
    }

    private boolean validateInput() {
        String email = binding.emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Email is required");
            return false;
        }

        return true;
    }

    private void resetPassword() {
        String email = binding.emailEditText.getText().toString().trim();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.resetPasswordButton.setEnabled(false);

        userRepository.resetPassword(email, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                binding.resetPasswordButton.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.resetPasswordButton.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}