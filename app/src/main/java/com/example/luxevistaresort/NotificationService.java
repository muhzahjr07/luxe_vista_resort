package com.example.luxevistaresort;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;   // <-- ADD THIS
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Random;


public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "luxevista_notifications";
    private static final int NOTIFICATION_ID = 1;
    
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHelper(this);
        session = new SessionManager(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NotificationService started");
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification("LuxeVista Resort", "Notification service running"));
        
        // Schedule notifications
        scheduleNotifications();
        
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "LuxeVista Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for LuxeVista Resort app");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotifications() {
        // Check for upcoming bookings
        checkUpcomingBookings();
        
        // Send promotional notifications
        sendPromotionalNotifications();
        
        // Send service updates
        sendServiceUpdates();
    }

    private void checkUpcomingBookings() {
        if (!session.isLoggedIn()) return;
        
        long userId = session.getUserId();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        
        // Check room bookings
        Cursor roomBookings = db.getRoomBookingsForUser(userId);
        while (roomBookings.moveToNext()) {
            String checkInDate = roomBookings.getString(3); // check_in_date
            if (isDateTomorrow(checkInDate)) {
                String roomName = roomBookings.getString(6); // room_name
                showNotification(
                    "Upcoming Check-in",
                    "Your room " + roomName + " check-in is tomorrow!",
                    MainActivity.class
                );
            }
        }
        roomBookings.close();
        
        // Check service bookings
        Cursor serviceBookings = db.getServiceBookingsForUser(userId);
        while (serviceBookings.moveToNext()) {
            String bookingDate = serviceBookings.getString(3); // booking_date
            if (isDateTomorrow(bookingDate)) {
                String serviceName = serviceBookings.getString(6); // service_name
                showNotification(
                    "Upcoming Service",
                    "Your " + serviceName + " appointment is tomorrow!",
                    MainActivity.class
                );
            }
        }
        serviceBookings.close();
    }

    private void sendPromotionalNotifications() {
        // Random promotional notifications
        String[] promotions = {
            "Special Offer: 20% off spa treatments this week!",
            "Weekend Package: Book 2 nights, get 1 free!",
            "New Service Alert: Traditional Sri Lankan Cooking Class now available!",
            "Exclusive: Private beach dinner for couples - Limited availability!",
            "Flash Sale: 30% off water sports activities today only!"
        };
        
        Random random = new Random();
        String promotion = promotions[random.nextInt(promotions.length)];
        
        showNotification(
            "Special Promotion",
            promotion,
            MainActivity.class
        );
    }

    private void sendServiceUpdates() {
        String[] updates = {
            "New Service Available: Ayurvedic Wellness Package",
            "Service Update: Cultural Tours now include transportation",
            "Enhanced Service: Spa treatments now include complimentary tea",
            "New Addition: Private yoga sessions on the beach",
            "Service Alert: Water sports equipment upgraded"
        };
        
        Random random = new Random();
        String update = updates[random.nextInt(updates.length)];
        
        showNotification(
            "Service Update",
            update,
            ServicesActivity.class
        );
    }

    private boolean isDateTomorrow(String dateString) {
        try {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            
            Calendar bookingDate = Calendar.getInstance();
            String[] parts = dateString.split("-");
            bookingDate.set(Integer.parseInt(parts[0]), 
                          Integer.parseInt(parts[1]) - 1, 
                          Integer.parseInt(parts[2]));
            
            return bookingDate.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                   bookingDate.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return false;
        }
    }

    private void showNotification(String title, String message, Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(1000), notification);
    }

    private Notification createNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .build();
    }
}
