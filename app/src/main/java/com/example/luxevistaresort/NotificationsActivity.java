package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {

    private RecyclerView rvNotifications;
    private NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        setupViews();
        loadNotifications();
    }

    private void setupViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        
        // Sample notifications
        notifications.add(new NotificationItem(
            "🔔", "Booking Confirmation", 
            "Your Colombo Ocean Suite booking has been confirmed for tomorrow.", 
            "2 hours ago", "Booking"
        ));
        
        notifications.add(new NotificationItem(
            "🛎", "Service Reminder", 
            "Your Ayurvedic Spa & Wellness appointment is scheduled for today at 2:00 PM.", 
            "1 day ago", "Service"
        ));
        
        notifications.add(new NotificationItem(
            "🎉", "Special Offer", 
            "Flash Sale: 30% off water sports activities today only!", 
            "3 hours ago", "Promotion"
        ));
        
        notifications.add(new NotificationItem(
            "📢", "Service Update", 
            "New Service Available: Traditional Sri Lankan Cooking Class now available!", 
            "1 day ago", "Update"
        ));
        
        notifications.add(new NotificationItem(
            "🏨", "Check-in Reminder", 
            "Your room check-in is tomorrow! We look forward to welcoming you.", 
            "4 hours ago", "Booking"
        ));

        adapter = new NotificationsAdapter(notifications);
        rvNotifications.setAdapter(adapter);
        
        Toast.makeText(this, notifications.size() + " notifications", Toast.LENGTH_SHORT).show();
    }
}
