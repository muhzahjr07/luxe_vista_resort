package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new SessionManager(this);

        setupRecyclerDashboard();
        setupBottomButtons();
        setupNotificationBell();
    }

    private void setupRecyclerDashboard() {
        RecyclerView recyclerDashboard = findViewById(R.id.recyclerDashboard);
        recyclerDashboard.setLayoutManager(new GridLayoutManager(this, 2));

        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("🏨", "Rooms", "Browse & Book"));
        items.add(new DashboardItem("🛎", "Services", "Spa & Dining"));
        items.add(new DashboardItem("📅", "Bookings", "My Reservations"));
        items.add(new DashboardItem("🎉", "Special Offers", "Exclusive Deals"));
        items.add(new DashboardItem("👤", "Profile", "My Account"));
        items.add(new DashboardItem("ℹ️", "Resort Info", "About & Contact"));

        DashboardAdapter adapter = new DashboardAdapter(items, item -> {
            switch (item.getTitle()) {
                case "Rooms":
                    if (checkLoginRequired()) {
                        startActivity(new Intent(this, RoomsActivity.class));
                    }
                    break;
                case "Services":
                    if (checkLoginRequired()) {
                        startActivity(new Intent(this, ServicesActivity.class));
                    }
                    break;
                case "Bookings":
                    if (checkLoginRequired()) {
                        startActivity(new Intent(this, BookingsActivity.class));
                    }
                    break;
                case "Special Offers":
                    startActivity(new Intent(this, SpecialOffersActivity.class));
                    break;
                case "Profile":
                    if (checkLoginRequired()) {
                        startActivity(new Intent(this, ProfileActivity.class));
                    }
                    break;
                case "Resort Info":
                    startActivity(new Intent(this, InfoActivity.class));
                    break;
            }
        });

        recyclerDashboard.setAdapter(adapter);
    }

    private void setupBottomButtons() {
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                session.logout();
                btnLogin.setText("🔐 Login");
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        updateLoginButton();
    }

    private void setupNotificationBell() {
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });
    }

    private boolean checkLoginRequired() {
        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }
        return true;
    }

    private void updateLoginButton() {
        Button btnLogin = findViewById(R.id.btnLogin);
        if (session.isLoggedIn()) {
            btnLogin.setText("🚪 Logout");
        } else {
            btnLogin.setText("🔐 Login");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }
}
