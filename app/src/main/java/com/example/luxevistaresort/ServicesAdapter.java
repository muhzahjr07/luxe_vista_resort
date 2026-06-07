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

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.VH> {
    private final Context ctx;
    private List<Service> data;

    public ServicesAdapter(Context ctx, List<Service> data) {
        this.ctx = ctx; this.data = data;
    }

    public void updateServices(List<Service> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_service, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Service s = data.get(pos);
        h.tvName.setText(s.name);
        h.tvDesc.setText(s.description);
        h.tvPrice.setText(String.format("Rs. %.0f", s.price));
        h.tvDuration.setText("⏱ " + s.duration);
        h.tvAvailability.setText("🕘 " + s.availability);
        h.tvCategory.setText("Category: " + s.category);

        h.btnBook.setOnClickListener(v -> {
            Intent i = new Intent(ctx, ServiceDetailActivity.class);
            i.putExtra("service_id", s.id);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice, tvDuration, tvAvailability, tvCategory;
        Button btnBook;
        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvServiceName);
            tvDesc = v.findViewById(R.id.tvServiceDescription);
            tvPrice = v.findViewById(R.id.tvServicePrice);
            tvDuration = v.findViewById(R.id.tvServiceDuration);
            tvAvailability = v.findViewById(R.id.tvServiceAvailability);
            tvCategory = v.findViewById(R.id.tvServiceCategory);
            btnBook = v.findViewById(R.id.btnBookService);
        }
    }
}
