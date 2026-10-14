package com.example.hydrotrack.ui;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.hydrotrack.R;
import com.example.hydrotrack.db.DatabaseHelper;
import com.example.hydrotrack.model.ReminderSettings;
import com.example.hydrotrack.model.UserProfile;
import com.example.hydrotrack.notification.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private TextView tvUserName;
    private TextView tvUserInfo;
    private TextView tvDailyGoal;
    private ImageButton btnEditProfile;

    private MaterialSwitch switchReminders;
    private Spinner spinnerInterval;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private LinearLayout layoutStartTime;
    private LinearLayout layoutEndTime;

    private MaterialSwitch switchDarkMode;
    private MaterialButton btnResetToday;
    private MaterialButton btnClearAllData;

    private DatabaseHelper dbHelper;
    private ReminderSettings reminderSettings;
    private UserProfile userProfile;

    private boolean isSpinnerInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        tvUserName = view.findViewById(R.id.tv_settings_user_name);
        tvUserInfo = view.findViewById(R.id.tv_settings_user_info);
        tvDailyGoal = view.findViewById(R.id.tv_settings_daily_goal);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        switchReminders = view.findViewById(R.id.switch_reminders);
        spinnerInterval = view.findViewById(R.id.spinner_reminder_interval);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        layoutStartTime = view.findViewById(R.id.layout_start_time);
        layoutEndTime = view.findViewById(R.id.layout_end_time);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        btnResetToday = view.findViewById(R.id.btn_reset_today);
        btnClearAllData = view.findViewById(R.id.btn_clear_all_data);

        setupIntervalSpinner();
        loadSettingsData();

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        switchReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderSettings.setEnabled(isChecked);
            dbHelper.updateReminderSettings(reminderSettings);
            if (isChecked) {
                NotificationHelper.scheduleNextReminder(requireContext());
                Toast.makeText(requireContext(), "Reminders enabled", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.cancelReminders(requireContext());
                Toast.makeText(requireContext(), "Reminders disabled", Toast.LENGTH_SHORT).show();
            }
        });

        layoutStartTime.setOnClickListener(v -> showTimePicker(true));
        layoutEndTime.setOnClickListener(v -> showTimePicker(false));

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switchDarkMode.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnResetToday.setOnClickListener(v -> showResetTodayDialog());
        btnClearAllData.setOnClickListener(v -> showClearAllDialog());

        return view;
    }

    private void setupIntervalSpinner() {
        String[] intervals = {"30 minutes", "45 minutes", "60 minutes", "90 minutes", "120 minutes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, intervals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(adapter);

        spinnerInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true;
                    return;
                }
                int[] minutesArray = {30, 45, 60, 90, 120};
                int selectedMinutes = minutesArray[position];
                reminderSettings.setIntervalMinutes(selectedMinutes);
                dbHelper.updateReminderSettings(reminderSettings);
                if (reminderSettings.isEnabled()) {
                    NotificationHelper.scheduleNextReminder(requireContext());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSettingsData() {
        if (getContext() == null || dbHelper == null) return;

        userProfile = dbHelper.getUserProfile();
        reminderSettings = dbHelper.getReminderSettings();

        tvUserName.setText(String.format("Name: %s", userProfile.getName()));
        tvUserInfo.setText(String.format(Locale.getDefault(), "Age: %d yrs | Weight: %.1f kg", userProfile.getAge(), userProfile.getWeight()));
        tvDailyGoal.setText(String.format(Locale.getDefault(), "Daily Water Goal: %d ml", userProfile.getDailyGoal()));

        switchReminders.setChecked(reminderSettings.isEnabled());
        tvStartTime.setText(reminderSettings.getStartTime());
        tvEndTime.setText(reminderSettings.getEndTime());

        int[] minutesArray = {30, 45, 60, 90, 120};
        for (int i = 0; i < minutesArray.length; i++) {
            if (minutesArray[i] == reminderSettings.getIntervalMinutes()) {
                spinnerInterval.setSelection(i);
                break;
            }
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile & Daily Goal");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etName = new EditText(requireContext());
        etName.setHint("Name");
        etName.setText(userProfile.getName());
        layout.addView(etName);

        final EditText etAge = new EditText(requireContext());
        etAge.setHint("Age (yrs)");
        etAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        etAge.setText(String.valueOf(userProfile.getAge()));
        layout.addView(etAge);

        final EditText etWeight = new EditText(requireContext());
        etWeight.setHint("Weight (kg)");
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etWeight.setText(String.valueOf(userProfile.getWeight()));
        layout.addView(etWeight);

        final EditText etGoal = new EditText(requireContext());
        etGoal.setHint("Daily Water Goal (ml)");
        etGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
        etGoal.setText(String.valueOf(userProfile.getDailyGoal()));
        layout.addView(etGoal);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                String name = etName.getText().toString().trim();
                int age = Integer.parseInt(etAge.getText().toString().trim());
                double weight = Double.parseDouble(etWeight.getText().toString().trim());
                int goal = Integer.parseInt(etGoal.getText().toString().trim());

                if (name.isEmpty() || goal <= 0) {
                    Toast.makeText(requireContext(), "Please enter valid profile details", Toast.LENGTH_SHORT).show();
                    return;
                }

                userProfile.setName(name);
                userProfile.setAge(age);
                userProfile.setWeight(weight);
                userProfile.setDailyGoal(goal);

                dbHelper.updateUserProfile(userProfile);
                loadSettingsData();
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid inputs provided", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showTimePicker(boolean isStart) {
        String currentTime = isStart ? reminderSettings.getStartTime() : reminderSettings.getEndTime();
        String[] parts = currentTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    if (isStart) {
                        reminderSettings.setStartTime(formattedTime);
                    } else {
                        reminderSettings.setEndTime(formattedTime);
                    }
                    dbHelper.updateReminderSettings(reminderSettings);
                    loadSettingsData();
                }, hour, minute, true);

        timePickerDialog.setTitle(isStart ? "Select Reminder Start Time" : "Select Reminder End Time");
        timePickerDialog.show();
    }

    private void showResetTodayDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset Today's Data")
                .setMessage("Are you sure you want to delete all water logs recorded today?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    dbHelper.resetTodayData();
                    Toast.makeText(requireContext(), "Today's water logs cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All SQLite Data")
                .setMessage("DANGER: This will delete ALL saved history, profile settings, and logs permanently from SQLite.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    dbHelper.clearAllData();
                    loadSettingsData();
                    Toast.makeText(requireContext(), "All SQLite data cleared successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
