package com.example.final_project_prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_prm392.Domain.User;
import com.example.final_project_prm392.Repository.UserRepository;
import com.example.final_project_prm392.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Khởi tạo Firebase và App Check
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // 2. Thiết lập ViewBinding thay vì findViewById
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. Khởi tạo repository xử lý đăng nhập
        userRepository = new UserRepository();

        // 4. Thiết lập UI (gán sự kiện cho nút bấm, link)
        setupUI();
    }

    private void setupUI() {
        // Khi nhấn nút Login → kiểm tra input → gọi loginUser()
        binding.loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser();
            }
        });

        // Link sang màn Register
        binding.registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        // Link sang màn Quên mật khẩu
        binding.forgotPasswordTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    // Kiểm tra email & password không để trống
    private boolean validateInputs() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }

    // Thực hiện đăng nhập thông qua UserRepository
    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Hiển thị ProgressBar và disable nút để tránh bấm nhiều lần
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);

        // Gọi repository, truyền callback để xử lý kết quả
        userRepository.loginUser(email, password, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // 1. Ẩn Progress, enable nút
                binding.progressBar.setVisibility(View.GONE);
                binding.loginButton.setEnabled(true);
                // 2. Thông báo thành công
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                // 3. Chuyển sang MainActivity, xóa back stack
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("USER_NAME", user.getName());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // Hiển thị lại UI bình thường và show lỗi
                binding.progressBar.setVisibility(View.GONE);
                binding.loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


