package com.example.luxevistaresort;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookingsActivity extends BaseActivity {

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            finish();
            return;
        }

        setupViews();
        loadBookings();
    }

    private void setupViews() {
        RecyclerView rvRoomBookings = findViewById(R.id.rvRoomBookings);
        RecyclerView rvServiceBookings = findViewById(R.id.rvServiceBookings);
        TextView tvNoRoomBookings = findViewById(R.id.tvNoRoomBookings);
        TextView tvNoServiceBookings = findViewById(R.id.tvNoServiceBookings);

        rvRoomBookings.setLayoutManager(new LinearLayoutManager(this));
        rvServiceBookings.setLayoutManager(new LinearLayoutManager(this));

        // Load room bookings
        ArrayList<Booking> roomBookings = new ArrayList<>();
        Cursor roomCursor = db.getRoomBookingsForUser(session.getUserId());
        while (roomCursor.moveToNext()) {
            roomBookings.add(new Booking(
                roomCursor.getLong(roomCursor.getColumnIndexOrThrow("id")), // booking id
                roomCursor.getString(roomCursor.getColumnIndexOrThrow("room_name")), // room name
                roomCursor.getString(roomCursor.getColumnIndexOrThrow("check_in_date")), // check-in date
                roomCursor.getString(roomCursor.getColumnIndexOrThrow("check_out_date")), // check-out date
                roomCursor.getDouble(roomCursor.getColumnIndexOrThrow("total_price")), // total price
                "room"
            ));
        }
        roomCursor.close();

        if (roomBookings.isEmpty()) {
            tvNoRoomBookings.setVisibility(TextView.VISIBLE);
            rvRoomBookings.setVisibility(RecyclerView.GONE);
        } else {
            tvNoRoomBookings.setVisibility(TextView.GONE);
            rvRoomBookings.setVisibility(RecyclerView.VISIBLE);
            rvRoomBookings.setAdapter(new BookingsAdapter(this, roomBookings));
        }

        // Load service bookings
        ArrayList<Booking> serviceBookings = new ArrayList<>();
        Cursor serviceCursor = db.getServiceBookingsForUser(session.getUserId());
        while (serviceCursor.moveToNext()) {
            serviceBookings.add(new Booking(
                serviceCursor.getLong(serviceCursor.getColumnIndexOrThrow("id")), // booking id
                serviceCursor.getString(serviceCursor.getColumnIndexOrThrow("service_name")), // service name
                serviceCursor.getString(serviceCursor.getColumnIndexOrThrow("booking_date")), // booking date
                serviceCursor.getString(serviceCursor.getColumnIndexOrThrow("booking_time")), // booking time
                serviceCursor.getDouble(serviceCursor.getColumnIndexOrThrow("total_price")), // total price
                "service"
            ));
        }
        serviceCursor.close();

        if (serviceBookings.isEmpty()) {
            tvNoServiceBookings.setVisibility(TextView.VISIBLE);
            rvServiceBookings.setVisibility(RecyclerView.GONE);
        } else {
            tvNoServiceBookings.setVisibility(TextView.GONE);
            rvServiceBookings.setVisibility(RecyclerView.VISIBLE);
            rvServiceBookings.setAdapter(new BookingsAdapter(this, serviceBookings));
        }
    }

    private void loadBookings() {
        // This method is called from onCreate, but we moved the logic to setupViews
        // Keeping it for potential future use
    }
}
