package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffersActivity extends BaseActivity {

    private RecyclerView rvOffers;
    private SpecialOffersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_special_offers);

        setupViews();
        loadSpecialOffers();
    }

    private void setupViews() {
        rvOffers = findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSpecialOffers() {
        List<SpecialOffer> offers = new ArrayList<>();
        
        // Sample special offers
        offers.add(new SpecialOffer(
            "🛎", "Flash Sale: Spa Treatments", "30% OFF",
            "Enjoy our premium Ayurvedic spa treatments at a special discounted rate. Book now and save big!",
            "Original Price: Rs. 12,000", "Discounted: Rs. 8,400", "Dec 31, 2024", "service"
        ));
        
        offers.add(new SpecialOffer(
            "🏨", "Weekend Package Deal", "25% OFF",
            "Book 2 nights and get 1 night free! Perfect for a relaxing weekend getaway at our luxury resort.",
            "Original Price: Rs. 90,000", "Discounted: Rs. 67,500", "Jan 15, 2025", "room"
        ));
        
        offers.add(new SpecialOffer(
            "🍽", "Fine Dining Experience", "20% OFF",
            "Indulge in authentic Sri Lankan cuisine prepared by our master chefs. Limited time offer!",
            "Original Price: Rs. 8,500", "Discounted: Rs. 6,800", "Jan 10, 2025", "service"
        ));
        
        offers.add(new SpecialOffer(
            "🏊", "Water Sports Package", "35% OFF",
            "Experience thrilling water activities including snorkeling, diving, and boat tours in the Indian Ocean.",
            "Original Price: Rs. 6,500", "Discounted: Rs. 4,225", "Dec 25, 2024", "service"
        ));
        
        offers.add(new SpecialOffer(
            "🎯", "Cultural Heritage Tour", "15% OFF",
            "Explore UNESCO World Heritage sites and cultural attractions with our expert guides.",
            "Original Price: Rs. 4,500", "Discounted: Rs. 3,825", "Jan 20, 2025", "service"
        ));
        
        offers.add(new SpecialOffer(
            "🚗", "Airport Transfer Bundle", "40% OFF",
            "Complimentary airport pickup and drop-off service. Book with any room reservation and save!",
            "Original Price: Rs. 3,500", "Discounted: Rs. 2,100", "Ongoing", "service"
        ));

        adapter = new SpecialOffersAdapter(this, offers);
        rvOffers.setAdapter(adapter);
        
        Toast.makeText(this, offers.size() + " special offers available", Toast.LENGTH_SHORT).show();
    }
}
