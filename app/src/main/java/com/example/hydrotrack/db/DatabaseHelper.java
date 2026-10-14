package com.example.hydrotrack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hydrotrack.model.DailySummary;
import com.example.hydrotrack.model.ReminderSettings;
import com.example.hydrotrack.model.UserProfile;
import com.example.hydrotrack.model.WaterIntake;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hydrotrack.db";
    private static final int DATABASE_VERSION = 1;

    // User Profile Table
    public static final String TABLE_USER = "user_profile";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_AGE = "age";
    public static final String COLUMN_USER_WEIGHT = "weight";
    public static final String COLUMN_USER_GOAL = "daily_goal";

    // Water Intake Table
    public static final String TABLE_INTAKE = "water_intake";
    public static final String COLUMN_INTAKE_ID = "id";
    public static final String COLUMN_INTAKE_AMOUNT = "amount";
    public static final String COLUMN_INTAKE_DATE = "date";
    public static final String COLUMN_INTAKE_TIMESTAMP = "timestamp";

    // Reminder Settings Table
    public static final String TABLE_REMINDER = "reminder_settings";
    public static final String COLUMN_REMINDER_ID = "id";
    public static final String COLUMN_REMINDER_ENABLED = "enabled";
    public static final String COLUMN_REMINDER_INTERVAL = "interval_minutes";
    public static final String COLUMN_REMINDER_START = "start_time";
    public static final String COLUMN_REMINDER_END = "end_time";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_USER_AGE + " INTEGER, " +
                COLUMN_USER_WEIGHT + " REAL, " +
                COLUMN_USER_GOAL + " INTEGER" + ")";

        String CREATE_INTAKE_TABLE = "CREATE TABLE " + TABLE_INTAKE + " (" +
                COLUMN_INTAKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INTAKE_AMOUNT + " INTEGER, " +
                COLUMN_INTAKE_DATE + " TEXT, " +
                COLUMN_INTAKE_TIMESTAMP + " INTEGER" + ")";

        String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMINDER + " (" +
                COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_REMINDER_ENABLED + " INTEGER, " +
                COLUMN_REMINDER_INTERVAL + " INTEGER, " +
                COLUMN_REMINDER_START + " TEXT, " +
                COLUMN_REMINDER_END + " TEXT" + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_INTAKE_TABLE);
        db.execSQL(CREATE_REMINDER_TABLE);

        // Insert default initial values
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_ID, 1);
        userValues.put(COLUMN_USER_NAME, "Water Tracker");
        userValues.put(COLUMN_USER_AGE, 22);
        userValues.put(COLUMN_USER_WEIGHT, 68.0);
        userValues.put(COLUMN_USER_GOAL, 2500);
        db.insert(TABLE_USER, null, userValues);

        ContentValues reminderValues = new ContentValues();
        reminderValues.put(COLUMN_REMINDER_ID, 1);
        reminderValues.put(COLUMN_REMINDER_ENABLED, 1);
        reminderValues.put(COLUMN_REMINDER_INTERVAL, 60);
        reminderValues.put(COLUMN_REMINDER_START, "08:00");
        reminderValues.put(COLUMN_REMINDER_END, "22:00");
        db.insert(TABLE_REMINDER, null, reminderValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTAKE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        onCreate(db);
    }

    public static String getTodayDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // --- USER PROFILE OPERATIONS ---

    public UserProfile getUserProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = 1", null);
        UserProfile profile = new UserProfile("Hydration Champion", 22, 68.0, 2500);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_AGE));
                double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_WEIGHT));
                int goal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_GOAL));
                profile = new UserProfile(name, age, weight, goal);
            }
            cursor.close();
        }
        return profile;
    }

    public void updateUserProfile(UserProfile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, profile.getName());
        values.put(COLUMN_USER_AGE, profile.getAge());
        values.put(COLUMN_USER_WEIGHT, profile.getWeight());
        values.put(COLUMN_USER_GOAL, profile.getDailyGoal());

        db.update(TABLE_USER, values, COLUMN_USER_ID + " = 1", null);
    }

    public void updateDailyGoal(int newGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_GOAL, newGoal);
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = 1", null);
    }

    // --- WATER INTAKE OPERATIONS ---

    public long addWaterIntake(int amount) {
        return addWaterIntakeWithDate(amount, getTodayDateString(), System.currentTimeMillis());
    }

    public long addWaterIntakeWithDate(int amount, String dateStr, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INTAKE_AMOUNT, amount);
        values.put(COLUMN_INTAKE_DATE, dateStr);
        values.put(COLUMN_INTAKE_TIMESTAMP, timestamp);
        return db.insert(TABLE_INTAKE, null, values);
    }

    public boolean deleteWaterIntake(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_INTAKE, COLUMN_INTAKE_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public int getTodayTotal() {
        return getTotalForDate(getTodayDateString());
    }

    public int getTotalForDate(String dateStr) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_INTAKE_AMOUNT + ") FROM " + TABLE_INTAKE +
                " WHERE " + COLUMN_INTAKE_DATE + " = ?", new String[]{dateStr});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
            cursor.close();
        }
        return total;
    }

    public List<WaterIntake> getTodayIntakes() {
        return getIntakesForDate(getTodayDateString());
    }

    public List<WaterIntake> getIntakesForDate(String dateStr) {
        List<WaterIntake> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INTAKE +
                " WHERE " + COLUMN_INTAKE_DATE + " = ? ORDER BY " + COLUMN_INTAKE_TIMESTAMP + " DESC", new String[]{dateStr});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INTAKE_ID));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INTAKE_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INTAKE_DATE));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INTAKE_TIMESTAMP));
                list.add(new WaterIntake(id, amount, date, timestamp));
            }
            cursor.close();
        }
        return list;
    }

    public List<DailySummary> getDailySummaries() {
        List<DailySummary> summaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int userGoal = getUserProfile().getDailyGoal();

        String query = "SELECT " + COLUMN_INTAKE_DATE + ", SUM(" + COLUMN_INTAKE_AMOUNT + ") AS total_amount " +
                "FROM " + TABLE_INTAKE + " " +
                "GROUP BY " + COLUMN_INTAKE_DATE + " " +
                "ORDER BY " + COLUMN_INTAKE_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                int totalAmount = cursor.getInt(1);
                summaries.add(new DailySummary(date, totalAmount, userGoal));
            }
            cursor.close();
        }
        return summaries;
    }

    // --- REMINDER SETTINGS OPERATIONS ---

    public ReminderSettings getReminderSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REMINDER + " WHERE " + COLUMN_REMINDER_ID + " = 1", null);
        ReminderSettings settings = new ReminderSettings(true, 60, "08:00", "22:00");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                boolean enabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_ENABLED)) == 1;
                int interval = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_INTERVAL));
                String start = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_START));
                String end = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_END));
                settings = new ReminderSettings(enabled, interval, start, end);
            }
            cursor.close();
        }
        return settings;
    }

    public void updateReminderSettings(ReminderSettings settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_ENABLED, settings.isEnabled() ? 1 : 0);
        values.put(COLUMN_REMINDER_INTERVAL, settings.getIntervalMinutes());
        values.put(COLUMN_REMINDER_START, settings.getStartTime());
        values.put(COLUMN_REMINDER_END, settings.getEndTime());
        db.update(TABLE_REMINDER, values, COLUMN_REMINDER_ID + " = 1", null);
    }

    // --- STATISTICS CALCULATIONS ---

    public int getWeeklyConsumption() {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -6);
        String sevenDaysAgoStr = sdf.format(cal.getTime());

        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_INTAKE_AMOUNT + ") FROM " + TABLE_INTAKE +
                " WHERE " + COLUMN_INTAKE_DATE + " >= ? AND " + COLUMN_INTAKE_DATE + " <= ?", new String[]{sevenDaysAgoStr, todayStr});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
            cursor.close();
        }
        return total;
    }

    public int getAverageDailyIntake() {
        List<DailySummary> summaries = getDailySummaries();
        if (summaries.isEmpty()) return 0;
        int totalSum = 0;
        for (DailySummary s : summaries) {
            totalSum += s.getTotalAmount();
        }
        return totalSum / summaries.size();
    }

    public int getGoalCompletionPercentage() {
        List<DailySummary> summaries = getDailySummaries();
        if (summaries.isEmpty()) return 0;
        int completedDays = 0;
        for (DailySummary s : summaries) {
            if (s.getTotalAmount() >= s.getGoal()) {
                completedDays++;
            }
        }
        return (int) Math.round(((double) completedDays / summaries.size()) * 100);
    }

    public int getCurrentStreak() {
        List<DailySummary> summaries = getDailySummaries();
        if (summaries.isEmpty()) return 0;

        int goal = getUserProfile().getDailyGoal();
        int streak = 0;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String todayStr = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayStr = sdf.format(cal.getTime());

        // Check if today or yesterday has met goal
        boolean foundStart = false;
        for (DailySummary s : summaries) {
            if (s.getDate().equals(todayStr) || s.getDate().equals(yesterdayStr)) {
                if (s.getTotalAmount() >= goal) {
                    foundStart = true;
                    break;
                }
            }
        }

        if (!foundStart) return 0;

        // Count consecutive days
        cal = Calendar.getInstance();
        while (true) {
            String checkDate = sdf.format(cal.getTime());
            boolean goalMet = false;
            for (DailySummary s : summaries) {
                if (s.getDate().equals(checkDate)) {
                    if (s.getTotalAmount() >= goal) {
                        goalMet = true;
                    }
                    break;
                }
            }
            if (goalMet) {
                streak++;
                cal.add(Calendar.DAY_OF_YEAR, -1);
            } else {
                // If checking today and goal not met yet today, check yesterday
                if (checkDate.equals(todayStr)) {
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                } else {
                    break;
                }
            }
        }
        return streak;
    }

    public int getLongestStreak() {
        List<DailySummary> summaries = getDailySummaries();
        if (summaries.isEmpty()) return 0;

        int goal = getUserProfile().getDailyGoal();
        int maxStreak = 0;
        int currentStreak = 0;

        for (DailySummary s : summaries) {
            if (s.getTotalAmount() >= goal) {
                currentStreak++;
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak;
                }
            } else {
                currentStreak = 0;
            }
        }
        return maxStreak;
    }

    public DailySummary getBestHydrationDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        int userGoal = getUserProfile().getDailyGoal();
        DailySummary best = null;

        String query = "SELECT " + COLUMN_INTAKE_DATE + ", SUM(" + COLUMN_INTAKE_AMOUNT + ") AS total_amount " +
                "FROM " + TABLE_INTAKE + " " +
                "GROUP BY " + COLUMN_INTAKE_DATE + " " +
                "ORDER BY total_amount DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String date = cursor.getString(0);
                int totalAmount = cursor.getInt(1);
                best = new DailySummary(date, totalAmount, userGoal);
            }
            cursor.close();
        }
        return best;
    }

    // --- RESET AND CLEAR DATA ---

    public void resetTodayData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INTAKE, COLUMN_INTAKE_DATE + " = ?", new String[]{getTodayDateString()});
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INTAKE, null, null);
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_REMINDER, null, null);

        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_ID, 1);
        userValues.put(COLUMN_USER_NAME, "Water Tracker");
        userValues.put(COLUMN_USER_AGE, 22);
        userValues.put(COLUMN_USER_WEIGHT, 68.0);
        userValues.put(COLUMN_USER_GOAL, 2500);
        db.insert(TABLE_USER, null, userValues);

        ContentValues reminderValues = new ContentValues();
        reminderValues.put(COLUMN_REMINDER_ID, 1);
        reminderValues.put(COLUMN_REMINDER_ENABLED, 1);
        reminderValues.put(COLUMN_REMINDER_INTERVAL, 60);
        reminderValues.put(COLUMN_REMINDER_START, "08:00");
        reminderValues.put(COLUMN_REMINDER_END, "22:00");
        db.insert(TABLE_REMINDER, null, reminderValues);
    }
}
