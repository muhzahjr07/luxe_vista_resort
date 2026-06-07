package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsAdapter extends BaseActivity {
    private final List<NotificationItem> data;

    public NotificationsAdapter(List<NotificationItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NotificationItem notification = data.get(position);
        
        h.tvNotificationIcon.setText(notification.icon);
        h.tvNotificationTitle.setText(notification.title);
        h.tvNotificationMessage.setText(notification.message);
        h.tvNotificationTime.setText(notification.time);
        h.tvNotificationType.setText(notification.type);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNotificationIcon, tvNotificationTitle, tvNotificationMessage, tvNotificationTime, tvNotificationType;

        VH(View itemView) {
            super(itemView);
            tvNotificationIcon = itemView.findViewById(R.id.tvNotificationIcon);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            tvNotificationType = itemView.findViewById(R.id.tvNotificationType);
        }
    }
}
