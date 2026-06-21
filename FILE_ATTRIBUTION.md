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
| `Main.java` | shared → iman → daus | Initial scaffold, then Iman, then Firdaus | Skeleton created before task division; rebuilt by Iman to wire Resident/Booking/Queue together; Admin Login + Station menu added by Firdaus |
| **Resident & Vehicle Management (Member 1 — Iman)** | | | |
| `Resident.java` | iman | Iman | Supersedes an earlier, simpler draft from Xinyee's branch (no `User` inheritance) |
| `User.java` | iman | Iman | |
| `Vehicle.java` | iman | Iman | |
| `ResidentManager.java` | iman | Iman | |
| `PasswordUtil.java` | iman | Iman | SHA-256 hashing helper |
| `ProfileMenu.java` | iman | Iman | |
| `VehicleMenu.java` | iman | Iman | |
| `ResidentMenuUI.java` | iman | Iman | Not referenced by `Main.java` — looks like an earlier draft of the dashboard. Patched by Firdaus only to keep it compiling. **Consider deleting.** |
| `TestAuth.java` | iman | Iman | |
| `Exception/DuplicateAccountException.java` | iman | Iman | |
| `Exception/InvalidIcFormatException.java` | iman | Iman | |
| `Exception/InvalidLoginException.java` | iman | Iman | |
| **Charging Station Management (Member 2 — Firdaus)** | | | |
| `ChargingStation.java` | daus | Firdaus | Iman left a one-field placeholder stub; this is the full abstract base class |
| `FastChargingStation.java` | daus | Firdaus | |
| `NormalChargingStation.java` | daus | Firdaus | |
| `MaintenanceRecord.java` | daus | Firdaus | |
| `StationManager.java` | daus | Firdaus | |
| `StationMenu.java` | daus | Firdaus | Resident-facing, view-only |
| `AdminMenu.java` | daus | Firdaus | Admin-only controls |
| `Exception/DuplicateStationException.java` | daus | Firdaus | |
| `Exception/InvalidStatusUpdateException.java` | daus | Firdaus | |
| `Exception/StationNotFoundException.java` | daus | Firdaus | |
| **Booking & Reservation Management (Member 3 — Hong Jia Bao)** | | | |
| `Booking.java` | jiabao | Jiabao | |
| `BookingConflictException.java` | jiabao | Jiabao | |
| `BookingManager.java` | jiabao | Jiabao | Extended by Firdaus to accept `StationManager` and added `findBooking()` |
| `BookingMenu.java` | jiabao | Jiabao | Extended by Firdaus to resolve real stations and share the `Scanner` instance instead of creating a new one |
| `TimeSlot.java` | jiabao | Jiabao | |
| **Smart Queue & Waitlist Management (Member 4 — Teoh Xin Yee)** | | | |
| `Exception/EmptyQueueException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `Exception/InvalidQueueSelectionException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `Exception/ResidentAlreadyInQueueException.java` | xinyee | Xinyee | Unchanged since Xinyee's branch |
| `NormalQueueStrategy.java` | xinyee | Xinyee | |
| `QueueHistory.java` | xinyee | Xinyee | |
| `QueueRecord.java` | xinyee | Xinyee | |
| `QueueStrategy.java` | xinyee | Xinyee | |
| `SmartQueueManagement.java` | xinyee | Xinyee | |
| `WaitlistEntry.java` | xinyee | Xinyee | |
| `WaitlistManager.java` | xinyee | Xinyee | Constructor call updated by Iman after the `Resident`/`User` refactor |
| `QueueMenu.java` | iman | Iman | Menu wrapper Iman wrote around Xinyee's `WaitlistManager` |

## Data files

| File | Origin |
|---|---|
| `resident.txt`, `vehicle.txt` | iman |
| `queue.txt`, `queueHistory.txt` | xinyee (carried through iman) |
| `booking.txt` | generated at runtime (Jiabao's `BookingManager`) |
| `station.txt`, `maintenance.txt` | generated at runtime (Firdaus's `StationManager`) |

## Quick summary by member

- **Iman (Member 1):** Resident & Vehicle Management, plus the original `Main.java` integration wiring and `QueueMenu.java`
- **Firdaus (Member 2):** Charging Station Management, plus the Admin Login addition to `Main.java` and the BookingManager/BookingMenu station-lookup fixes
- **Hong Jia Bao (Member 3):** Booking & Reservation Management
- **Teoh Xin Yee (Member 4):** Smart Queue & Waitlist Management
