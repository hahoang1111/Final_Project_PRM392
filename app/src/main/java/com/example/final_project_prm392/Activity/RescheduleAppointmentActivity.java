package com.example.final_project_prm392.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_project_prm392.Domain.Appointment;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.Repository.AppointmentRepository;
import com.example.final_project_prm392.databinding.ActivityRescheduleAppointmentBinding;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RescheduleAppointmentActivity extends AppCompatActivity {

    private ActivityRescheduleAppointmentBinding binding;
    private AppointmentRepository appointmentRepository;
    private Date selectedDate = null;
    private String selectedTimeSlot = null;

    private final List<String> timeSlots = Arrays.asList(
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRescheduleAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appointmentRepository = new AppointmentRepository();

        setupDatePicker();
        setupTimeSlots();
        setupRescheduleButton();
    }

    private void setupDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        binding.datePickerButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RescheduleAppointmentActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Set time to beginning of day
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        selectedDate = calendar.getTime();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
                        binding.selectedDate.setText(dateFormat.format(selectedDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Set min date to today
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            // Set max date to 30 days from now
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 30);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            datePickerDialog.show();
        });
    }

    private void setupTimeSlots() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeSlots);
        binding.timeSlotSpinner.setAdapter(adapter);

        binding.timeSlotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeSlot = timeSlots.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTimeSlot = null;
            }
        });
    }

    private void setupRescheduleButton() {
        binding.rescheduleButton.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTimeSlot == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the appointment ID from the intent
            String appointmentId = getIntent().getStringExtra("APPOINTMENT_ID");

            // Update the appointment with the new date and time
            appointmentRepository.rescheduleAppointment(appointmentId, selectedDate, selectedTimeSlot, new AppointmentRepository.AppointmentCallback() {
                @Override
                public void onSuccess(Appointment appointment) {
                    Toast.makeText(RescheduleAppointmentActivity.this, "Appointment rescheduled successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, update the UI or navigate back to the previous screen
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(RescheduleAppointmentActivity.this, "Failed to reschedule appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}