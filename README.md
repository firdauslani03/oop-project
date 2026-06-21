# EV Community Management System

This project implements a small EV community management system demonstrating account, vehicle, and queue management features.

## Features

- Account management
  - Register account (Resident ID, name, password, IC, phone)
  - Login with password (hashed using SHA-256)
  - Change password (requires current password and strength check)
- Profile management
  - View profile (ID, name, IC, phone)
  - Update phone number
- Vehicle / EV management
  - Add vehicle with model and battery capacity (kWh)
  - Remove vehicle by model
  - View registered vehicles
- Queue & booking management
  - Join/leave waitlist for stations
  - Promote next resident (assign to station)
  - Queue history recording and reporting
  - Pluggable queue strategy (`NormalQueueStrategy` for FIFO)

## Files of interest

- `Main.java` — Main entry point that launches the full integrated system
- `ResidentMenuUI.java` — Resident dashboard UI
- `ResidentManager.java` — Account, profile, and vehicle management + persistence (`resident.txt`, `vehicle.txt`)
- `WaitlistManager.java` — Queue operations and persistence (`queue.txt`, `queueHistory.txt`)
- `SmartQueueManagement.java` — Non-interactive demo for queue behaviors
- `PasswordUtil.java`, `User.java` — Password hashing and authentication
- `QueueStrategy.java`, `NormalQueueStrategy.java` — Strategy pattern for queue selection

## Run

From the project folder (`d:\XAMPP\htdocs\oop-project`) compile and run:

```cmd
javac *.java
java Main
```

Interactive options:
- Register (1), Login (2), Exit (3)
- After login: view/update profile, change password, add/remove/view vehicles, manage bookings, logout

Non-interactive queue demo:

```cmd
java SmartQueueManagement
```

## Data persistence

- `resident.txt` — stored resident records (id,name,hashedPassword,ic,phone)
- `vehicle.txt` — vehicle records (residentId,model,batteryCapacity)
- `queue.txt` — current waitlist
- `queueHistory.txt` — promotion history

## Notes

- Password strength: minimum 8 chars, must include upper, lower, digit, and special character.
- Passwords are hashed before storage.

If you want, I can add README badges, examples of sample runs, or extend the UI to expose queue operations interactively.