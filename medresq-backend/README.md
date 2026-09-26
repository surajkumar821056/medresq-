# MedResQ Backend

Spring Boot backend for the MedResQ Health hospital bed availability & emergency
referral dashboard. Replaces the frontend's `localStorage` mock data with a real
database and REST API.

## Stack
- Java 17, Spring Boot 3.3, Maven
- Spring Web, Spring Data JPA, Spring Security
- MySQL (default) / H2 (optional, zero-setup profile)
- JWT auth (`jjwt`)
- Lombok

## Running it

### Option A — MySQL (default)
1. Make sure MySQL is running locally. The connection string uses
   `createDatabaseIfNotExist=true`, so you don't need to manually create the
   `medresq` schema — it's created automatically on first connect as long as
   the user has permission to do so.
2. Update credentials in `src/main/resources/application.properties` if yours
   differ from `root` / `root` on `localhost:3306`.
3. In IntelliJ: open the project (`pom.xml` as a Maven project), let it index,
   then run `BackendApplication`.
4. Tables are auto-created (`ddl-auto=update`) and 12 Delhi NCR hospitals +
   matching admin accounts are seeded automatically on first run.

### Option B — H2 (no setup, resets on restart)
In IntelliJ's Run Configuration for `BackendApplication`, set
**Active profiles: `h2`** (or run `mvn spring-boot:run -Dspring-boot.run.profiles=h2`).
H2 console available at `http://localhost:8080/h2-console` (JDBC URL:
`jdbc:h2:mem:medresq`, user `sa`, no password).

The API runs on **`http://localhost:8080`**.

## Connecting the React frontend
Point your `fetch`/`axios` calls at `http://localhost:8080/api/...`. CORS is
pre-configured for `http://localhost:5173` (Vite default) — add other origins
via `app.cors.allowed-origins` in `application.properties` if needed.

## Demo credentials (seeded)
- **Admin**: any hospital email from the seed data (e.g. `emergency@aiims.edu`)
  + password `admin` — same as the current frontend demo.
- **Patient**: any 10-digit phone number via `/api/auth/patient/send-otp` then
  `/api/auth/patient/verify-otp` with **any** OTP code (stub — see note below).

## API Overview

### Auth — `/api/auth`
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/patient/send-otp` | public | `{ "phone": "9876543210" }` |
| POST | `/patient/verify-otp` | public | `{ "phone": "...", "otp": "1234" }` → JWT |
| POST | `/admin/login` | public | `{ "email": "...", "password": "admin" }` → JWT |

### Hospitals — `/api/hospitals`
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/` | public | List all hospitals with bed inventory |
| GET | `/{id}` | public | Single hospital |
| POST | `/` | ADMIN | Create hospital |
| PUT | `/{id}` | ADMIN | Update hospital profile |
| PUT | `/{id}/beds` | ADMIN | Bulk update bed counts |
| DELETE | `/{id}` | ADMIN | Remove hospital |

### Referrals — `/api/referrals`
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/` | public | Patient books a bed (creates a pending referral) |
| GET | `/?hospitalId=` | authenticated | List referrals, optionally filtered |
| PATCH | `/{id}/approve` | ADMIN | Approve — deducts 1 bed from inventory |
| PATCH | `/{id}/decline` | ADMIN | Reject the request |
| PATCH | `/{id}/admit` | ADMIN | Mark patient as admitted |
| PATCH | `/{id}/discharge` | ADMIN | Discharge — releases the bed back |

Send the JWT from login as `Authorization: Bearer <token>` on subsequent
requests.

## Notes & what's intentionally left out of this first pass
- **OTP is a stub.** `sendOtp`/`verifyOtp` simulate the frontend's current demo
  behavior (any code verifies). Swap in a real provider (Twilio, MSG91, etc.)
  before going to production.
- **Ambulances, live notifications, and analytics** aren't modeled yet — the
  frontend's `AnalyticsDashboard`, `EmergencyDashboard` ambulance tracking, and
  `NotificationToast` are still running on local/mock state. These can be
  added as follow-up modules once the core (hospitals/beds/referrals/auth) is
  wired up and working end-to-end.
- **Change `app.jwt.secret`** in `application.properties` before any real
  deployment — move it to an environment variable.
