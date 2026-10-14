package com.example.hydrotrack.model;

public class ReminderSettings {
    private boolean enabled;
    private int intervalMinutes;
    private String startTime;
    private String endTime;

    public ReminderSettings(boolean enabled, int intervalMinutes, String startTime, String endTime) {
        this.enabled = enabled;
        this.intervalMinutes = intervalMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
