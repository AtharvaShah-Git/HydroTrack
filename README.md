# 💧 HydroTrack - Professional Water Reminder & Hydration Tracker

**HydroTrack** is an Android application designed to help users track their daily water intake, set hydration goals, receive intelligent reminders, and analyze drinking habits over time.

Built using **Java**, **Android Native XML Layouts**, **Material Design 3**, and **Native Android SQLite** (`SQLiteOpenHelper`) for offline data persistence.

---

## ✨ Features

- 📊 **Home Dashboard**:
  - Real-time hydration progress tracking with visual percentage indicator.
  - Quick-add buttons (`+250 ml`, `+350 ml`, `+500 ml`) and custom intake dialog.
  - Dynamic display of daily goal, total consumed volume, and remaining water.
  - Today's detailed intake logs with single-tap deletion.

- 📜 **Historical Data**:
  - Daily summaries grouped by date stored permanently in SQLite.
  - Full history list showing daily totals, goals, and goal completion badges.
  - Detailed breakdown modal for viewing time-stamped logs for any specific date.

- 📈 **Hydration Analytics & Statistics**:
  - 7-day total water consumption.
  - Daily average water intake.
  - Overall goal completion percentage.
  - Current streak & longest streak tracking (consecutive goal achievement).
  - All-time best hydration record day.

- ⚙️ **Settings & Customization**:
  - Edit user profile (Name, Age, Weight, Daily Goal).
  - Hydration Reminders: Toggle notifications, set custom intervals (30–120 mins), and active hours window.
  - Dark Mode / Light Mode theme toggle.
  - Data management: Reset today's water entries or perform a full SQLite database clear.

- 🔔 **Hydration Reminders & System Support**:
  - Automated local notifications via Android `AlarmManager`.
  - `BOOT_COMPLETED` receiver to automatically restore reminders upon device restart.
  - Android 13+ runtime notification permissions support.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Java 11
- **UI Framework**: Native Android XML, Material Design 3 Components (`MaterialCardView`, `MaterialButton`, `MaterialSwitch`, `BottomNavigationView`)
- **Database**: Native Android `SQLiteOpenHelper` (`hydrotrack.db`)
  - No Room, Firebase, or external dependencies required.
- **Background Tasks**: `AlarmManager`, `BroadcastReceiver`, `NotificationManagerCompat`
- **Target SDK**: Android 36 (Min SDK: 24 / Android 7.0+)

### Database Schema Overview (`hydrotrack.db`)

1. `user_profile`: `(id PRIMARY KEY, name, age, weight, daily_goal)`
2. `water_intake`: `(id PRIMARY KEY AUTOINCREMENT, amount, date, timestamp)`
3. `reminder_settings`: `(id PRIMARY KEY, enabled, interval_minutes, start_time, end_time)`

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Ladybug / Jellyfish or newer recommended)
- Android SDK 36 installed
- Java Development Kit (JDK 11 or higher)
- Android Emulator or Physical Device running Android 7.0 (API level 24) or higher

### Installation & Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/HydroTrack.git
   cd HydroTrack
   ```

2. **Open in Android Studio**:
   - Select **File > Open...**
   - Choose the cloned `HydroTrack` directory.
   - Allow Gradle to sync dependencies automatically.

3. **Build & Run**:
   - Connect an Android device or launch an emulator.
   - Click the green **Run** button (`Shift + F10`) or run from terminal:
     ```bash
     ./gradlew installDebug
     ```

---

## 📱 Screenshots & Demo Guide

| Home Dashboard | History Log | Hydration Stats | Settings |
| :---: | :---: | :---: | :---: |
| Dynamic progress & quick log buttons | Grouped daily summary cards | Streak & consumption analytics | Profile, notifications & theme toggle |

---

## 🎓 College Micro-Project Notice

This project was built as a college micro-project demonstrating clean Android application architecture, native SQLite data persistence, system alarms & broadcast receivers, and Material Design UI principles in Java.

---

## 📄 License

This project is licensed under the MIT License - feel free to use and adapt it for learning purposes!
