package com.example.luxevistaresort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "luxevista.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL, "
                + COL_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // Create rooms table
        String createRoomsTable = "CREATE TABLE rooms ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "price REAL NOT NULL, "
                + "image TEXT, "
                + "amenities TEXT, "
                + "capacity INTEGER)";
        db.execSQL(createRoomsTable);

        // Create services table
        String createServicesTable = "CREATE TABLE services ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "price REAL NOT NULL, "
                + "availability TEXT, "
                + "duration TEXT, "
                + "category TEXT)";
        db.execSQL(createServicesTable);

        // Create room bookings table
        String createRoomBookingsTable = "CREATE TABLE room_bookings ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "room_id INTEGER NOT NULL, "
                + "check_in_date TEXT NOT NULL, "
                + "check_out_date TEXT NOT NULL, "
                + "total_price REAL NOT NULL, "
                + "status TEXT DEFAULT 'confirmed', "
                + "FOREIGN KEY(user_id) REFERENCES users(id), "
                + "FOREIGN KEY(room_id) REFERENCES rooms(id))";
        db.execSQL(createRoomBookingsTable);

        // Create service bookings table
        String createServiceBookingsTable = "CREATE TABLE service_bookings ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "service_id INTEGER NOT NULL, "
                + "booking_date TEXT NOT NULL, "
                + "booking_time TEXT NOT NULL, "
                + "total_price REAL NOT NULL, "
                + "status TEXT DEFAULT 'confirmed', "
                + "FOREIGN KEY(user_id) REFERENCES users(id), "
                + "FOREIGN KEY(service_id) REFERENCES services(id))";
        db.execSQL(createServiceBookingsTable);

        // Create reviews table
        String createReviewsTable = "CREATE TABLE reviews ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "item_id INTEGER NOT NULL, "
                + "review_type TEXT NOT NULL, "
                + "reviewer_name TEXT NOT NULL, "
                + "review_text TEXT NOT NULL, "
                + "rating INTEGER NOT NULL, "
                + "review_date TEXT NOT NULL, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createReviewsTable);

        // Create favorites table
        String createFavoritesTable = "CREATE TABLE favorites ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "item_id INTEGER NOT NULL, "
                + "item_type TEXT NOT NULL, "
                + "added_date TEXT NOT NULL, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createFavoritesTable);

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS room_bookings");
        db.execSQL("DROP TABLE IF EXISTS service_bookings");
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS rooms");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Insert sample rooms
        String[] roomNames = {
            "Colombo Ocean Suite", "Kandy Garden Villa", "Presidential Suite", 
            "Deluxe Room", "Bentota Beach Bungalow", "Ella Mountain View Room"
        };
        String[] roomDescriptions = {
            "Luxurious suite with panoramic Indian Ocean views, private balcony, and premium amenities.",
            "Spacious villa surrounded by tropical Sri Lankan gardens with private pool access.",
            "The ultimate luxury experience with butler service and exclusive amenities.",
            "Comfortable room with modern amenities and resort views of the beautiful landscape.",
            "Charming bungalow steps away from the pristine beaches of Bentota.",
            "Cozy room with stunning views of the Ella mountains and tea plantations."
        };
        double[] roomPrices = {45000.0, 38000.0, 75000.0, 22000.0, 32000.0, 18000.0};
        String[] roomAmenities = {
            "Indian Ocean view, Private balcony, Mini bar, Room service, Air conditioning",
            "Garden view, Pool access, Kitchenette, Daily housekeeping, Mosquito nets",
            "City view, Butler service, Jacuzzi, Premium toiletries, 24/7 concierge",
            "Resort view, Tea/coffee maker, Work desk, Free WiFi, Ceiling fan",
            "Beach view, Private terrace, Outdoor shower, Beach chairs, Sea breeze",
            "Mountain view, Fireplace, Balcony, Hiking maps, Tea plantation views"
        };
        int[] roomCapacities = {2, 4, 2, 2, 2, 2};

        for (int i = 0; i < roomNames.length; i++) {
            ContentValues roomValues = new ContentValues();
            roomValues.put("name", roomNames[i]);
            roomValues.put("description", roomDescriptions[i]);
            roomValues.put("price", roomPrices[i]);
            roomValues.put("amenities", roomAmenities[i]);
            roomValues.put("capacity", roomCapacities[i]);
            roomValues.put("image", "room_" + (i + 1));
            db.insert("rooms", null, roomValues);
        }

        // Insert sample services
        String[] serviceNames = {
            "Ayurvedic Spa & Wellness", "Sri Lankan Cuisine", "Water Sports", 
            "Cultural Heritage Tours", "Concierge Service", "Airport Transfer"
        };
        String[] serviceDescriptions = {
            "Traditional Ayurvedic spa treatments including herbal massages, facials, and wellness therapies.",
            "Authentic Sri Lankan dining experience with local chefs and fresh island ingredients.",
            "Exciting water activities including snorkeling, diving, and boat tours in the Indian Ocean.",
            "Expert-guided tours to UNESCO World Heritage sites and cultural attractions.",
            "Personalized concierge service for all your resort and travel needs in Sri Lanka.",
            "Complimentary airport pickup and drop-off service from Colombo International Airport."
        };
        double[] servicePrices = {12000.0, 8500.0, 6500.0, 4500.0, 2500.0, 3500.0};
        String[] serviceAvailability = {
            "Daily 9AM-9PM", "Daily 6PM-11PM", "Daily 8AM-6PM", 
            "Daily 9AM-5PM", "24/7", "On request"
        };
        String[] serviceDuration = {
            "2 hours", "1.5 hours", "3 hours", "4 hours", "As needed", "1 hour"
        };
        String[] serviceCategories = {
            "Ayurvedic Wellness", "Sri Lankan Dining", "Water Recreation", "Cultural Tours", "Concierge", "Transportation"
        };

        for (int i = 0; i < serviceNames.length; i++) {
            ContentValues serviceValues = new ContentValues();
            serviceValues.put("name", serviceNames[i]);
            serviceValues.put("description", serviceDescriptions[i]);
            serviceValues.put("price", servicePrices[i]);
            serviceValues.put("availability", serviceAvailability[i]);
            serviceValues.put("duration", serviceDuration[i]);
            serviceValues.put("category", serviceCategories[i]);
            db.insert("services", null, serviceValues);
        }

        // Insert sample reviews
        String[] reviewerNames = {
            "Sarah Johnson", "Michael Chen", "Emma Williams", "David Brown", "Lisa Garcia",
            "James Wilson", "Maria Rodriguez", "John Smith", "Anna Taylor", "Robert Davis"
        };
        String[] reviewTexts = {
            "Absolutely amazing experience! The ocean view suite was breathtaking and the service was impeccable.",
            "The spa treatment was incredibly relaxing. The staff was professional and the facilities were top-notch.",
            "Perfect location with beautiful beaches nearby. The room was clean and comfortable.",
            "The cultural tour was educational and fun. Our guide was knowledgeable and friendly.",
            "Excellent dining experience with authentic Sri Lankan cuisine. Highly recommended!",
            "The water sports activities were thrilling. Great equipment and safety measures.",
            "Outstanding concierge service. They helped us plan our entire stay perfectly.",
            "Beautiful resort with stunning architecture. The gardens are well-maintained.",
            "The Ayurvedic wellness package was transformative. Feel completely rejuvenated.",
            "Great value for money. The airport transfer was convenient and comfortable."
        };
        int[] ratings = {5, 5, 4, 5, 4, 5, 5, 4, 5, 4};
        String[] reviewTypes = {"room", "service", "room", "service", "service", "service", "service", "room", "service", "service"};
        long[] itemIds = {1, 1, 2, 4, 2, 3, 5, 3, 1, 6}; // Corresponding room/service IDs
        String[] reviewDates = {
            "2024-01-10", "2024-01-12", "2024-01-15", "2024-01-18", "2024-01-20",
            "2024-01-22", "2024-01-25", "2024-01-28", "2024-01-30", "2024-02-02"
        };

        for (int i = 0; i < reviewerNames.length; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put("user_id", 1); // Sample user ID
            reviewValues.put("item_id", itemIds[i]);
            reviewValues.put("review_type", reviewTypes[i]);
            reviewValues.put("reviewer_name", reviewerNames[i]);
            reviewValues.put("review_text", reviewTexts[i]);
            reviewValues.put("rating", ratings[i]);
            reviewValues.put("review_date", reviewDates[i]);
            db.insert("reviews", null, reviewValues);
        }
    }

    // Register user
    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_USERS, null, values);
    }

    // Login (check by email & password)
    public long loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long userId = cursor.getLong(0);
            cursor.close();
            return userId;
        }
        return -1;
    }
    public Cursor getRoomBookingsForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT rb.*, r.name as room_name, r.price as room_price FROM room_bookings rb " +
                "JOIN rooms r ON rb.room_id = r.id WHERE rb.user_id=?", 
                new String[]{String.valueOf(userId)});
    }

    public Cursor getServiceBookingsForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT sb.*, s.name as service_name, s.price as service_price FROM service_bookings sb " +
                "JOIN services s ON sb.service_id = s.id WHERE sb.user_id=?", 
                new String[]{String.valueOf(userId)});
    }

    public long addRoomBooking(long userId, long roomId, String checkInDate, String checkOutDate, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("room_id", roomId);
        values.put("check_in_date", checkInDate);
        values.put("check_out_date", checkOutDate);
        values.put("total_price", totalPrice);
        return db.insert("room_bookings", null, values);
    }

    public Cursor getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM rooms ORDER BY price ASC", null);
    }

    public Cursor getRoomById(long roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM rooms WHERE id=?", new String[]{String.valueOf(roomId)});
    }

    public long addServiceBooking(long userId, long serviceId, String bookingDate, String bookingTime, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("service_id", serviceId);
        values.put("booking_date", bookingDate);
        values.put("booking_time", bookingTime);
        values.put("total_price", totalPrice);
        return db.insert("service_bookings", null, values);
    }

    public Cursor getAllServices() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM services ORDER BY category, name", null);
    }

    public Cursor getServiceById(long serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM services WHERE id=?", new String[]{String.valueOf(serviceId)});
    }

    public Cursor getServicesByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM services WHERE category=? ORDER BY name", new String[]{category});
    }

    // Optional: get username by email
    public String getUsernameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USERNAME},
                COL_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
        return null;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                new String[]{COL_ID, COL_USERNAME, COL_EMAIL, COL_PASSWORD},
                COL_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );
    }

    public boolean updateUserProfile(long userId, String username, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        
        int updatedRows = db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{String.valueOf(userId)});
        return updatedRows > 0;
    }

    // Review methods
    public long addReview(long userId, long itemId, String reviewType, String reviewerName, String reviewText, int rating, String reviewDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("item_id", itemId);
        values.put("review_type", reviewType);
        values.put("reviewer_name", reviewerName);
        values.put("review_text", reviewText);
        values.put("rating", rating);
        values.put("review_date", reviewDate);
        return db.insert("reviews", null, values);
    }

    public Cursor getReviewsForItem(long itemId, String reviewType) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reviews WHERE item_id=? AND review_type=? ORDER BY review_date DESC", 
                new String[]{String.valueOf(itemId), reviewType});
    }

    public Cursor getAllReviews() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reviews ORDER BY review_date DESC", null);
    }

    public double getAverageRating(long itemId, String reviewType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(rating) FROM reviews WHERE item_id=? AND review_type=?", 
                new String[]{String.valueOf(itemId), reviewType});
        
        if (cursor.moveToFirst()) {
            double avg = cursor.getDouble(0);
            cursor.close();
            return avg;
        }
        cursor.close();
        return 0.0;
    }

    // Favorites methods
    public long addToFavorites(long userId, long itemId, String itemType, String addedDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("item_id", itemId);
        values.put("item_type", itemType);
        values.put("added_date", addedDate);
        return db.insert("favorites", null, values);
    }

    public boolean removeFromFavorites(long userId, long itemId, String itemType) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("favorites", 
                "user_id=? AND item_id=? AND item_type=?", 
                new String[]{String.valueOf(userId), String.valueOf(itemId), itemType});
        return deletedRows > 0;
    }

    public boolean isFavorite(long userId, long itemId, String itemType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("favorites", 
                new String[]{"id"}, 
                "user_id=? AND item_id=? AND item_type=?", 
                new String[]{String.valueOf(userId), String.valueOf(itemId), itemType}, 
                null, null, null);
        
        boolean isFav = cursor.getCount() > 0;
        cursor.close();
        return isFav;
    }

    public Cursor getFavoritesForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM favorites WHERE user_id=? ORDER BY added_date DESC", 
                new String[]{String.valueOf(userId)});
    }

    // Booking update methods
    public Cursor getRoomBookingById(long bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM room_bookings WHERE id=?", 
                new String[]{String.valueOf(bookingId)});
    }

    public Cursor getServiceBookingById(long bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM service_bookings WHERE id=?", 
                new String[]{String.valueOf(bookingId)});
    }

    public boolean updateRoomBooking(long bookingId, long userId, String checkInDate, String checkOutDate, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("check_in_date", checkInDate);
        values.put("check_out_date", checkOutDate);
        values.put("total_price", totalPrice);
        
        int updatedRows = db.update("room_bookings", values, 
                "id=? AND user_id=?", 
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        return updatedRows > 0;
    }

    public boolean updateServiceBooking(long bookingId, long userId, String bookingDate, String bookingTime, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("booking_date", bookingDate);
        values.put("booking_time", bookingTime);
        values.put("total_price", totalPrice);
        
        int updatedRows = db.update("service_bookings", values, 
                "id=? AND user_id=?", 
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        return updatedRows > 0;
    }

    // Delete room booking
    public boolean deleteRoomBooking(long bookingId, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("room_bookings", 
                "id=? AND user_id=?", 
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        return deletedRows > 0;
    }

    // Delete service booking
    public boolean deleteServiceBooking(long bookingId, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("service_bookings", 
                "id=? AND user_id=?", 
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        return deletedRows > 0;
    }
}
