package com.example.final_project_prm392.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_prm392.Activity.RescheduleAppointmentActivity;
import com.example.final_project_prm392.Domain.Appointment;
import com.example.final_project_prm392.Domain.DoctorsModel;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.Repository.DoctorRepository;
import com.example.final_project_prm392.databinding.ItemAppointmentBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder> {

    private final DoctorRepository doctorRepository;
    private final AppointmentClickListener cancelListener;
    private final AppointmentClickListener rescheduleListener;

    public interface AppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentAdapter(AppointmentClickListener cancelListener, AppointmentClickListener rescheduleListener) {
        super(new AppointmentDiffCallback());
        this.doctorRepository = new DoctorRepository();
        this.cancelListener = cancelListener;
        this.rescheduleListener = rescheduleListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentBinding binding = ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AppointmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = getItem(position);
        holder.bind(appointment);
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private final ItemAppointmentBinding binding;

        public AppointmentViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Appointment appointment) {
            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
            binding.appointmentDate.setText(dateFormat.format(appointment.getDate()));

            // Set time
            binding.appointmentTime.setText(appointment.getTime());

            // Set status with appropriate color
            binding.appointmentStatus.setText(capitalize(appointment.getStatus()));
            switch (appointment.getStatus()) {
                case "pending":
                    binding.appointmentStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.orange));
                    break;
                case "confirmed":
                    binding.appointmentStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.green));
                    break;
                case "completed":
                    binding.appointmentStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.blue));
                    break;
                case "cancelled":
                    binding.appointmentStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.red));
                    break;
            }

            // Load doctor information
            loadDoctorInfo(appointment.getDoctorId());

            // Set service
            if (appointment.getService() != null && !appointment.getService().isEmpty()) {
                binding.appointmentService.setText(appointment.getService());
                binding.appointmentService.setVisibility(View.VISIBLE);
            } else {
                binding.appointmentService.setVisibility(View.GONE);
            }

            // Set notes
            if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
                binding.appointmentNotes.setText(appointment.getNotes());
                binding.appointmentNotes.setVisibility(View.VISIBLE);
            } else {
                binding.appointmentNotes.setVisibility(View.GONE);
            }

            // Set button visibility based on status
            if ("pending".equals(appointment.getStatus()) || "confirmed".equals(appointment.getStatus())) {
                binding.cancelButton.setVisibility(View.VISIBLE);
                binding.rescheduleButton.setVisibility(View.VISIBLE);
            } else {
                binding.cancelButton.setVisibility(View.GONE);
                binding.rescheduleButton.setVisibility(View.GONE);
            }

            // Set click listeners
            binding.cancelButton.setOnClickListener(v -> cancelListener.onAppointmentClick(appointment));
            binding.rescheduleButton.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), RescheduleAppointmentActivity.class);
                intent.putExtra("APPOINTMENT_ID", appointment.getId());
                binding.getRoot().getContext().startActivity(intent);
            });
        }

        private void loadDoctorInfo(int doctorId) {
            binding.doctorName.setText("Loading...");
            binding.doctorSpecialty.setText("");

            doctorRepository.getDoctorById(doctorId, new DoctorRepository.DoctorCallback() {
                @Override
                public void onSuccess(DoctorsModel doctor) {
                    binding.doctorName.setText(doctor.getName());
                    binding.doctorSpecialty.setText(doctor.getSpecial());
                }

                @Override
                public void onFailure(Exception e) {
                    binding.doctorName.setText("Unknown Doctor");
                    binding.doctorSpecialty.setText("");
                }
            });
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    static class AppointmentDiffCallback extends DiffUtil.ItemCallback<Appointment> {
        @Override
        public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.equals(newItem);
        }
    }
}

