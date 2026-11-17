# Online Food Ordering Android Application

An Android-based food ordering platform developed using Java and Firebase Realtime Database. The application connects customers, restaurant owners, and administrators through a centralized system for restaurant browsing, food ordering, restaurant management, and order processing.

This project was developed as my Bachelor's graduation project in Network Engineering at Anhui University of Technology, China (2026).

## Overview

The application provides three different user roles, each with dedicated interfaces and functionalities:

- **Customers:** Browse restaurants and food items, manage shopping carts, place orders, make simulated payments, and rate completed orders.
- **Restaurant Owners:** Manage restaurant information, food menus, restaurant availability, and incoming customer orders.
- **Administrators:** Manage users and restaurants, monitor orders, and view platform statistics and revenue analytics.

Firebase Realtime Database synchronizes application data across the different user interfaces, allowing changes to orders, restaurant information, and menus to be reflected in real time.

## Features

### Customer Module
- User registration and authentication.
- Browse restaurants and food categories.
- Search and filter food items by location, preparation time, and price.
- View restaurant and food details.
- Add, remove, and update shopping cart items.
- Manage multiple delivery addresses.
- Place orders using cash-on-delivery or simulated card payments.
- View order history and real-time order status updates.
- Cancel pending orders.
- Manage personal profile information.
- Rate restaurants and food items after completed orders.

### Restaurant Owner Module
- Restaurant registration and profile management.
- Restaurant dashboard.
- Add, edit, and delete food items.
- Manage food categories and menu information.
- Set restaurant availability (open/closed).
- Receive and manage customer orders.
- Accept, reject, and complete orders.

### Administrator Module
- Administrative dashboard.
- Manage customer and restaurant owner accounts.
- Manage restaurant information.
- Monitor platform orders.
- View system statistics.
- Analyze order activity, revenue, and user growth.

## Technologies & Tools

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Android Studio | Android development environment |
| Firebase Realtime Database | Cloud database and real-time synchronization |
| Firebase Authentication | User authentication |
| XML | User interface layouts |
| View Binding | Access UI components |
| RecyclerView | Display dynamic lists |
| Glide | Image loading and caching |
| SharedPreferences | Local session and user state management |
| Gradle | Build automation |

## Application Architecture

The application follows a role-based structure, providing different functionalities according to the authenticated user's role.

**Main application workflow:**

1. Application launch and introductory screen.
2. User login or registration.
3. Role-based navigation.
4. Access to the corresponding dashboard.
5. Interaction with Firebase services.
6. Real-time updates across the application.

### Database Design

Firebase Realtime Database is used as the primary cloud database. Data is organized into JSON-based structures covering the main application entities:

- Users
- Restaurants
- Foods
- Categories
- Orders
- Order Items
- Addresses
- Locations
- Durations
- Payment Methods
- Food Ratings
- Restaurant Ratings

The database supports the application's multi-role functionality and real-time data synchronization.

## Functional Testing

Functional testing was conducted to verify the main application workflows and ensure that the different modules behaved according to their requirements.

Testing covered:

- Login and registration.
- Food searching and filtering.
- Food details and cart management.
- Delivery address management.
- Order placement and simulated payments.
- Order cancellation and status updates.
- Restaurant and menu management.
- Order processing by restaurant owners.
- Administrator user and order management.
- Dashboard statistics and analytics.

## Screenshots

Application screenshots will be added here to demonstrate the customer, restaurant owner, and administrator interfaces.

<img width="406" height="843" alt="Picture1" src="https://github.com/user-attachments/assets/3f0c04af-b1ba-49b1-b845-1e0da59cfe95" />


## Installation and Setup

### Prerequisites
- Android Studio.
- Android SDK 24 or later.
- JDK compatible with the project's Gradle configuration.
- A Firebase project with Realtime Database and Authentication enabled.

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/najatIdou/Online-Food-Ordering-App.git
   ```

2. Open the project in Android Studio.

3. Configure your Firebase project and register the Android application using its application package name.

4. Add your own `google-services.json` file to the app module directory.

5. Configure the required Firebase services and database security rules.

6. Sync the project with Gradle.

7. Build and run the application on an Android emulator or compatible physical device.

**Note:** Private service account credentials and other sensitive configuration files are not included in this repository.

## Current Limitations and Future Improvements

Some advanced functionalities are outside the scope of the current implementation.

Potential improvements include:

- Integration with a real payment gateway.
- GPS-based delivery tracking.
- Push notifications for order updates.
- AI-based food recommendation system.
- Further UI/UX improvements.
- Expanded automated testing.

## Academic Project

**Project Title:** The Design and Implementation of an Online Marketplace Android Application

**Degree:** Bachelor of Network Engineering

**Institution:** Anhui University of Technology, China

**Year:** 2026

**Development:** Java, Android Studio, Firebase

---

**Author:** Najat Idouhazli

**GitHub:** [github.com/najatIdou](https://github.com/najatIdou)
