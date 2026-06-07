package com.example.luxevistaresort;

import android.app.DatePickerDialog;
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

public class RoomDetailActivity extends BaseActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private long roomId;
    private long bookingId = -1;
    private boolean isEditMode = false;
    private Room room;
    private TextView tvRoomName, tvRoomDescription, tvRoomPrice, tvRoomAmenities, tvRoomCapacity;
    private Button btnSelectCheckIn, btnSelectCheckOut, btnBookRoom;
    private Calendar checkInDate = Calendar.getInstance();
    private Calendar checkOutDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_detail);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to book rooms", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        roomId = getIntent().getLongExtra("room_id", -1);
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        bookingId = getIntent().getLongExtra("booking_id", -1);
        
        if (roomId == -1) {
            Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRoomDetails();
        setupViews();
    }

    private void loadRoomDetails() {
        Cursor cursor = db.getRoomById(roomId);
        if (cursor.moveToFirst()) {
            room = new Room(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getDouble(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getInt(6)
            );
        }
        cursor.close();
    }

    private void setupViews() {
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        tvRoomAmenities = findViewById(R.id.tvRoomAmenities);
        tvRoomCapacity = findViewById(R.id.tvRoomCapacity);
        btnSelectCheckIn = findViewById(R.id.btnSelectCheckIn);
        btnSelectCheckOut = findViewById(R.id.btnSelectCheckOut);
        btnBookRoom = findViewById(R.id.btnBookRoom);

        if (room != null) {
            tvRoomName.setText(room.name);
            tvRoomDescription.setText(room.description);
            tvRoomPrice.setText(formatPrice(room.price) + " / night");
            tvRoomAmenities.setText(room.amenities);
            tvRoomCapacity.setText("👥 " + room.capacity + " guests");
        }

        // Set default dates (check-in tomorrow, check-out day after)
        checkInDate.add(Calendar.DAY_OF_MONTH, 1);
        checkOutDate.add(Calendar.DAY_OF_MONTH, 2);
        updateDateButtons();

        btnSelectCheckIn.setOnClickListener(v -> showDatePickerDialog(true));
        btnSelectCheckOut.setOnClickListener(v -> showDatePickerDialog(false));
        
        if (isEditMode) {
            btnBookRoom.setText("Update Booking");
            loadBookingDetails();
        } else {
            btnBookRoom.setText("Book This Room");
        }
        
        btnBookRoom.setOnClickListener(v -> {
            if (isEditMode) {
                updateBooking();
            } else {
                bookRoom();
            }
        });
    }

    private void showDatePickerDialog(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInDate : checkOutDate;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                if (isCheckIn) {
                    checkInDate = calendar;
                    // Ensure check-out is after check-in
                    if (checkOutDate.before(checkInDate) || checkOutDate.equals(checkInDate)) {
                        checkOutDate = (Calendar) checkInDate.clone();
                        checkOutDate.add(Calendar.DAY_OF_MONTH, 1);
                    }
                } else {
                    checkOutDate = calendar;
                }
                updateDateButtons();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        if (isCheckIn) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMinDate(checkInDate.getTimeInMillis());
        }
        
        datePickerDialog.show();
    }

    private void updateDateButtons() {
        btnSelectCheckIn.setText("Check-in: " + dateFormat.format(checkInDate.getTime()));
        btnSelectCheckOut.setText("Check-out: " + dateFormat.format(checkOutDate.getTime()));
    }

    private void bookRoom() {
        if (room == null) return;

        long days = (checkOutDate.getTimeInMillis() - checkInDate.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        if (days <= 0) {
            Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPrice = room.price * days;
        long bookingId = db.addRoomBooking(
            session.getUserId(),
            roomId,
            dateFormat.format(checkInDate.getTime()),
            dateFormat.format(checkOutDate.getTime()),
            totalPrice
        );

        if (bookingId > 0) {
            Toast.makeText(this, 
                String.format("Room booked successfully! Total: %s", formatPrice(totalPrice)), 
                Toast.LENGTH_LONG).show();
            
            // Show notification
            NotificationHelper.showBookingConfirmation(this, 
                "Room Booking Confirmed", 
                String.format("Your %s booking has been confirmed for %s", room.name, dateFormat.format(checkInDate.getTime())));
            
            finish();
        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookingDetails() {
        if (bookingId == -1) return;
        
        Cursor cursor = db.getRoomBookingById(bookingId);
        if (cursor.moveToFirst()) {
            String checkInStr = cursor.getString(cursor.getColumnIndexOrThrow("check_in_date"));
            String checkOutStr = cursor.getString(cursor.getColumnIndexOrThrow("check_out_date"));
            
            try {
                checkInDate.setTime(dateFormat.parse(checkInStr));
                checkOutDate.setTime(dateFormat.parse(checkOutStr));
                updateDateButtons();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }

    private void updateBooking() {
        if (room == null || bookingId == -1) return;

        long days = (checkOutDate.getTimeInMillis() - checkInDate.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        if (days <= 0) {
            Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPrice = room.price * days;
        boolean success = db.updateRoomBooking(
            bookingId,
            session.getUserId(),
            dateFormat.format(checkInDate.getTime()),
            dateFormat.format(checkOutDate.getTime()),
            totalPrice
        );

        if (success) {
            Toast.makeText(this, 
                String.format("Booking updated successfully! Total: %s", formatPrice(totalPrice)), 
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