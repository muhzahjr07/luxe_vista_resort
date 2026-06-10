# LuxeVista Resort - Android App

A comprehensive hotel and resort management Android application built with Java and Material Design.

## 🌟 Features

### User Authentication
- **User Registration**: Create new accounts with email validation
- **Secure Login**: Email-based authentication with session management
- **Session Persistence**: Stay logged in across app sessions

### Room Management
- **Browse Rooms**: View all available rooms with detailed information
- **Room Details**: Comprehensive room information including amenities and capacity
- **Room Booking**: Select check-in/check-out dates and book rooms
- **Price Calculation**: Automatic total price calculation based on stay duration

### Service Management
- **Service Catalog**: Browse resort services by category
- **Service Details**: Detailed service information with availability and duration
- **Service Booking**: Book services with date and time selection
- **Category Filtering**: Services organized by Wellness, Dining, Recreation, etc.

### Booking Management
- **View Bookings**: See all room and service bookings in one place
- **Booking History**: Track past and upcoming reservations
- **Booking Details**: Complete booking information with pricing

### Resort Information
- **About Resort**: Comprehensive resort information and amenities
- **Local Attractions**: Nearby attractions and activities
- **Contact Information**: Direct contact options (call, email, website, location)
- **Resort Amenities**: Complete list of available facilities

### Additional Features
- **Promotional Notifications**: Special offers and promotions
- **Material Design**: Modern, beautiful UI with consistent theming
- **Responsive Layout**: Optimized for different screen sizes
- **Offline Capability**: Local SQLite database for data storage

## 🏗️ Technical Architecture

### Technology Stack
- **Language**: Java (Primary), Kotlin (UI Themes)
- **UI Framework**: Material Design Components
- **Database**: SQLite with custom DatabaseHelper
- **Architecture**: Traditional Android Activities with RecyclerView
- **Target SDK**: 36 (Android 14+)
- **Min SDK**: 21 (Android 5.0+)

### Database Schema
- **Users Table**: User authentication and profile data
- **Rooms Table**: Room information, pricing, and amenities
- **Services Table**: Service details, categories, and availability
- **Room Bookings Table**: Room reservation data
- **Service Bookings Table**: Service reservation data

### Key Components
- **Activities**: LoginActivity, RegisterActivity, MainActivity, RoomsActivity, ServicesActivity, BookingsActivity, InfoActivity, RoomDetailActivity, ServiceDetailActivity
- **Models**: Room, Service, Booking, User
- **Adapters**: RoomsAdapter, ServicesAdapter, BookingsAdapter
- **Helpers**: DatabaseHelper, SessionManager, NotificationHelper

## 🎨 Design System

### Color Palette
- **Primary**: Teal (#00695C) - Main brand color
- **Secondary**: Gold (#FFD740) - Accent color
- **Background**: Light Gray (#F5F5F5) - App background
- **Surface**: White (#FFFFFF) - Card backgrounds
- **Text**: Dark Gray (#212121) - Primary text

### UI Components
- **Material Cards**: Elevated cards with rounded corners
- **Material Buttons**: Consistent button styling with ripple effects
- **Text Input Layouts**: Outlined text fields with validation
- **RecyclerViews**: Efficient list display with custom adapters
- **Date/Time Pickers**: Native Android pickers for booking

## 📱 Screenshots

The app includes the following main screens showing the mobile layout:

| | |
|:---:|:---:|
| **Login & Onboarding** | **Main Dashboard** |
| ![Login & Onboarding](Screenshots/image%207.png) | ![Main Dashboard](Screenshots/image%208.png) |
| **Room Booking List** | **Service Booking Catalog** |
| ![Rooms List](Screenshots/image%209.png) | ![Services List](Screenshots/image%2010.png) |
| **Reservation Details** | **Resort Info & Settings** |
| ![Bookings](Screenshots/image%2011.png) | ![Resort Info](Screenshots/image%2012.png) |


## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21+ (Android 5.0+)
- Java 11+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on device/emulator

### Database Setup
The app automatically creates the database and populates it with sample data on first launch. No manual setup required.

## 📊 Sample Data

The app comes pre-loaded with:
- **6 Room Types**: From budget-friendly to luxury suites
- **6 Service Categories**: Spa, dining, water sports, tours, concierge, transportation
- **Realistic Pricing**: Competitive resort pricing structure
- **Detailed Descriptions**: Comprehensive room and service information

## 🔧 Customization

### Adding New Rooms
1. Modify the `insertSampleData()` method in `DatabaseHelper.java`
2. Add room details to the rooms array
3. Rebuild the app

### Adding New Services
1. Update the services array in `insertSampleData()` method
2. Add service categories as needed
3. Rebuild the app

### Styling Changes
1. Modify colors in `res/values/colors.xml`
2. Update themes in `res/values/themes.xml`
3. Customize layouts in `res/layout/` directory

## 🛡️ Security Features

- **Input Validation**: Email format validation and required field checks
- **Session Management**: Secure user session handling
- **Data Sanitization**: Proper handling of user inputs
- **SQL Injection Prevention**: Parameterized queries

## 🔮 Future Enhancements

- **Password Hashing**: Implement bcrypt or similar for password security
- **Push Notifications**: Real-time booking confirmations and updates
- **Payment Integration**: Stripe or PayPal integration for payments
- **Image Gallery**: Room and service photo galleries
- **Reviews & Ratings**: User review system
- **Offline Sync**: Cloud synchronization when online
- **Multi-language Support**: Internationalization
- **Dark Mode**: Theme switching capability

## 📄 License

This project is created for educational and demonstration purposes.

## 👥 Contributing

This is a complete, production-ready app. For modifications or enhancements, please follow standard Android development practices and maintain the existing code structure.

## 📞 Support

For technical support or questions about the app implementation, please refer to the code comments and documentation within the source files.

---

**LuxeVista Resort** - Where luxury meets paradise! 🌴✨
