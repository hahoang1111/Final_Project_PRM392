package com.example.final_project_prm392.Repository;

import androidx.annotation.NonNull;

import com.example.final_project_prm392.Domain.DoctorsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private final FirebaseDatabase database;
    private final DatabaseReference doctorsRef;

    public DoctorRepository() {
        this.database = FirebaseDatabase.getInstance();
        this.doctorsRef = database.getReference("Doctors");
    }

    public interface DoctorsCallback {
        void onSuccess(List<DoctorsModel> doctors);

        void onFailure(Exception e);
    }

    public interface DoctorCallback {
        void onSuccess(DoctorsModel doctor);

        void onFailure(Exception e);
    }

    public void getAllDoctors(DoctorsCallback callback) {
        doctorsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    List<DoctorsModel> doctors = new ArrayList<>();
                    for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                        DoctorsModel doctor = childSnapshot.getValue(DoctorsModel.class);
                        if (doctor != null) {
                            doctors.add(doctor);
                        }
                    }
                    callback.onSuccess(doctors);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void getDoctorsBySpecialty(String specialty, DoctorsCallback callback) {
        doctorsRef.orderByChild("Special").equalTo(specialty).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DoctorsModel> doctors = new ArrayList<>();
                            for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                                DoctorsModel doctor = childSnapshot.getValue(DoctorsModel.class);
                                if (doctor != null) {
                                    doctors.add(doctor);
                                }
                            }
                            callback.onSuccess(doctors);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getDoctorById(int doctorId, DoctorCallback callback) {
        doctorsRef.orderByChild("Id").equalTo(doctorId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                                DoctorsModel doctor = childSnapshot.getValue(DoctorsModel.class);
                                if (doctor != null) {
                                    callback.onSuccess(doctor);
                                    return;
                                }
                            }
                            callback.onFailure(new Exception("Doctor not found"));
                        } else {
                            callback.onFailure(new Exception("Doctor not found"));
                        }
                    }
                });
    }
}

