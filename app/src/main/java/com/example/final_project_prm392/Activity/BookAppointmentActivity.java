package com.example.final_project_prm392.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.final_project_prm392.Domain.Appointment;
import com.example.final_project_prm392.Domain.DoctorsModel;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.Repository.AppointmentRepository;
import com.example.final_project_prm392.Repository.DoctorRepository;
import com.example.final_project_prm392.Repository.UserRepository;
import com.example.final_project_prm392.databinding.ActivityBookAppointmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private DoctorRepository doctorRepository;
    private AppointmentRepository appointmentRepository;
    private UserRepository userRepository;

    private int doctorId = 0;
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
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        doctorRepository = new DoctorRepository();
        appointmentRepository = new AppointmentRepository();
        userRepository = new UserRepository();

        doctorId = getIntent().getIntExtra("DOCTOR_ID", 0);

        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to book an appointment", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupUI();
        loadDoctorDetails();
        setupDatePicker();
        setupTimeSlots();
        setupSubmitButton();
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void loadDoctorDetails() {
        doctorRepository.getDoctorById(doctorId, new DoctorRepository.DoctorCallback() {
            @Override
            public void onSuccess(DoctorsModel doctor) {
                updateDoctorUI(doctor);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(BookAppointmentActivity.this, "Failed to load doctor details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDoctorUI(DoctorsModel doctor) {
        binding.doctorName.setText(doctor.getName());
        binding.doctorSpecialty.setText(doctor.getSpecial());

        Glide.with(this)
                .load(doctor.getPicture())
                .placeholder(R.drawable.placeholder_doctor)
                .into(binding.doctorImage);
    }

    private void setupDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        binding.datePickerButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BookAppointmentActivity.this,
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
                        binding.selectedDate.setVisibility(View.VISIBLE);
                        binding.timeSlotsLayout.setVisibility(View.VISIBLE);
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

    private void setupSubmitButton() {
        binding.bookAppointmentButton.setOnClickListener(v -> {
            if (validateInputs()) {
                bookAppointment();
            }
        });
    }

    private boolean validateInputs() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedTimeSlot == null) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            return false;
        }

        String notes = binding.notesEditText.getText().toString();
        if (notes.length() > 500) {
            Toast.makeText(this, "Notes are too long", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void bookAppointment() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login to book an appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctorId);
        appointment.setUserId(currentUser.getUid());
        appointment.setDate(selectedDate);
        appointment.setTime(selectedTimeSlot);
        appointment.setNotes(binding.notesEditText.getText().toString());
        appointment.setService(binding.serviceEditText.getText().toString());
        appointment.setStatus("pending");
        appointment.setCreatedAt(new Date());

        binding.progressBar.setVisibility(View.VISIBLE);

        appointmentRepository.createAppointment(appointment, new AppointmentRepository.AppointmentCallback() {
            @Override
            public void onSuccess(Appointment createdAppointment) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

