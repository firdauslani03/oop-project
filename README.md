# Member 2 (Daus) — Charging Station Management

This package contains everything for your module, built and tested against
your team's `iman` branch code so it compiles and runs correctly with the
rest of the system.

## How to apply this to your local clone

1. `git checkout daus` in your local clone of `firdauslani03/oop-project`.
2. Copy all files from this folder into your repo root (keeping the
   `Exception/` subfolder structure).
3. You'll also need the shared dependency files from `iman`'s branch if you
   don't already have them locally (Resident.java, Vehicle.java, User.java,
   Booking.java, BookingConflictException.java, TimeSlot.java,
   ResidentManager.java, PasswordUtil.java, ProfileMenu.java, VehicleMenu.java,
   WaitlistManager.java, WaitlistEntry.java, QueueStrategy.java,
   NormalQueueStrategy.java, QueueHistory.java, QueueRecord.java, QueueMenu.java,
   SmartQueueManagement.java, and the rest of the Exception/ package). Run:
   `git checkout iman -- Resident.java User.java Vehicle.java ResidentManager.java PasswordUtil.java TimeSlot.java NormalQueueStrategy.java QueueStrategy.java QueueHistory.java QueueRecord.java WaitlistEntry.java WaitlistManager.java SmartQueueManagement.java QueueMenu.java ProfileMenu.java VehicleMenu.java Exception/ resident.txt vehicle.txt queue.txt queueHistory.txt`
4. `javac *.java Exception/*.java -d out` then `cd out && java Main` to test.
5. `git add -A && git commit -m "Add Charging Station Management module" && git push origin daus`

## New files (your module)

- `ChargingStation.java` — abstract base class (Encapsulation, Inheritance, Polymorphism)
- `FastChargingStation.java`, `NormalChargingStation.java` — subtypes, each overrides `displayInfo()`
- `MaintenanceRecord.java` — simple record class for maintenance log entries
- `StationManager.java` — all business logic: add/remove stations, status updates,
  maintenance, usage statistics, manual "complete charging" demo feature, and
  file I/O to `station.txt` / `maintenance.txt`
- `StationMenu.java` — resident-facing, view-only station menu
- `AdminMenu.java` — admin-only menu wrapping all of `StationManager`'s admin features
- `Exception/DuplicateStationException.java`
- `Exception/InvalidStatusUpdateException.java`
- `Exception/StationNotFoundException.java`

## Modified files (integration with teammates' code)

- `Main.java` — wires up `StationManager`, adds an Admin Login option
  (hardcoded `admin` / `admin123` for the demo), adds "View Charging Stations"
  to the resident dashboard, and creates `StationManager` before
  `BookingManager` so bookings can resolve real stations on load.
- `BookingManager.java` — constructor now takes a `StationManager` so
  `loadBookingsFromFile()` looks up the real station object instead of
  building a throwaway one (this was only possible before because
  `ChargingStation` had no subclasses yet). Added a `findBooking(id)` helper
  used by `StationManager.completeCharging()`.
- `BookingMenu.java` — `handleBookSlot()` now looks up the station from
  `StationManager` and checks `isAvailable()` before allowing a booking,
  instead of constructing a placeholder station with just an ID. Constructor
  now takes a `StationManager` and a shared `Scanner`.
- `ResidentMenuUI.java` — this file appears to be an earlier/unused draft of
  the main dashboard (not referenced anywhere in `Main.java`). I patched its
  `BookingMenu` call site so the project still compiles; you and your
  teammates may want to confirm whether this file is still needed or can be
  deleted.

## Bug fix worth knowing about

`BookingMenu` (and `ResidentMenuUI`) were each creating their own
`new Scanner(System.in)` instead of reusing the one passed down from `Main`.
Multiple `Scanner` instances reading the same `System.in` stream can lose or
desync buffered input. I changed `BookingMenu` to accept and reuse the shared
`Scanner` like `ProfileMenu`, `VehicleMenu`, and `QueueMenu` already correctly
do. Worth mentioning to Iman since it's in code from their branch.

## How your module fits the assignment's OOP requirement list

- **Encapsulation** — station fields are private with getters/setters in `ChargingStation`.
- **Inheritance** — `FastChargingStation` / `NormalChargingStation extends ChargingStation`.
- **Polymorphism** — `displayInfo()` is overridden differently per subtype; `StationManager.viewAllStations()` calls it through the base type.
- **Association** — `ChargingStation` tracks booking IDs made against it; `Booking` holds a reference to a `ChargingStation`.
- **Exception Handling** — `DuplicateStationException`, `InvalidStatusUpdateException`, `StationNotFoundException`, all thrown/caught with specific messages.
- **File I/O** — `station.txt` and `maintenance.txt`, loaded on startup and saved after every change.

## Demo flow suggestion (for your 15-minute presentation slot)

1. Show `Main` start screen → Admin Login (`admin` / `admin123`).
2. View All Charging Stations (shows the seeded 5 Fast + 5 Normal).
3. Add a new station, then try adding the same ID again to show `DuplicateStationException`.
4. Update a station's status to Inactive, then try an invalid status string to show `InvalidStatusUpdateException`.
5. Mark a station under maintenance, view maintenance records, then resolve it.
6. Log out, log in as a resident, book a slot on a station.
7. Log back in as Admin, show Usage Statistics (booking count went up), then use "Manually Complete Charging" on that booking ID.
