# File Attribution

This table shows which branch/member each file in the merged project
originated from, verified against actual git commit history (not guessed
from filenames). Every `.java` file in the project also has a one-line
`// [Module: ...]` comment at the top matching this table.

How this was verified: `git merge-base --is-ancestor` confirms both
`jiabao`'s and `xinyee`'s full commit history are ancestors of `iman`'s
branch — meaning Iman already merged both teammates' work into their own
branch and fixed bugs on top of it before this final integration. File
origin below is based on each file's first ("Create ...") commit and which
original branch it lived on before any merging happened.

| File | Origin | Member | Notes |
|---|---|---|---|
| `Main.java` | shared → iman → daus | Initial scaffold, then Iman, then Firdaus | Skeleton created before task division; rebuilt by Iman to wire Resident/Booking/Queue together; Admin Login + Station menu added by Firdaus. **[TAMPERED by Claude]**: now calls `waitlistManager.setStationManager(stationManager)`; passes `stationManager` and `bookingManager` into `QueueMenu`'s constructor; passes `waitlistManager` into `BookingMenu`'s constructor. (Note: this row refers to `BookingChargerApp.java`, which is the actual entry point file in this branch.) |
| **Resident & Vehicle Management (Member 1 — Iman)** | | | |
| `Resident.java` | iman | Iman | Supersedes an earlier, simpler draft from Xinyee's branch (no `User` inheritance) |
| `User.java` | iman | Iman | |
| `Vehicle.java` | iman | Iman | **[TAMPERED by Claude]**: added a `plateNumber` field so vehicles with the same model can be told apart. |
| `ResidentManager.java` | iman | Iman | **[TAMPERED by Claude]**: `login()` now requires an exact-case match on Resident ID instead of case-insensitive; `addVehicleToResident()`/`removeVehicleFromResident()` now use `plateNumber` instead of `model`; `vehicle.txt` save/load format updated to 4 fields accordingly; `registerResident()` now rejects a blank name or password via the new `InvalidRegistrationException`. |
| `PasswordUtil.java` | iman | Iman | SHA-256 hashing helper |
| `ProfileMenu.java` | iman | Iman | |
| `VehicleMenu.java` | iman | Iman | **[TAMPERED by Claude]**: add/remove now prompts for and matches on plate number instead of model. |
| `ResidentMenuUI.java` | iman | Iman | Not referenced by `Main.java` — looks like an earlier draft of the dashboard. Patched by Firdaus, then by Claude (vehicle add/remove updated to plate number), only to keep it compiling. **Consider deleting.** |
| `TestAuth.java` | iman | Iman | |
| `Exception/DuplicateAccountException.java` | iman | Iman | |
| `Exception/InvalidIcFormatException.java` | iman | Iman | |
| `Exception/InvalidLoginException.java` | iman | Iman | |
| `Exception/InvalidRegistrationException.java` | — | Claude | **[NEW by Claude]**: thrown by `registerResident()` when name or password is left blank. |
| **Charging Station Management (Member 2 — Firdaus)** | | | |
| `ChargingStation.java` | daus | Firdaus | Iman left a one-field placeholder stub; this is the full abstract base class |
| `FastChargingStation.java` | daus | Firdaus | |
| `NormalChargingStation.java` | daus | Firdaus | |
| `MaintenanceRecord.java` | daus | Firdaus | |
| `StationManager.java` | daus | Firdaus | **[TAMPERED by Claude]**: removed `viewStationAvailability()` (redundant with `viewAllStations()`); `resolveMaintenance()` now throws `InvalidStatusUpdateException` if the station isn't actually under maintenance; `addStation()` now rejects any type other than "Fast"/"Normal" (case-insensitive) via the new `InvalidStationTypeException`; `removeStation()` now throws `InvalidStatusUpdateException` if the station has a current (Active) booking. |
| `StationMenu.java` | daus | Firdaus | Resident-facing, view-only. **[TAMPERED by Claude]**: removed "View Station Availability" menu option; menu renumbered. |
| `AdminMenu.java` | daus | Firdaus | Admin-only controls. **[TAMPERED by Claude]**: removed "View Station Availability" menu option (renumbered); `handleResolveMaintenance()` now also catches `InvalidStatusUpdateException`; `handleAddStation()` now also catches `InvalidStationTypeException`; `handleRemoveStation()` now lists all stations first and also catches `InvalidStatusUpdateException` for stations with a current booking. |
| `Exception/DuplicateStationException.java` | daus | Firdaus | |
| `Exception/InvalidStatusUpdateException.java` | daus | Firdaus | |
| `Exception/InvalidStationTypeException.java` | — | Claude | **[NEW by Claude]**: thrown by `addStation()` when the entered type isn't "Fast" or "Normal" (case-insensitive). |
| `Exception/StationNotFoundException.java` | daus | Firdaus | |
| **Booking & Reservation Management (Member 3 — Hong Jia Bao)** | | | |
| `Booking.java` | jiabao | Jiabao | |
| `BookingConflictException.java` | jiabao | Jiabao | |
| `BookingManager.java` | jiabao | Jiabao | Extended by Firdaus to accept `StationManager` and added `findBooking()`. **[TAMPERED by Claude]**: `viewMyBookings()` now skips bookings with status `Cancelled`; added `hasActiveBooking()`, `getActiveBooking()`, and `getActiveBookingsForResident()` helpers used by the station-removal guard and the booking/queue menu changes below. |
| `BookingMenu.java` | jiabao | Jiabao | Extended by Firdaus to resolve real stations and share the `Scanner` instance instead of creating a new one. **[TAMPERED by Claude]**: `handleBookSlot()` now calls `viewAllStations()` instead of the removed `viewStationAvailability()`; also now shows the current booked resident for an occupied station, lets the resident type "Q" to view a station's queue before choosing, and offers to add the resident to the waitlist instead of just rejecting the booking; `handleCancelBooking()` now lists the resident's Active bookings before asking which to cancel. Constructor gained a `WaitlistManager` parameter. **Bug fix**: `handleJoinWaitlistWithDetails()` now actually wraps the entered date/time into a `TimeSlot` and passes it to `WaitlistManager.joinQueue(resident, stationId, requestedSlot)` — previously the entered time was printed back to the resident and then thrown away, so requesting the exact same timeframe as the station's current booking was silently accepted. |
| `TimeSlot.java` | jiabao | Jiabao | |
| **Smart Queue & Waitlist Management (Member 4 — Teoh Xin Yee)** | | | |
| `Exception/EmptyQueueException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `Exception/InvalidQueueSelectionException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `Exception/ResidentAlreadyInQueueException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `NormalQueueStrategy.java` | xinyee | Xinyee | |
| `QueueStrategy.java` | xinyee | Xinyee | |
| `SmartQueueManagement.java` | xinyee | Xinyee | |
| `WaitlistEntry.java` | xinyee | Xinyee | **[TAMPERED by Claude]**: added a `requestedSlot` (`TimeSlot`) field, plus a 4-arg constructor, so a resident's desired booking time is actually stored on the queue entry instead of being collected and discarded. |
| `WaitlistManager.java` | xinyee | Xinyee | Constructor call updated by Iman after the `Resident`/`User` refactor. **[TAMPERED by Claude]**: Queue History feature removed (`viewQueueHistory()`, `appendHistoryToFile()`, `loadHistoryFromFile()`, the `QueueHistory` field, and `HISTORY_FILE`); also gained `setStationManager()` so `validateStationId()` can reject station IDs that don't exist, and `getQueuedStationIds()` so the queue menu can list which stations a resident is currently waiting at. **Bug fix**: added `setBookingManager()` and a new `joinQueue(resident, stationId, requestedSlot)` overload that rejects the join with `BookingConflictException` if the requested slot overlaps the station's current Active booking (previously any timeframe, including the exact same one already booked, was silently accepted). `queue.txt` format extended to 7 fields to persist the requested slot. |
| `QueueMenu.java` | iman | Iman | Menu wrapper Iman wrote around Xinyee's `WaitlistManager`. **[TAMPERED by Claude]**: "View Queue History" option removed; "Join Waiting Queue" now lists stations first via `StationManager`; added a new "View a Station's Queue" option; "Leave Queue" and "View Queue Status" now list the stations the resident is queued at along with that station's current booking info, via the new `BookingManager` dependency. |

## Data files

| File | Origin |
|---|---|
| `resident.txt`, `vehicle.txt` | iman |
| `queue.txt` | xinyee (carried through iman) |
| `booking.txt` | generated at runtime (Jiabao's `BookingManager`) |
| `station.txt`, `maintenance.txt` | generated at runtime (Firdaus's `StationManager`) |

## Quick summary by member

- **Iman (Member 1):** Resident & Vehicle Management, plus the original `Main.java` integration wiring and `QueueMenu.java`
- **Firdaus (Member 2):** Charging Station Management, plus the Admin Login addition to `Main.java` and the BookingManager/BookingMenu station-lookup fixes
- **Hong Jia Bao (Member 3):** Booking & Reservation Management
- **Teoh Xin Yee (Member 4):** Smart Queue & Waitlist Management
