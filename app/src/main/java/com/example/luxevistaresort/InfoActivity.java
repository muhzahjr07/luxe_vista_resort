package com.example.luxevistaresort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);

        setupViews();
    }

    private void setupViews() {
        Button btnCall = findViewById(R.id.btnCall);
        Button btnEmail = findViewById(R.id.btnEmail);
        Button btnWebsite = findViewById(R.id.btnWebsite);
        Button btnLocation = findViewById(R.id.btnLocation);

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+94112345678"));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:info@luxevistaresort.lk"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from LuxeVista Resort App");
            startActivity(intent);
        });

        btnWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.luxevistaresort.lk"));
            startActivity(intent);
        });

        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:6.9271,79.8612?q=LuxeVista Resort, Colombo, Sri Lanka"));
            startActivity(intent);
        });
    }
}
