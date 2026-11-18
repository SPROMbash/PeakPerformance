# PeakPerformance

> *Donde cada día eres tu mejor versión.*

Android fitness app written in **Kotlin**: user accounts, custom workout routines and
voice commands, with all data stored locally in **SQLite**.

## Features

| Area | What it does |
|---|---|
| **Accounts** | Sign up and log in. Form validation (required fields, email format, matching passwords) and a password policy: 8+ characters with a digit, a letter and an uppercase letter. |
| **Secure password storage** | Passwords are hashed with **PBKDF2** (random 16-byte salt per user, 120 000 iterations) and verified in constant time. Accounts created before this change are migrated to the hashed format on their next login. |
| **Workout routines** | Create routines with a name and description, add exercises with sets × reps through a dialog, list them as cards and delete them. |
| **Voice commands** | Android speech recognition: *"iniciar rutinas"*, *"modo oscuro"*, *"notificaciones"*. |
| **Settings** | Dark mode, notifications, profile picture, app language and sync preferences. |
| **Navigation** | Navigation drawer with Home, Settings, Share, About Us and Logout. |

## Tech stack

- Kotlin · Android SDK 35 (min SDK 24)
- AndroidX AppCompat, Material Components, ConstraintLayout, RecyclerView
- SQLite through `SQLiteOpenHelper`, with parameterized queries throughout
- `javax.crypto` PBKDF2 for password hashing

## Project structure

```
app/src/main/java/com/example/peakperformance/
├── LoginActivity.kt / RegisterActivity.kt   # authentication
├── PasswordHasher.kt                        # PBKDF2 hashing + verification
├── AdminSQLiteOpenHelper.kt                 # DB schema: users, routines, routine exercises
├── RutinasActivity.kt / CrearRutinaActivity.kt / DialogSeriesRepeticiones.kt
├── MainActivity.kt                          # dashboard, drawer, voice commands
└── SettingsActivity.kt
```

## Design process

The app was planned before writing any code: first a list of screens and features,
then **wireframes in Figma** to define the hierarchy and navigation, then the visual
design (a palette that conveys health and energy, readable typography and clear
icons for each section).

## Roadmap

The original design also included these sections. The screens exist in the app
but are **not implemented yet**:

- [ ] **Nutrition**: calories, macros and water intake, plus meal planning
- [ ] **Progress & reports**: charts and comparisons between periods, with PDF export
- [ ] **Achievements**, **Classes** and **Health** (sleep and rest tracking)
- [ ] Load/deload week planning
- [ ] Password recovery
- [ ] Integration with wearables

## Running it

1. Open the project in Android Studio (recent version, with JDK 17+).
2. Let Gradle sync, then run the `app` configuration on an emulator or a device (API 24+).

Or from the command line:

```bash
./gradlew assembleDebug
```
