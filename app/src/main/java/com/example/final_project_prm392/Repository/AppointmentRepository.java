package com.example.final_project_prm392.Repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.final_project_prm392.Domain.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AppointmentRepository {
    private final FirebaseFirestore firestore;
    private final String COLLECTION_NAME = "appointments";

    public AppointmentRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void rescheduleAppointment(String appointmentId, Date newDate, String newTimeSlot, AppointmentCallback callback) {
        DocumentReference documentRef = firestore.collection(COLLECTION_NAME).document(appointmentId);
        documentRef.update("date", newDate, "time", newTimeSlot)
                .addOnSuccessListener(aVoid -> {
                    documentRef.get().addOnSuccessListener(documentSnapshot -> {
                        Appointment updatedAppointment = documentSnapshot.toObject(Appointment.class);
                        callback.onSuccess(updatedAppointment);
                    });
                    Log.d("AppointmentRepository", "Appointment rescheduled successfully");
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    Log.e("AppointmentRepository", "Failed to reschedule appointment", e);
                });
    }

    public interface AppointmentCallback {
        void onSuccess(Appointment appointment);

        void onFailure(Exception e);
    }

    public interface AppointmentsCallback {
        void onSuccess(List<Appointment> appointments);

        void onFailure(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    public void createAppointment(Appointment appointment, AppointmentCallback callback) {
        DocumentReference documentRef = firestore.collection(COLLECTION_NAME).document();
        appointment.setId(documentRef.getId());

        documentRef.set(appointment)
                .addOnSuccessListener(aVoid -> callback.onSuccess(appointment))
                .addOnFailureListener(callback::onFailure);
    }

    public void getAppointmentsForUser(String userId, AppointmentsCallback callback) {
        firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Appointment> appointments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointments.add(appointment);
                            }
                            callback.onSuccess(appointments);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getUpcomingAppointmentsForUser(String userId, AppointmentsCallback callback) {
        Date currentDate = new Date();
        List<String> statusList = Arrays.asList("pending", "confirmed");

        firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", currentDate)
                .whereIn("status", statusList)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Appointment> appointments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointments.add(appointment);
                            }
                            callback.onSuccess(appointments);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void updateAppointmentStatus(String appointmentId, String status, OperationCallback callback) {
        firestore.collection(COLLECTION_NAME).document(appointmentId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteAppointment(String appointmentId, OperationCallback callback) {
        firestore.collection(COLLECTION_NAME).document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}

