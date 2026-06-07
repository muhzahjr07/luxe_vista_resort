package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServicesActivity extends BaseActivity {

    private DatabaseHelper db;
    private RecyclerView rvServices;
    private ServicesAdapter adapter;
    private List<Service> allServices;
    private List<Service> filteredServices;
    
    // Filter states
    private String selectedCategory = "All";
    private String selectedPriceRange = "All";
    private String selectedDuration = "All";
    private String currentSort = "price_asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);

        db = new DatabaseHelper(this);
        setupViews();
        loadServices();
        setupFilterButtons();
        setupSortButtons();
    }

    private void setupViews() {
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        
        allServices = new ArrayList<>();
        filteredServices = new ArrayList<>();
    }

    private void loadServices() {
        allServices.clear();
        Cursor c = db.getAllServices();
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            double price = c.getDouble(3);
            String availability = c.getString(4);
            String duration = c.getString(5);
            String category = c.getString(6);
            allServices.add(new Service(id, name, desc, availability, price, duration, category));
        }
        c.close();
        
        applyFiltersAndSort();
    }

    private void setupFilterButtons() {
        Button btnFilterCategory = findViewById(R.id.btnFilterCategory);
        Button btnFilterPrice = findViewById(R.id.btnFilterPrice);
        Button btnFilterDuration = findViewById(R.id.btnFilterDuration);
        Button btnClearFilters = findViewById(R.id.btnClearFilters);

        btnFilterCategory.setOnClickListener(v -> showCategoryFilter());
        btnFilterPrice.setOnClickListener(v -> showPriceRangeFilter());
        btnFilterDuration.setOnClickListener(v -> showDurationFilter());
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }

    private void setupSortButtons() {
        Button btnSortPrice = findViewById(R.id.btnSortPrice);
        Button btnSortName = findViewById(R.id.btnSortName);
        Button btnSortDuration = findViewById(R.id.btnSortDuration);

        btnSortPrice.setOnClickListener(v -> sortServices("price"));
        btnSortName.setOnClickListener(v -> sortServices("name"));
        btnSortDuration.setOnClickListener(v -> sortServices("duration"));
    }

    private void showCategoryFilter() {
        String[] categories = {"All", "Ayurvedic Wellness", "Sri Lankan Dining", "Water Recreation", "Cultural Tours", "Concierge", "Transportation"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setSingleChoiceItems(categories, getCategoryIndex(selectedCategory), (dialog, which) -> {
                    selectedCategory = categories[which];
                    updateFilterButton(R.id.btnFilterCategory, "Category", selectedCategory);
                    applyFiltersAndSort();
                    dialog.dismiss();
                })
                .show();
    }

    private void showPriceRangeFilter() {
        String[] priceRanges = {"All", "Under Rs. 5,000", "Rs. 5,000 - Rs. 8,000", "Rs. 8,000 - Rs. 12,000", "Above Rs. 12,000"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Price Range")
                .setSingleChoiceItems(priceRanges, getPriceRangeIndex(selectedPriceRange), (dialog, which) -> {
                    selectedPriceRange = priceRanges[which];
                    updateFilterButton(R.id.btnFilterPrice, "Price Range", selectedPriceRange);
                    applyFiltersAndSort();
                    dialog.dismiss();
                })
                .show();
    }

    private void showDurationFilter() {
        String[] durations = {"All", "1 hour", "1.5 hours", "2 hours", "3+ hours"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Duration")
                .setSingleChoiceItems(durations, getDurationIndex(selectedDuration), (dialog, which) -> {
                    selectedDuration = durations[which];
                    updateFilterButton(R.id.btnFilterDuration, "Duration", selectedDuration);
                    applyFiltersAndSort();
                    dialog.dismiss();
                })
                .show();
    }

    private void clearAllFilters() {
        selectedCategory = "All";
        selectedPriceRange = "All";
        selectedDuration = "All";
        
        updateFilterButton(R.id.btnFilterCategory, "Category", "All");
        updateFilterButton(R.id.btnFilterPrice, "Price Range", "All");
        updateFilterButton(R.id.btnFilterDuration, "Duration", "All");
        
        applyFiltersAndSort();
        Toast.makeText(this, "All filters cleared", Toast.LENGTH_SHORT).show();
    }

    private void updateFilterButton(int buttonId, String prefix, String value) {
        Button button = findViewById(buttonId);
        if (value.equals("All")) {
            button.setText(prefix);
        } else {
            button.setText(prefix + ": " + value);
        }
    }

    private void sortServices(String sortType) {
        if (currentSort.equals(sortType + "_asc")) {
            currentSort = sortType + "_desc";
        } else {
            currentSort = sortType + "_asc";
        }
        
        applyFiltersAndSort();
        
        // Update button text to show current sort
        String sortText = sortType.substring(0, 1).toUpperCase() + sortType.substring(1);
        if (currentSort.endsWith("_desc")) {
            sortText += " ↓";
        } else {
            sortText += " ↑";
        }
        
        Button sortButton = findViewById(getSortButtonId(sortType));
        sortButton.setText(sortText);
    }

    private int getSortButtonId(String sortType) {
        switch (sortType) {
            case "price": return R.id.btnSortPrice;
            case "name": return R.id.btnSortName;
            case "duration": return R.id.btnSortDuration;
            default: return R.id.btnSortPrice;
        }
    }

    private void applyFiltersAndSort() {
        filteredServices.clear();
        
        // Apply filters
        for (Service service : allServices) {
            if (matchesCategory(service) && matchesPriceRange(service) && matchesDuration(service)) {
                filteredServices.add(service);
            }
        }
        
        // Apply sorting
        Collections.sort(filteredServices, getComparator());
        
        // Update adapter
        if (adapter == null) {
            adapter = new ServicesAdapter(this, filteredServices);
            rvServices.setAdapter(adapter);
        } else {
            adapter.updateServices(filteredServices);
        }
        
        // Show results count
        Toast.makeText(this, filteredServices.size() + " services found", Toast.LENGTH_SHORT).show();
    }

    private boolean matchesCategory(Service service) {
        if (selectedCategory.equals("All")) return true;
        return service.category.equals(selectedCategory);
    }

    private boolean matchesPriceRange(Service service) {
        if (selectedPriceRange.equals("All")) return true;
        
        double price = service.price;
        switch (selectedPriceRange) {
            case "Under Rs. 5,000": return price < 5000;
            case "Rs. 5,000 - Rs. 8,000": return price >= 5000 && price <= 8000;
            case "Rs. 8,000 - Rs. 12,000": return price >= 8000 && price <= 12000;
            case "Above Rs. 12,000": return price > 12000;
            default: return true;
        }
    }

    private boolean matchesDuration(Service service) {
        if (selectedDuration.equals("All")) return true;
        return service.duration.equals(selectedDuration);
    }

    private Comparator<Service> getComparator() {
        String sortType = currentSort.split("_")[0];
        boolean ascending = currentSort.endsWith("_asc");
        
        switch (sortType) {
            case "price":
                return ascending ? 
                    Comparator.comparingDouble(service -> service.price) :
                    Comparator.comparingDouble((Service service) -> service.price).reversed();
            case "name":
                return ascending ?
                    Comparator.comparing(service -> service.name) :
                    Comparator.comparing((Service service) -> service.name).reversed();
            case "duration":
                return ascending ?
                    Comparator.comparing(service -> service.duration) :
                    Comparator.comparing((Service service) -> service.duration).reversed();
            default:
                return Comparator.comparingDouble(service -> service.price);
        }
    }

    private int getCategoryIndex(String category) {
        String[] categories = {"All", "Ayurvedic Wellness", "Sri Lankan Dining", "Water Recreation", "Cultural Tours", "Concierge", "Transportation"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) return i;
        }
        return 0;
    }

    private int getPriceRangeIndex(String priceRange) {
        String[] priceRanges = {"All", "Under Rs. 5,000", "Rs. 5,000 - Rs. 8,000", "Rs. 8,000 - Rs. 12,000", "Above Rs. 12,000"};
        for (int i = 0; i < priceRanges.length; i++) {
            if (priceRanges[i].equals(priceRange)) return i;
        }
        return 0;
    }

    private int getDurationIndex(String duration) {
        String[] durations = {"All", "1 hour", "1.5 hours", "2 hours", "3+ hours"};
        for (int i = 0; i < durations.length; i++) {
            if (durations[i].equals(duration)) return i;
        }
        return 0;
    }
}
