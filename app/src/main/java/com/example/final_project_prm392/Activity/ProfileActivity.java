package com.example.final_project_prm392.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.final_project_prm392.Domain.User;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.ViewModel.ProfileViewModel;
import com.example.final_project_prm392.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    // Binding tới layout và ViewModel
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private Uri selectedImageUri; // URI ảnh người dùng chọn
    private boolean isPasswordSectionVisible = false; // theo dõi hiển thị form đổi mật khẩu

    // Launcher để chọn ảnh từ thiết bị
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri; // lưu URI đã chọn
                    binding.profileImage.setImageURI(uri); // hiển thị lên ImageView
                    binding.changePhotoText.setText("Change Photo"); // đổi text
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo binding và set layout
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        viewModel = new ProfileViewModel();

        // Thiết lập sự kiện UI, load dữ liệu và theo dõi LiveData
        setupUI();
        loadUserProfile();
        observeViewModel();
    }

    // Thiết lập các listener cho UI
    private void setupUI() {
        // Nút Back: finish activity
        binding.backButton.setOnClickListener(v -> finish());

        // Click vào ảnh đại diện: mở picker
        binding.profileImageLayout.setOnClickListener(v ->
                getContent.launch("image/*"));

        // Nút Save: lưu thông tin profile
        binding.saveButton.setOnClickListener(v -> saveProfile());

        // Nút Logout: đăng xuất và về LoginActivity
        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Nút Change Password: toggle hiển thị form đổi mật khẩu
        binding.changePasswordButton.setOnClickListener(v -> togglePasswordSection());

        // Nút Submit New Password: thực hiện đổi mật khẩu
        binding.submitPasswordButton.setOnClickListener(v -> changePassword());
    }

    // Hiện/ẩn section đổi mật khẩu
    private void togglePasswordSection() {
        isPasswordSectionVisible = !isPasswordSectionVisible;
        int visibility = isPasswordSectionVisible ? View.VISIBLE : View.GONE;
        binding.currentPasswordInputLayout.setVisibility(visibility);
        binding.newPasswordInputLayout.setVisibility(visibility);
        binding.confirmNewPasswordInputLayout.setVisibility(visibility);
        binding.submitPasswordButton.setVisibility(visibility);

        // Nếu ẩn, xóa hết nội dung các field
        if (!isPasswordSectionVisible) {
            binding.currentPasswordEditText.setText("");
            binding.newPasswordEditText.setText("");
            binding.confirmNewPasswordEditText.setText("");
        }
    }

    // Xử lý logic đổi mật khẩu
    private void changePassword() {
        String currentPassword = binding.currentPasswordEditText.getText().toString().trim();
        String newPassword = binding.newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = binding.confirmNewPasswordEditText.getText().toString().trim();

        // Kiểm tra validation
        if (TextUtils.isEmpty(currentPassword)) {
            binding.currentPasswordEditText.setError("Current password is required");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            binding.newPasswordEditText.setError("New password is required");
            return;
        }
        if (newPassword.length() < 6) {
            binding.newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            binding.confirmNewPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Hiển thị progress và gọi ViewModel
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.changePassword(currentPassword, newPassword);
    }

    // Quan sát LiveData từ ViewModel
    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                // Nếu thành công, ẩn form đổi mật khẩu
                if (errorMessage.equals("Password updated successfully")) {
                    togglePasswordSection();
                }
            }
        });

        // Khi user data được load, cập nhật UI
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }

    // Load profile nếu user đã đăng nhập
    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        viewModel.loadUserProfile(); // gọi ViewModel lấy dữ liệu
    }

    // Cập nhật dữ liệu user lên các field
    private void updateUI(User user) {
        binding.nameEditText.setText(user.getName());
        binding.emailEditText.setText(user.getEmail());
        binding.phoneEditText.setText(user.getPhone());
        binding.medicalHistoryEditText.setText(user.getMedicalHistory());

        // Nếu có URL ảnh, load bằng Glide
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePicture())
                    .placeholder(R.drawable.placeholder_user)
                    .into(binding.profileImage);
            binding.changePhotoText.setText("Change Photo");
        }
    }

    // Lưu profile khi nhấn Save
    private void saveProfile() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String medicalHistory = binding.medicalHistoryEditText.getText().toString().trim();

        // Kiểm tra bắt buộc
        if (name.isEmpty()) {
            binding.nameEditText.setError("Name is required");
            return;
        }
        if (phone.isEmpty()) {
            binding.phoneEditText.setError("Phone is required");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        // Nếu chọn ảnh mới thì upload trước, ngược lại chỉ cập nhật thông tin
        if (selectedImageUri != null) {
            uploadImageAndSaveProfile(name, phone, medicalHistory);
        } else {
            saveProfileWithoutImage(name, phone, medicalHistory);
        }
    }

    // Upload ảnh lên Firebase Storage, sau đó lưu profile
    private void uploadImageAndSaveProfile(String name, String phone, String medicalHistory) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            viewModel.updateUserProfile(name, phone, medicalHistory, imageUrl);
                        }))
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Lưu profile khi không có ảnh mới
    private void saveProfileWithoutImage(String name, String phone, String medicalHistory) {
        viewModel.updateUserProfile(name, phone, medicalHistory, null);
    }
}
