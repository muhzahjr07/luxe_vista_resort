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

public class RoomsActivity extends BaseActivity {

    private DatabaseHelper db;
    private RecyclerView rvRooms;
    private RoomsAdapter adapter;
    private List<Room> allRooms;
    private List<Room> filteredRooms;
    
    // Filter states
    private String selectedRoomType = "All";
    private String selectedPriceRange = "All";
    private String selectedCapacity = "All";
    private String currentSort = "price_asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rooms);

        db = new DatabaseHelper(this);
        setupViews();
        loadRooms();
        setupFilterButtons();
        setupSortButtons();
    }

    private void setupViews() {
        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        
        allRooms = new ArrayList<>();
        filteredRooms = new ArrayList<>();
    }

    private void loadRooms() {
        allRooms.clear();
        Cursor c = db.getAllRooms();
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            double price = c.getDouble(3);
            String image = c.getString(4);
            String amenities = c.getString(5);
            int capacity = c.getInt(6);
            allRooms.add(new Room(id, name, desc, price, image, amenities, capacity));
        }
        c.close();
        
        applyFiltersAndSort();
    }

    private void setupFilterButtons() {
        Button btnFilterType = findViewById(R.id.btnFilterType);
        Button btnFilterPrice = findViewById(R.id.btnFilterPrice);
        Button btnFilterCapacity = findViewById(R.id.btnFilterCapacity);
        Button btnClearFilters = findViewById(R.id.btnClearFilters);

        btnFilterType.setOnClickListener(v -> showRoomTypeFilter());
        btnFilterPrice.setOnClickListener(v -> showPriceRangeFilter());
        btnFilterCapacity.setOnClickListener(v -> showCapacityFilter());
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }

    private void setupSortButtons() {
        Button btnSortPrice = findViewById(R.id.btnSortPrice);
        Button btnSortName = findViewById(R.id.btnSortName);
        Button btnSortCapacity = findViewById(R.id.btnSortCapacity);

        btnSortPrice.setOnClickListener(v -> sortRooms("price"));
        btnSortName.setOnClickListener(v -> sortRooms("name"));
        btnSortCapacity.setOnClickListener(v -> sortRooms("capacity"));
    }

    private void showRoomTypeFilter() {
        String[] roomTypes = {"All", "Suite", "Villa", "Room", "Bungalow"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Room Type")
                .setSingleChoiceItems(roomTypes, getRoomTypeIndex(selectedRoomType), (dialog, which) -> {
                    selectedRoomType = roomTypes[which];
                    updateFilterButton(R.id.btnFilterType, "Room Type", selectedRoomType);
                    applyFiltersAndSort();
                    dialog.dismiss();
                })
                .show();
    }

    private void showPriceRangeFilter() {
        String[] priceRanges = {"All", "Under Rs. 25,000", "Rs. 25,000 - Rs. 40,000", "Rs. 40,000 - Rs. 60,000", "Above Rs. 60,000"};
        
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

    private void showCapacityFilter() {
        String[] capacities = {"All", "2 Guests", "4+ Guests"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Capacity")
                .setSingleChoiceItems(capacities, getCapacityIndex(selectedCapacity), (dialog, which) -> {
                    selectedCapacity = capacities[which];
                    updateFilterButton(R.id.btnFilterCapacity, "Capacity", selectedCapacity);
                    applyFiltersAndSort();
                    dialog.dismiss();
                })
                .show();
    }

    private void clearAllFilters() {
        selectedRoomType = "All";
        selectedPriceRange = "All";
        selectedCapacity = "All";
        
        updateFilterButton(R.id.btnFilterType, "Room Type", "All");
        updateFilterButton(R.id.btnFilterPrice, "Price Range", "All");
        updateFilterButton(R.id.btnFilterCapacity, "Capacity", "All");
        
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

    private void sortRooms(String sortType) {
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
            case "capacity": return R.id.btnSortCapacity;
            default: return R.id.btnSortPrice;
        }
    }

    private void applyFiltersAndSort() {
        filteredRooms.clear();
        
        // Apply filters
        for (Room room : allRooms) {
            if (matchesRoomType(room) && matchesPriceRange(room) && matchesCapacity(room)) {
                filteredRooms.add(room);
            }
        }
        
        // Apply sorting
        Collections.sort(filteredRooms, getComparator());
        
        // Update adapter
        if (adapter == null) {
            adapter = new RoomsAdapter(this, filteredRooms);
            rvRooms.setAdapter(adapter);
        } else {
            adapter.updateRooms(filteredRooms);
        }
        
        // Show results count
        Toast.makeText(this, filteredRooms.size() + " rooms found", Toast.LENGTH_SHORT).show();
    }

    private boolean matchesRoomType(Room room) {
        if (selectedRoomType.equals("All")) return true;
        
        String name = room.name.toLowerCase();
        switch (selectedRoomType) {
            case "Suite": return name.contains("suite");
            case "Villa": return name.contains("villa");
            case "Room": return name.contains("room");
            case "Bungalow": return name.contains("bungalow");
            default: return true;
        }
    }

    private boolean matchesPriceRange(Room room) {
        if (selectedPriceRange.equals("All")) return true;
        
        double price = room.price;
        switch (selectedPriceRange) {
            case "Under Rs. 25,000": return price < 25000;
            case "Rs. 25,000 - Rs. 40,000": return price >= 25000 && price <= 40000;
            case "Rs. 40,000 - Rs. 60,000": return price >= 40000 && price <= 60000;
            case "Above Rs. 60,000": return price > 60000;
            default: return true;
        }
    }

    private boolean matchesCapacity(Room room) {
        if (selectedCapacity.equals("All")) return true;
        
        switch (selectedCapacity) {
            case "2 Guests": return room.capacity == 2;
            case "4+ Guests": return room.capacity >= 4;
            default: return true;
        }
    }

    private Comparator<Room> getComparator() {
        String sortType = currentSort.split("_")[0];
        boolean ascending = currentSort.endsWith("_asc");
        
        switch (sortType) {
            case "price":
                return ascending ? 
                    Comparator.comparingDouble(room -> room.price) :
                    Comparator.comparingDouble((Room room) -> room.price).reversed();
            case "name":
                return ascending ?
                    Comparator.comparing(room -> room.name) :
                    Comparator.comparing((Room room) -> room.name).reversed();
            case "capacity":
                return ascending ?
                    Comparator.comparingInt(room -> room.capacity) :
                    Comparator.comparingInt((Room room) -> room.capacity).reversed();
            default:
                return Comparator.comparingDouble(room -> room.price);
        }
    }

    private int getRoomTypeIndex(String roomType) {
        String[] roomTypes = {"All", "Suite", "Villa", "Room", "Bungalow"};
        for (int i = 0; i < roomTypes.length; i++) {
            if (roomTypes[i].equals(roomType)) return i;
        }
        return 0;
    }

    private int getPriceRangeIndex(String priceRange) {
        String[] priceRanges = {"All", "Under Rs. 25,000", "Rs. 25,000 - Rs. 40,000", "Rs. 40,000 - Rs. 60,000", "Above Rs. 60,000"};
        for (int i = 0; i < priceRanges.length; i++) {
            if (priceRanges[i].equals(priceRange)) return i;
        }
        return 0;
    }

    private int getCapacityIndex(String capacity) {
        String[] capacities = {"All", "2 Guests", "4+ Guests"};
        for (int i = 0; i < capacities.length; i++) {
            if (capacities[i].equals(capacity)) return i;
        }
        return 0;
    }
}
