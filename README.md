# Codigo Code Test

## How to Run the Application

1. Make sure you have Docker and Docker Compose installed on your system.
2. Navigate to the project directory.
3. Run the following command to start the application and its dependencies:

   ```bash
   docker-compose up --build

The application will be available at http://localhost:8080.

API Testing
A Postman collection is included to help you test the available APIs easily.
You can import the collection into Postman using the provided JSON file.

File: Booking-Service.postman_collection.json

Database Schema
A visual diagram of the database schema is available to illustrate the entity relationships in the system.

File: database-schema.png

# Project Documentation

## Overview

This project is a modular class booking system built with a focus on clean architecture, user experience, and performance. The system includes modules for user management, package handling, and course booking—with concurrency controls and session-aware authentication.

---

## Modules

### User Module

Handles user authentication, profile management, and secure multi-device support.

#### Features:
- **Registration & Login**: Users can register and log in using their credentials.
- **Logout with Multi-Device Support**: Logout supports `deviceId`-based session invalidation using Redis.
- **Change Password**: Users can securely update their passwords.
- **Reset Password**: Password reset functionality via secure token or email-based mechanism.
- **Profile View**: Users can view:
  - Account information
  - Purchased packages
  - Booked courses

---

### Package Module

Handles package display and purchase logic, with intelligent expiration handling.

#### Features:
- **View Packages**: All available packages are visible to users.
- **Purchase Package**: Users can purchase one or more packages.
- **Smart Expiry Handling**:
  - If a user purchases a **package of the same country**, the existing expiration date is **extended** instead of replaced.

---

### Course Module

Handles course booking, concurrency management, and waitlist processing.

#### Features:
- **Book Courses**:
  - Uses Redis to **check available slots** before hitting the database to ensure high performance and avoid overbooking.
  - Users **cannot book multiple courses at the same time slot**.
- **Cancel Booking**:
  - Users can **cancel within 4 hours** of booking.
  - Cancelled slots are **refunded**.
- **Waitlist Handling**:
  - If a slot becomes available (due to cancellation), a user from the **waitlist is auto-promoted** to a confirmed booking.

---

## Technologies

- **Spring Boot** (Core framework)
- **Redis** (Session handling, caching, and concurrency control)
- **JWT** (Authentication)
- **JPA/Hibernate** (Database ORM)
- **Docker** (Containerization for consistent deployment)

---

## Known Limitations (Time-Constrained)

Due to the one-day time constraint of the challenge, the following features were planned but not fully implemented:

- **Role-Based Authorization**: Currently, the system assumes all users have the same permissions.
- **Cache Invalidation**: No logic to invalidate Redis cache on updates (e.g., user profile or booking changes).

These would be next priorities in a production-ready system.
