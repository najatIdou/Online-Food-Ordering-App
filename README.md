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
<img width="407" height="843" alt="Picture2" src="https://github.com/user-attachments/assets/3ccb93dc-18b6-42c2-baf3-9d35c8508d18" />
<img width="406" height="843" alt="Picture3" src="https://github.com/user-attachments/assets/509f3b14-e341-4558-8573-1fe8e76fb265" />
<img width="178" height="368" alt="Picture4" src="https://github.com/user-attachments/assets/0b1b1fa4-08c1-4956-b355-2a9982ba1658" />
<img width="394" height="843" alt="Picture5" src="https://github.com/user-attachments/assets/d89177bc-f21e-4d3a-a31c-fe1806ec58fd" />
<img width="407" height="843" alt="Picture6" src="https://github.com/user-attachments/assets/10876d89-c986-4ef4-a7b1-7e4676027c4c" />
<img width="406" height="843" alt="Picture7" src="https://github.com/user-attachments/assets/48f884ea-2b2f-4900-b432-f976f92e9286" />
<img width="405" height="837" alt="Picture8" src="https://github.com/user-attachments/assets/8c18f50f-f0f2-4517-adca-0a4136f1cc89" />
<img width="405" height="843" alt="Picture9" src="https://github.com/user-attachments/assets/2a29ee92-eb10-4007-ba31-bd4c20ca1339" />
<img width="406" height="843" alt="Picture10" src="https://github.com/user-attachments/assets/637e883e-05ef-4648-b3b1-5b8d235b3fdc" />
<img width="406" height="843" alt="Picture11" src="https://github.com/user-attachments/assets/512291f8-824d-4b26-8065-a763a922e398" />
<img width="177" height="368" alt="Picture12" src="https://github.com/user-attachments/assets/4cdf10f9-f2e5-4e7d-b744-426df31a1c4b" />
<img width="178" height="368" alt="Picture13" src="https://github.com/user-attachments/assets/a7fbaea3-32a5-4420-ba41-64277ece785d" />
<img width="178" height="368" alt="Picture14" src="https://github.com/user-attachments/assets/a94827b8-dad0-456c-9639-378a92adab97" />
<img width="406" height="843" alt="Picture15" src="https://github.com/user-attachments/assets/bc16ce4d-c053-4bf1-8890-d21dae30d6c8" />
<img width="348" height="724" alt="Picture16" src="https://github.com/user-attachments/assets/569e90d1-7fd6-409e-9182-722ed5965162" />
<img width="405" height="842" alt="Picture17" src="https://github.com/user-attachments/assets/4060e1ec-f5bb-4b46-8507-2e6b87f84f17" />
<img width="177" height="368" alt="Picture18" src="https://github.com/user-attachments/assets/e1685f4e-0ec3-481a-906b-8e246893d808" />
<img width="409" height="843" alt="Picture19" src="https://github.com/user-attachments/assets/172b1a75-e67b-48d5-87fb-14ed1986da7d" />
<img width="406" height="843" alt="Picture20" src="https://github.com/user-attachments/assets/5a05818c-cb85-41b2-81be-29ff3a338bef" />
<img width="367" height="762" alt="Picture21" src="https://github.com/user-attachments/assets/4612b30e-3576-4f54-b6f3-458e9792327e" />
<img width="407" height="843" alt="Picture22" src="https://github.com/user-attachments/assets/a51d10d7-4381-4850-8fd6-48c3169f628a" />
<img width="407" height="843" alt="Picture22" src="https://github.com/user-attachments/assets/fecf4f5c-5ef3-45dd-a920-c54a6b4add95" />
<img width="373" height="843" alt="Picture24" src="https://github.com/user-attachments/assets/3610843a-9f4d-4fed-a91a-5d55d128d4ef" />

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
