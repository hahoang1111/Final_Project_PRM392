// AppointmentViewModel.java
package com.example.final_project_prm392.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_project_prm392.Domain.Appointment;
import com.example.final_project_prm392.Repository.AppointmentRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AppointmentViewModel extends ViewModel {
    private final AppointmentRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AppointmentViewModel() {
        this.repository = new AppointmentRepository();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createAppointment(Appointment appointment) {
        isLoading.setValue(true);
        repository.createAppointment(appointment, new AppointmentRepository.AppointmentCallback() {
            @Override
            public void onSuccess(Appointment createdAppointment) {
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void getAppointmentsForCurrentUser(MutableLiveData<List<Appointment>> appointmentsLiveData) {
        isLoading.setValue(true);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        repository.getAppointmentsForUser(userId, new AppointmentRepository.AppointmentsCallback() {
            @Override
            public void onSuccess(List<Appointment> appointments) {
                appointmentsLiveData.postValue(appointments);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void cancelAppointment(String appointmentId) {
        isLoading.setValue(true);
        repository.updateAppointmentStatus(appointmentId, "cancelled", new AppointmentRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                isLoading.postValue(false);
                errorMessage.postValue(e.getMessage());
            }
        });
    }
}