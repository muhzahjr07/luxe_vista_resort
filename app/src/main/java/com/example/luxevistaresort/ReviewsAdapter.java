package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.VH> {
    private final List<Review> data;

    public ReviewsAdapter(List<Review> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Review review = data.get(position);
        
        h.tvReviewerName.setText(review.reviewerName);
        h.tvReviewDate.setText(review.reviewDate);
        h.tvReviewText.setText(review.reviewText);
        h.tvReviewType.setText(review.reviewType.equals("room") ? "Room Review" : "Service Review");
        
        // Set rating stars
        String stars = getStarsString(review.rating);
        h.ratingStars.removeAllViews();
        TextView starsView = new TextView(h.itemView.getContext());
        starsView.setText(stars);
        starsView.setTextSize(16);
        h.ratingStars.addView(starsView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getStarsString(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("⭐");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewDate, tvReviewText, tvReviewType;
        LinearLayout ratingStars;

        VH(View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvReviewType = itemView.findViewById(R.id.tvReviewType);
            ratingStars = itemView.findViewById(R.id.ratingStars);
        }
    }
}
