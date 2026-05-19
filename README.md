# Kartsho – Zeerostock Mobile App Assignment 🚀

**Candidate**: Augutsya Pandey  
**Stack**: Compose Multiplatform (KMP), Firebase, Room, MVVM  

Kartsho is a high-performance mobile application prototype built using **Compose Multiplatform (CMP)**. It demonstrates a unified codebase for Android and iOS, featuring a marketplace, live bidding system, and role-based moderation.

To ensure production-grade reliability, I implemented an **Offline-First Architecture** using a local Room Database cache that synchronizes with Firebase Auth and Firestore.

---

## ✨ Core Features

### 1. 🔐 Role-Based Authentication
* **Multi-Role Support**: Distinct flows for **Buyers**, **Suppliers**, and **Admins**.
* **Secure Auth**: Powered by Firebase Authentication with local session persistence.
* **Pre-seeded Accounts**: Quick testing with dedicated demo credentials.

### 2. 🛒 Marketplace Module
* **Supplier Uploads**: Suppliers can list products with titles, descriptions, pricing, and images.
* **Smart Browsing**: Real-time search and category filtering (Deals, Popular, Top).
* **Detailed Previews**: Rich product detail screens with verified stock status badges.

### 3. 🔨 Live Auction System
* **Real-time Bidding**: Place bids and see updates instantly.
* **Countdown Timers**: Visual indicators for auction expiration.
* **Bid History**: Transparent tracking of previous bids per item.

### 4. 🛡️ Admin Moderation
* **User Insights**: View all registered users and their roles.
* **Product Queue**: Approve or reject supplier listings before they go live in the marketplace.
* **Global Oversight**: Full visibility into the system's product and auction ecosystem.

---

## 🎨 UI/UX Highlights
* **Dynamic Illustrations**: Built a custom `Canvas` drawing function (`DemoIllustrationBanner`) that renders beautiful gradients and gloss effects as placeholders when URLs aren't available.
* **Enhanced Visuals**: Large card layouts (`240dp`) for an engaging "eBay-meets-Amazon" shopping experience.
* **Native Feel**: 100% Shared UI using Material 3 components that adapt to both Android and iOS design languages.

---

## 🏗 Architecture & Tech Stack

The project follows **Clean Architecture** principles with a modular Gradle setup:

* **`:composeApp`**: Shared Kotlin Multiplatform module.
  * `commonMain`: 100% of UI (Compose), ViewModels, and Repository logic.
  * `androidMain` / `iosMain`: Platform-specific implementations for Room drivers and dependency injection.
* **`:app`**: Slim Android wrapper for deployment.

**Key Libraries**:
* **UI**: Compose Multiplatform, Material 3
* **Database**: Room KMP (SQLite)
* **Backend**: Firebase Auth & Firestore (GitLive SDK)
* **Image Loading**: Coil3 (KMP)
* **Concurrency**: Kotlin Coroutines & Flow

---

## 💡 Engineering Challenges & Solutions

### 1. Robust Firestore Failover
**Challenge**: Remote database requests can hang or fail on poor connections.  
**Solution**: Wrapped all Firestore queries in a `withTimeoutOrNull(2500L)` block. If a request exceeds 2.5s, the repository automatically falls back to the **Room Local Cache**, ensuring the UI never freezes.

### 2. KMP Dependency Injection
**Challenge**: Providing platform-specific dependencies (like Database Drivers) to a shared module.  
**Solution**: Implemented an `AppModule` using the `expect`/`actual` pattern. This allows the shared code to interact with a single interface while the platform modules handle the heavy lifting (Context on Android, Path discovery on iOS).

### 3. Manual Data Mapping
**Challenge**: Firebase "Serializer not found" errors when working with KMP.  
**Solution**: Instead of relying on complex plugins, I implemented type-safe mapping extensions on `DocumentSnapshot`. This approach eliminated crashes and provided better control over data validation during the deserialization process.

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Buyer** | `buyer@zeerostock.com` | `buyer123` | Shopping, Bidding, Checkout |
| **Supplier** | `supplier@zeerostock.com` | `supplier123` | Product Uploads, Stock Management |
| **Admin** | `admin@zeerostock.com` | `admin123` | Approval Queue, User Management |

---

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone [Repo-URL]
   ```
2. **Environment**: Ensure you have **Android Studio Ladybug (or newer)** and **JDK 17** configured.
3. **Build**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Deploy**: Run the `app` configuration on an emulator or physical device.

---
*Developed for the Zeerostock Mobile App Developer Assessment.*
