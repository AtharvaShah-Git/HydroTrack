package com.example.hydrotrack.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hydrotrack.db.DatabaseHelper;
import com.example.hydrotrack.model.ReminderSettings;

import java.util.Calendar;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        ReminderSettings settings = db.getReminderSettings();

        if (settings.isEnabled() && isWithinActiveHours(settings.getStartTime(), settings.getEndTime())) {
            NotificationHelper.showHydrationNotification(context);
        }

        // Reschedule for next interval
        NotificationHelper.scheduleNextReminder(context);
    }

    private boolean isWithinActiveHours(String startTime, String endTime) {
        try {
            Calendar now = Calendar.getInstance();
            int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

            String[] startParts = startTime.split(":");
            int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);

            String[] endParts = endTime.split(":");
            int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);

            return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
        } catch (Exception e) {
            return true;
        }
    }
}
