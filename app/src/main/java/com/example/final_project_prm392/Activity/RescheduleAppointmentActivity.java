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

    // Danh sách các khung giờ cố định cho cuộc hẹn.
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

    /**
     * Thiết lập chức năng chọn ngày cho lịch hẹn.
     * Mở DatePickerDialog khi nhấn nút, cho phép người dùng chọn ngày.
     * Giới hạn ngày có thể chọn: từ hôm nay đến 30 ngày sau.
     */
    private void setupDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        binding.datePickerButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RescheduleAppointmentActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Đặt thời gian về đầu ngày (00:00:00.000).
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        selectedDate = calendar.getTime();

                        // Định dạng ngày và hiển thị lên TextView.
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
                        binding.selectedDate.setText(dateFormat.format(selectedDate));
                    },
                    // Đặt ngày khởi tạo của DatePickerDialog là ngày hiện tại.
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Đặt ngày tối thiểu có thể chọn là hôm nay.
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            // Đặt ngày tối đa có thể chọn là 30 ngày kể từ bây giờ.
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 30);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            datePickerDialog.show();
        });
    }

    /**
     * Thiết lập Spinner để chọn khung giờ.
     * Điền dữ liệu từ danh sách timeSlots vào Spinner và xử lý sự kiện chọn.
     */
    private void setupTimeSlots() {
        // Tạo ArrayAdapter để hiển thị danh sách khung giờ trong Spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeSlots);
        binding.timeSlotSpinner.setAdapter(adapter);

        binding.timeSlotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lưu khung giờ đã chọn.
                selectedTimeSlot = timeSlots.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTimeSlot = null;
            }
        });
    }

    /**
     * Thiết lập chức năng cho nút "Reschedule".
     * Kiểm tra xem ngày và khung giờ đã được chọn chưa, sau đó gọi repository để cập nhật lịch hẹn.
     */
    private void setupRescheduleButton() {
        // Đặt lắng nghe sự kiện click cho nút đặt lại lịch.
        binding.rescheduleButton.setOnClickListener(v -> {
            // Kiểm tra nếu ngày chưa được chọn, hiển thị Toast và thoát
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra nếu khung giờ chưa được chọn, hiển thị Toast và thoát.
            if (selectedTimeSlot == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy ID của lịch hẹn từ Intent. ID này phải được truyền vào Activity này.
            String appointmentId = getIntent().getStringExtra("APPOINTMENT_ID");

            // Cập nhật lịch hẹn với ngày và giờ mới thông qua AppointmentRepository.
            appointmentRepository.rescheduleAppointment(appointmentId, selectedDate, selectedTimeSlot, new AppointmentRepository.AppointmentCallback() {
                @Override
                public void onSuccess(Appointment appointment) {
                    Toast.makeText(RescheduleAppointmentActivity.this, "Appointment rescheduled successfully", Toast.LENGTH_SHORT).show();
                    // Tùy chọn: cập nhật UI hoặc quay lại màn hình trước.
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    // Hiển thị thông báo lỗi nếu việc đặt lại lịch hẹn thất bại.
                    Toast.makeText(RescheduleAppointmentActivity.this, "Failed to reschedule appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}