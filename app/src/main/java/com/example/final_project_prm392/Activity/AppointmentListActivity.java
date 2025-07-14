package com.example.final_project_prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project_prm392.Adapter.AppointmentAdapter;
import com.example.final_project_prm392.Domain.Appointment;
import com.example.final_project_prm392.Repository.AppointmentRepository;
import com.example.final_project_prm392.ViewModel.AppointmentViewModel;
import com.example.final_project_prm392.databinding.ActivityAppointmentListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    private AppointmentViewModel viewModel;
    private MutableLiveData<List<Appointment>> appointmentsLiveData = new MutableLiveData<>();
    private ActivityAppointmentListBinding binding;
    private AppointmentRepository appointmentRepository;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new AppointmentViewModel();

        setupUI();
        setupRecyclerView();
        observeViewModel();
        loadAppointments();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });

        appointmentsLiveData.observe(this, appointments -> {
            if (appointments.isEmpty()) {
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                adapter.submitList(appointments);
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(
                appointment -> cancelAppointment(appointment),
                appointment -> {
                    // Navigate to reschedule screen
                    // This would be implemented in a future update
                    Toast.makeText(this, "Reschedule feature coming soon", Toast.LENGTH_SHORT).show();
                }
        );

        binding.appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.appointmentsRecyclerView.setAdapter(adapter);
    }
    private void loadAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login to view appointments", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        viewModel.getAppointmentsForCurrentUser(appointmentsLiveData);
    }


    private void cancelAppointment(Appointment appointment) {
        viewModel.cancelAppointment(appointment.getId());
        // Reload appointments after cancellation
        viewModel.getAppointmentsForCurrentUser(appointmentsLiveData);
    }
}

