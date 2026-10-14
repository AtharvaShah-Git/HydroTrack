package com.example.hydrotrack.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterIntake {
    private long id;
    private int amount;
    private String date;
    private long timestamp;

    public WaterIntake(long id, int amount, String date, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            return "";
        }
    }
}
