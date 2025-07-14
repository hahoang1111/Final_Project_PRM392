package com.example.final_project_prm392.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_project_prm392.Domain.User;
import com.example.final_project_prm392.Repository.UserRepository;

public class ProfileViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private User currentUser;

    public ProfileViewModel() {
        this.repository = new UserRepository();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void loadUserProfile() {
        isLoading.setValue(true);
        repository.getCurrentUser(new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                userLiveData.postValue(user);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue("Failed to load profile: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void updateUserProfile(String name, String phone, String medicalHistory, String profilePicture) {
        if (currentUser == null) {
            errorMessage.postValue("User not found");
            return;
        }

        isLoading.setValue(true);

        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setMedicalHistory(medicalHistory);

        if (profilePicture != null) {
            currentUser.setProfilePicture(profilePicture);
        }

        repository.updateUserProfile(currentUser, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                userLiveData.postValue(currentUser);
                isLoading.postValue(false);
                errorMessage.postValue("Profile updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue("Failed to update profile: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    // Thêm phương thức đổi mật khẩu
    public void changePassword(String currentPassword, String newPassword) {
        isLoading.setValue(true);
        repository.changePassword(currentPassword, newPassword, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                errorMessage.postValue("Password updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                errorMessage.postValue(e.getMessage());
            }
        });
    }
}