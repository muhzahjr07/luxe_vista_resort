package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends BaseActivity {
    private DatabaseHelper db;
    private SessionManager session;
    
    // Views
    private TextView tvUserEmail, tvTotalBookings, tvTotalSpent;
    private Button btnUpdateProfile, btnSavePreferences;
    private Button btnRoomType, btnServiceCategory, btnStartDate, btnEndDate;
    
    // User data
    private String currentUsername, currentEmail, currentPhone;
    private String preferredRoomType = "Any";
    private String preferredServiceCategory = "Any";
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to view profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        loadUserData();
        loadBookingStatistics();
        setupClickListeners();
    }

    private void setupViews() {
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnSavePreferences = findViewById(R.id.btnSavePreferences);
        btnRoomType = findViewById(R.id.btnRoomType);
        btnServiceCategory = findViewById(R.id.btnServiceCategory);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        
        findViewById(R.id.btnReviewRooms).setOnClickListener(v -> showReviewableRooms());
        findViewById(R.id.btnReviewServices).setOnClickListener(v -> showReviewableServices());
    }

    private void loadUserData() {
        String email = session.getEmail();
        tvUserEmail.setText(email);
        
        // Load user details from database
        Cursor cursor = db.getUserByEmail(email);
        if (cursor.moveToFirst()) {
            currentUsername = cursor.getString(1); // username
            currentEmail = cursor.getString(2); // email
            currentPhone = cursor.getString(3); // phone (if exists)
            
            // Set the edit text fields
            findViewById(R.id.etUsername).setTag(currentUsername);
            findViewById(R.id.etEmail).setTag(currentEmail);
            findViewById(R.id.etPhone).setTag(currentPhone != null ? currentPhone : "");
        }
        cursor.close();
        
        // Load preferences (you can extend this to load from a preferences table)
        updatePreferenceButtons();
    }

    private void loadBookingStatistics() {
        long userId = session.getUserId();
        
        // Count total bookings
        Cursor roomBookings = db.getRoomBookingsForUser(userId);
        Cursor serviceBookings = db.getServiceBookingsForUser(userId);
        
        int totalBookings = roomBookings.getCount() + serviceBookings.getCount();
        tvTotalBookings.setText(String.valueOf(totalBookings));
        
        // Calculate total spent
        double totalSpent = 0;
        
        // Room bookings total
        while (roomBookings.moveToNext()) {
            totalSpent += roomBookings.getDouble(roomBookings.getColumnIndexOrThrow("total_price"));
        }
        
        // Service bookings total
        while (serviceBookings.moveToNext()) {
            totalSpent += serviceBookings.getDouble(serviceBookings.getColumnIndexOrThrow("total_price"));
        }
        
        tvTotalSpent.setText(String.format("Rs. %.0f", totalSpent));
        
        roomBookings.close();
        serviceBookings.close();
    }

    private void setupClickListeners() {
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnSavePreferences.setOnClickListener(v -> savePreferences());
        btnRoomType.setOnClickListener(v -> selectRoomType());
        btnServiceCategory.setOnClickListener(v -> selectServiceCategory());
        btnStartDate.setOnClickListener(v -> selectStartDate());
        btnEndDate.setOnClickListener(v -> selectEndDate());
    }

    private void updateProfile() {
        String username = ((android.widget.EditText) findViewById(R.id.etUsername)).getText().toString().trim();
        String email = ((android.widget.EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String phone = ((android.widget.EditText) findViewById(R.id.etPhone)).getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Username and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user in database
        boolean success = db.updateUserProfile(session.getUserId(), username, email, phone);
        
        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            currentUsername = username;
            currentEmail = email;
            currentPhone = phone;
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePreferences() {
        // Save preferences to database (you can create a preferences table)
        Toast.makeText(this, "Preferences saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void selectRoomType() {
        String[] roomTypes = {"Any", "Suite", "Villa", "Room", "Bungalow"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Preferred Room Type")
                .setSingleChoiceItems(roomTypes, getRoomTypeIndex(preferredRoomType), (dialog, which) -> {
                    preferredRoomType = roomTypes[which];
                    updatePreferenceButtons();
                    dialog.dismiss();
                })
                .show();
    }

    private void selectServiceCategory() {
        String[] categories = {"Any", "Ayurvedic Wellness", "Sri Lankan Dining", "Water Recreation", "Cultural Tours", "Concierge", "Transportation"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Preferred Service Category")
                .setSingleChoiceItems(categories, getServiceCategoryIndex(preferredServiceCategory), (dialog, which) -> {
                    preferredServiceCategory = categories[which];
                    updatePreferenceButtons();
                    dialog.dismiss();
                })
                .show();
    }

    private void selectStartDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDate.set(year, month, dayOfMonth);
                    btnStartDate.setText(dateFormat.format(startDate.getTime()));
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectEndDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    endDate.set(year, month, dayOfMonth);
                    btnEndDate.setText(dateFormat.format(endDate.getTime()));
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updatePreferenceButtons() {
        btnRoomType.setText(preferredRoomType.equals("Any") ? "Select Room Type" : preferredRoomType);
        btnServiceCategory.setText(preferredServiceCategory.equals("Any") ? "Select Service Category" : preferredServiceCategory);
        btnStartDate.setText(dateFormat.format(startDate.getTime()));
        btnEndDate.setText(dateFormat.format(endDate.getTime()));
    }

    private int getRoomTypeIndex(String roomType) {
        String[] roomTypes = {"Any", "Suite", "Villa", "Room", "Bungalow"};
        for (int i = 0; i < roomTypes.length; i++) {
            if (roomTypes[i].equals(roomType)) return i;
        }
        return 0;
    }

    private int getServiceCategoryIndex(String category) {
        String[] categories = {"Any", "Ayurvedic Wellness", "Sri Lankan Dining", "Water Recreation", "Cultural Tours", "Concierge", "Transportation"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) return i;
        }
        return 0;
    }

    private void showReviewableRooms() {
        long userId = session.getUserId();
        Cursor cursor = db.getRoomBookingsForUser(userId);
        
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No room bookings found to review", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        
        // Create a list of all room bookings for review
        String[] roomNames = new String[cursor.getCount()];
        long[] roomIds = new long[cursor.getCount()];
        int index = 0;
        
        while (cursor.moveToNext()) {
            roomIds[index] = cursor.getLong(cursor.getColumnIndexOrThrow("room_id"));
            roomNames[index] = cursor.getString(cursor.getColumnIndexOrThrow("room_name"));
            index++;
        }
        cursor.close();
        
        // Show dialog to select room for review
        new AlertDialog.Builder(this)
            .setTitle("Select Room to Review")
            .setItems(roomNames, (dialog, which) -> {
                Intent intent = new Intent(this, WriteReviewActivity.class);
                intent.putExtra("item_id", roomIds[which]);
                intent.putExtra("item_type", "room");
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showReviewableServices() {
        long userId = session.getUserId();
        Cursor cursor = db.getServiceBookingsForUser(userId);
        
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No service bookings found to review", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        
        // Create a list of all service bookings for review
        String[] serviceNames = new String[cursor.getCount()];
        long[] serviceIds = new long[cursor.getCount()];
        int index = 0;
        
        while (cursor.moveToNext()) {
            serviceIds[index] = cursor.getLong(cursor.getColumnIndexOrThrow("service_id"));
            serviceNames[index] = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));
            index++;
        }
        cursor.close();
        
        // Show dialog to select service for review
        new AlertDialog.Builder(this)
            .setTitle("Select Service to Review")
            .setItems(serviceNames, (dialog, which) -> {
                Intent intent = new Intent(this, WriteReviewActivity.class);
                intent.putExtra("item_id", serviceIds[which]);
                intent.putExtra("item_type", "service");
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
