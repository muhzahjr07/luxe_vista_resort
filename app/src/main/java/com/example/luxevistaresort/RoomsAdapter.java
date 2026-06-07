package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.VH> {

    private final Context ctx;
    private List<Room> data;

    public RoomsAdapter(Context ctx, List<Room> data) {
        this.ctx = ctx;
        this.data = data;
    }

    public void updateRooms(List<Room> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_room, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Room r = data.get(position);
        h.tvName.setText(r.name);
        h.tvDesc.setText(r.description);
        h.tvPrice.setText(String.format("Rs. %.0f / night", r.price));
        h.tvAmenities.setText("Amenities: " + r.amenities);
        h.tvCapacity.setText("👥 " + r.capacity + " guests");

        h.btnBook.setOnClickListener(v -> {
            Intent i = new Intent(ctx, RoomDetailActivity.class);
            i.putExtra("room_id", r.id);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice, tvAmenities, tvCapacity;
        Button btnBook;
        VH(View itemView) {
            super(itemView);
            tvName  = itemView.findViewById(R.id.tvRoomName);
            tvDesc  = itemView.findViewById(R.id.tvRoomDescription);
            tvPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvAmenities = itemView.findViewById(R.id.tvRoomAmenities);
            tvCapacity = itemView.findViewById(R.id.tvRoomCapacity);
            btnBook = itemView.findViewById(R.id.btnBookRoom);
        }
    }
}
