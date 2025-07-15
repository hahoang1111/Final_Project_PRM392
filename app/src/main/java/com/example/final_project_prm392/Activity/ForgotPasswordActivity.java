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

    private ActivityForgotPasswordBinding binding; // Đối tượng binding cho layout
    private UserRepository userRepository; // Repository cho hoạt động người dùng

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
    /**
     * Thiết lập giao diện người dùng và lắng nghe sự kiện.
     */
    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.resetPasswordButton.setOnClickListener(v -> {
            if (validateInput()) {// Xác thực đầu vào
                resetPassword(); // Đặt lại mật khẩu
            }
        });
    }
    /**
     * Xác thực email nhập vào.
     * @return true nếu hợp lệ, false nếu không.
     */
    private boolean validateInput() {
        String email = binding.emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Email is required"); // Lỗi email trống
            return false;
        }

        return true;
    }
    /**
     * Thực hiện quá trình đặt lại mật khẩu.
     */
    private void resetPassword() {
        String email = binding.emailEditText.getText().toString().trim();

        binding.progressBar.setVisibility(View.VISIBLE); // Hiển thị thanh tiến trình
        binding.resetPasswordButton.setEnabled(false); // Vô hiệu hóa nút

        userRepository.resetPassword(email, new UserRepository.OperationCallback() {
            /**
             * Được gọi khi liên kết đặt lại mật khẩu được gửi thành công.
             */
            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE); // Ẩn thanh tiến trình
                binding.resetPasswordButton.setEnabled(true); // Kích hoạt lại nút
                Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show(); // Thông báo thành công
                finish();
            }

            /**
             * Được gọi khi có lỗi trong việc gửi liên kết đặt lại mật khẩu.
             */
            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE); // Ẩn thanh tiến trình
                binding.resetPasswordButton.setEnabled(true); // Kích hoạt lại nút
                Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Thông báo lỗi
            }
        });
    }
}