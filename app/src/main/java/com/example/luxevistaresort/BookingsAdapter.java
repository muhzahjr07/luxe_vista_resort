package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingsAdapter extends BaseActivity {
    private final Context ctx;
    private final List<Booking> data;
    private DatabaseHelper db;
    private SessionManager session;

    public BookingsAdapter(Context ctx, List<Booking> data) {
        this.ctx = ctx;
        this.data = data;
        this.db = new DatabaseHelper(ctx);
        this.session = new SessionManager(ctx);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Booking b = data.get(position);
        h.tvName.setText(b.name);
        h.tvPrice.setText(formatPrice(b.price));
        
        if ("room".equals(b.type)) {
            h.tvDate1.setText("Check-in: " + b.date1);
            h.tvDate2.setText("Check-out: " + b.date2);
            h.tvType.setText("🏨 Room Booking");
        } else {
            h.tvDate1.setText("Date: " + b.date1);
            h.tvDate2.setText("Time: " + b.date2);
            h.tvType.setText("🛎 Service Booking");
        }

        // Set up delete button click listener
        h.btnDelete.setOnClickListener(v -> showDeleteConfirmation(b, position));
        
        // Set up edit button click listener
        h.btnEdit.setOnClickListener(v -> editBooking(b, position));
    }

    private void showDeleteConfirmation(Booking booking, int position) {
        new AlertDialog.Builder(ctx)
                .setTitle("Delete Booking")
                .setMessage("Are you sure you want to delete this booking? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteBooking(booking, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBooking(Booking booking, int position) {
        long userId = session.getUserId();
        boolean success = false;

        if ("room".equals(booking.type)) {
            success = db.deleteRoomBooking(booking.id, userId);
        } else {
            success = db.deleteServiceBooking(booking.id, userId);
        }

        if (success) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
            Toast.makeText(ctx, "Booking deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctx, "Failed to delete booking", Toast.LENGTH_SHORT).show();
        }
    }

    private void editBooking(Booking booking, int position) {
        if ("room".equals(booking.type)) {
            // For room bookings, we need to get the room_id from the booking
            Cursor cursor = db.getRoomBookingById(booking.id);
            if (cursor.moveToFirst()) {
                long roomId = cursor.getLong(cursor.getColumnIndexOrThrow("room_id"));
                Intent intent = new Intent(ctx, RoomDetailActivity.class);
                intent.putExtra("room_id", roomId);
                intent.putExtra("edit_mode", true);
                intent.putExtra("booking_id", booking.id);
                ctx.startActivity(intent);
            }
            cursor.close();
        } else {
            // For service bookings, we need to get the service_id from the booking
            Cursor cursor = db.getServiceBookingById(booking.id);
            if (cursor.moveToFirst()) {
                long serviceId = cursor.getLong(cursor.getColumnIndexOrThrow("service_id"));
                Intent intent = new Intent(ctx, ServiceDetailActivity.class);
                intent.putExtra("service_id", serviceId);
                intent.putExtra("edit_mode", true);
                intent.putExtra("booking_id", booking.id);
                ctx.startActivity(intent);
            }
            cursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String formatPrice(double price) {
        return String.format("Rs. %,.2f", price);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate1, tvDate2, tvPrice, tvType;
        ImageButton btnDelete, btnEdit;

        VH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBookingName);
            tvDate1 = itemView.findViewById(R.id.tvBookingDate1);
            tvDate2 = itemView.findViewById(R.id.tvBookingDate2);
            tvPrice = itemView.findViewById(R.id.tvBookingPrice);
            tvType = itemView.findViewById(R.id.tvBookingType);
            btnDelete = itemView.findViewById(R.id.btnDeleteBooking);
            btnEdit = itemView.findViewById(R.id.btnEditBooking);
        }
    }
}