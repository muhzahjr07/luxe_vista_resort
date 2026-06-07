package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ServiceDetailActivity extends BaseActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private long serviceId;
    private long bookingId = -1;
    private boolean isEditMode = false;
    private Service service;
    private TextView tvServiceName, tvServiceDescription, tvServicePrice, tvServiceAvailability, tvServiceDuration, tvServiceCategory;
    private Button btnSelectDate, btnSelectTime, btnBookService;
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_detail);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to book services", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        serviceId = getIntent().getLongExtra("service_id", -1);
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        bookingId = getIntent().getLongExtra("booking_id", -1);
        
        if (serviceId == -1) {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadServiceDetails();
        setupViews();
    }

    private void loadServiceDetails() {
        Cursor cursor = db.getServiceById(serviceId);
        if (cursor.moveToFirst()) {
            service = new Service(
                cursor.getLong(0),        // id
                cursor.getString(1),      // name
                cursor.getString(2),      // description
                cursor.getString(4),      // availability
                cursor.getDouble(3),      // price
                cursor.getString(5),      // duration
                cursor.getString(6)       // category
            );
        }
        cursor.close();
    }

    private void setupViews() {
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceDescription = findViewById(R.id.tvServiceDescription);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvServiceAvailability = findViewById(R.id.tvServiceAvailability);
        tvServiceDuration = findViewById(R.id.tvServiceDuration);
        tvServiceCategory = findViewById(R.id.tvServiceCategory);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnBookService = findViewById(R.id.btnBookService);

        if (service != null) {
            tvServiceName.setText(service.name);
            tvServiceDescription.setText(service.description);
            tvServicePrice.setText(formatPrice(service.price));
            tvServiceAvailability.setText("🕘 " + service.availability);
            tvServiceDuration.setText("⏱ Duration: " + service.duration);
            tvServiceCategory.setText("Category: " + service.category);
        }

        // Set default time to next hour
        selectedTime.add(Calendar.HOUR_OF_DAY, 1);
        selectedTime.set(Calendar.MINUTE, 0);
        updateDateTimeButtons();

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        
        if (isEditMode) {
            btnBookService.setText("Update Booking");
            loadBookingDetails();
        } else {
            btnBookService.setText("Book This Service");
        }
        
        btnBookService.setOnClickListener(v -> {
            if (isEditMode) {
                updateBooking();
            } else {
                bookService();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateDateTimeButtons();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                updateDateTimeButtons();
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeButtons() {
        btnSelectDate.setText("Date: " + dateFormat.format(selectedDate.getTime()));
        btnSelectTime.setText("Time: " + timeFormat.format(selectedTime.getTime()));
    }

    private void bookService() {
        if (service == null) return;

        // Check if selected date is in the past
        Calendar now = Calendar.getInstance();
        if (selectedDate.before(now) || (selectedDate.equals(now) && selectedTime.before(now))) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
                return;
            }

        long bookingId = db.addServiceBooking(
            session.getUserId(),
            serviceId,
            dateFormat.format(selectedDate.getTime()),
            timeFormat.format(selectedTime.getTime()),
            service.price
        );

        if (bookingId > 0) {
            Toast.makeText(this, 
                String.format("Service booked successfully! Total: %s", formatPrice(service.price)), 
                Toast.LENGTH_LONG).show();
            
            // Show notification
            NotificationHelper.showBookingConfirmation(this, 
                "Service Booking Confirmed", 
                String.format("Your %s appointment has been confirmed for %s at %s", 
                    service.name, dateFormat.format(selectedDate.getTime()), timeFormat.format(selectedTime.getTime())));
            
            finish();
        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookingDetails() {
        if (bookingId == -1) return;
        
        Cursor cursor = db.getServiceBookingById(bookingId);
        if (cursor.moveToFirst()) {
            String bookingDateStr = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
            String bookingTimeStr = cursor.getString(cursor.getColumnIndexOrThrow("booking_time"));
            
            try {
                selectedDate.setTime(dateFormat.parse(bookingDateStr));
                selectedTime.setTime(timeFormat.parse(bookingTimeStr));
                updateDateTimeButtons();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }

    private void updateBooking() {
        if (service == null || bookingId == -1) return;

        // Check if selected date is in the past
        Calendar now = Calendar.getInstance();
        if (selectedDate.before(now) || (selectedDate.equals(now) && selectedTime.before(now))) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = db.updateServiceBooking(
            bookingId,
            session.getUserId(),
            dateFormat.format(selectedDate.getTime()),
            timeFormat.format(selectedTime.getTime()),
            service.price
        );

        if (success) {
            Toast.makeText(this, 
                String.format("Booking updated successfully! Total: %s", formatPrice(service.price)), 
                Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatPrice(double price) {
        return String.format("Rs. %,.2f", price);
    }
}