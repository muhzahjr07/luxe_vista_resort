package com.example.luxevistaresort;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText; // Added import
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteReviewActivity extends BaseActivity {

    private DatabaseHelper db;
    private SessionManager session;
    
    private long itemId;
    private String itemType;
    private String itemName;
    private int selectedRating = 0;
    
    private TextView tvItemName, tvRatingText;
    private Button btnSubmitReview;
    private TextView[] starViews = new TextView[5];
    private EditText etReviewText; // Declare EditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_write_review);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to write reviews", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        itemId = getIntent().getLongExtra("item_id", -1);
        itemType = getIntent().getStringExtra("item_type");
        
        if (itemId == -1 || itemType == null) {
            Toast.makeText(this, "Invalid item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        loadItemDetails();
        setupStarRating();
    }

    private void setupViews() {
        tvItemName = findViewById(R.id.tvItemName);
        tvRatingText = findViewById(R.id.tvRatingText);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        etReviewText = findViewById(R.id.etReviewText); // Initialize EditText
        
        starViews[0] = findViewById(R.id.star1);
        starViews[1] = findViewById(R.id.star2);
        starViews[2] = findViewById(R.id.star3);
        starViews[3] = findViewById(R.id.star4);
        starViews[4] = findViewById(R.id.star5);
        
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void loadItemDetails() {
        if ("room".equals(itemType)) {
            Cursor cursor = db.getRoomById(itemId);
            if (cursor.moveToFirst()) {
                itemName = cursor.getString(1); // name
                tvItemName.setText(itemName);
            }
            cursor.close();
        } else {
            Cursor cursor = db.getServiceById(itemId);
            if (cursor.moveToFirst()) {
                itemName = cursor.getString(1); // name
                tvItemName.setText(itemName);
            }
            cursor.close();
        }
    }

    private void setupStarRating() {
        for (int i = 0; i < starViews.length; i++) {
            final int starIndex = i + 1;
            starViews[i].setOnClickListener(v -> setRating(starIndex));
        }
    }

    private void setRating(int rating) {
        selectedRating = rating;
        updateStarDisplay();
        updateRatingText();
    }

    private void updateStarDisplay() {
        for (int i = 0; i < starViews.length; i++) {
            if (i < selectedRating) {
                starViews[i].setText("⭐");
            } else {
                starViews[i].setText("☆");
            }
        }
    }

    private void updateRatingText() {
        String[] ratingTexts = {
            "Tap to rate", "Poor", "Fair", "Good", "Very Good", "Excellent"
        };
        tvRatingText.setText(ratingTexts[selectedRating]);
    }

    private void submitReview() {
        if (selectedRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cast to EditText to use getText()
        String reviewTextString = etReviewText.getText().toString().trim();
        if (TextUtils.isEmpty(reviewTextString)) {
            Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        String reviewerName = session.getEmail().split("@")[0]; // Use email prefix as name
        String reviewDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        long reviewId = db.addReview(
            session.getUserId(),
            itemId,
            itemType,
            reviewerName,
            reviewTextString,
            selectedRating,
            reviewDate
        );

        if (reviewId > 0) {
            Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
        }
    }
}
