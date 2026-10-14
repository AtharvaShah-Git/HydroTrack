package com.example.hydrotrack.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailySummary {
    private final String date;
    private final int totalAmount;
    private final int goal;

    public DailySummary(String date, int totalAmount, int goal) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.goal = goal;
    }

    public String getDate() {
        return date;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getGoal() {
        return goal;
    }

    public int getPercentage() {
        if (goal <= 0) return 0;
        return (int) Math.min(100, Math.round(((double) totalAmount / goal) * 100));
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate == null) return date;

            String todayStr = inputFormat.format(new Date());
            if (date.equals(todayStr)) {
                return "Today";
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            return date;
        }
    }
}
