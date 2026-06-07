package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.VH> {
    private final Context context;
    private final List<SpecialOffer> data;

    public SpecialOffersAdapter(Context context, List<SpecialOffer> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_special_offer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SpecialOffer offer = data.get(position);
        
        h.tvOfferIcon.setText(offer.icon);
        h.tvOfferTitle.setText(offer.title);
        h.tvOfferDiscount.setText(offer.discount);
        h.tvOfferDescription.setText(offer.description);
        h.tvOfferValidUntil.setText("Valid until: " + offer.validUntil);
        
        // Set price information
        h.tvOriginalPrice.setText(offer.originalPrice);
        h.tvDiscountedPrice.setText(offer.discountedPrice);
        
        h.btnClaimOffer.setOnClickListener(v -> claimOffer(offer));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void claimOffer(SpecialOffer offer) {
        Toast.makeText(context, "Offer claimed! Redirecting to booking...", Toast.LENGTH_SHORT).show();
        
        // Redirect based on offer type
        Intent intent;
        if ("room".equals(offer.offerType)) {
            intent = new Intent(context, RoomsActivity.class);
        } else if ("service".equals(offer.offerType)) {
            intent = new Intent(context, ServicesActivity.class);
        } else {
            intent = new Intent(context, MainActivity.class);
        }
        
        context.startActivity(intent);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvOfferIcon, tvOfferTitle, tvOfferDiscount, tvOfferDescription, tvOfferValidUntil;
        TextView tvOriginalPrice, tvDiscountedPrice;
        Button btnClaimOffer;

        VH(View itemView) {
            super(itemView);
            tvOfferIcon = itemView.findViewById(R.id.tvOfferIcon);
            tvOfferTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvOfferDiscount = itemView.findViewById(R.id.tvOfferDiscount);
            tvOfferDescription = itemView.findViewById(R.id.tvOfferDescription);
            tvOfferValidUntil = itemView.findViewById(R.id.tvOfferValidUntil);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountedPrice = itemView.findViewById(R.id.tvDiscountedPrice);
            btnClaimOffer = itemView.findViewById(R.id.btnClaimOffer);
        }
    }
}
