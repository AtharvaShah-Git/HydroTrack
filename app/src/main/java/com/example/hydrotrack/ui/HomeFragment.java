package com.example.hydrotrack.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hydrotrack.R;
import com.example.hydrotrack.adapter.WaterIntakeAdapter;
import com.example.hydrotrack.db.DatabaseHelper;
import com.example.hydrotrack.model.UserProfile;
import com.example.hydrotrack.model.WaterIntake;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvCurrentIntake;
    private TextView tvGoalTarget;
    private TextView tvProgressPercentage;
    private TextView tvRemainingWater;
    private TextView tvEntryCount;
    private TextView tvEmptyState;
    private ProgressBar pbCircularProgress;

    private RecyclerView rvTodayIntakes;
    private WaterIntakeAdapter intakeAdapter;
    private final List<WaterIntake> todayIntakes = new ArrayList<>();

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvCurrentIntake = view.findViewById(R.id.tv_current_intake);
        tvGoalTarget = view.findViewById(R.id.tv_goal_target);
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage);
        tvRemainingWater = view.findViewById(R.id.tv_remaining_water);
        tvEntryCount = view.findViewById(R.id.tv_entry_count);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        pbCircularProgress = view.findViewById(R.id.pb_circular_progress);

        rvTodayIntakes = view.findViewById(R.id.rv_today_intakes);
        rvTodayIntakes.setLayoutManager(new LinearLayoutManager(requireContext()));

        intakeAdapter = new WaterIntakeAdapter(todayIntakes, intake -> {
            boolean deleted = dbHelper.deleteWaterIntake(intake.getId());
            if (deleted) {
                Toast.makeText(requireContext(), "Water entry removed", Toast.LENGTH_SHORT).show();
                loadDashboardData();
            }
        });
        rvTodayIntakes.setAdapter(intakeAdapter);

        MaterialButton btnAdd250 = view.findViewById(R.id.btn_add_250);
        MaterialButton btnAdd350 = view.findViewById(R.id.btn_add_350);
        MaterialButton btnAdd500 = view.findViewById(R.id.btn_add_500);
        MaterialButton btnAddCustom = view.findViewById(R.id.btn_add_custom);

        btnAdd250.setOnClickListener(v -> addWaterAmount(250));
        btnAdd350.setOnClickListener(v -> addWaterAmount(350));
        btnAdd500.setOnClickListener(v -> addWaterAmount(500));
        btnAddCustom.setOnClickListener(v -> showCustomAmountDialog());

        loadDashboardData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (getContext() == null || dbHelper == null) return;

        UserProfile profile = dbHelper.getUserProfile();
        tvGreeting.setText(String.format("Hello, %s! 💧", profile.getName()));

        int currentTotal = dbHelper.getTodayTotal();
        int goal = profile.getDailyGoal();
        int remaining = Math.max(0, goal - currentTotal);
        int percentage = (goal > 0) ? (int) Math.min(100, Math.round(((double) currentTotal / goal) * 100)) : 0;

        tvCurrentIntake.setText(String.format(Locale.getDefault(), "%d ml", currentTotal));
        tvGoalTarget.setText(String.format(Locale.getDefault(), "Goal: %d ml", goal));
        tvProgressPercentage.setText(String.format(Locale.getDefault(), "%d%%", percentage));
        tvRemainingWater.setText(String.format(Locale.getDefault(), "Remaining: %d ml", remaining));

        pbCircularProgress.setMax(100);
        pbCircularProgress.setProgress(percentage);

        todayIntakes.clear();
        todayIntakes.addAll(dbHelper.getTodayIntakes());
        intakeAdapter.notifyDataSetChanged();

        tvEntryCount.setText(String.format(Locale.getDefault(), "%d logs", todayIntakes.size()));

        if (todayIntakes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvTodayIntakes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvTodayIntakes.setVisibility(View.VISIBLE);
        }
    }

    private void addWaterAmount(int amountMl) {
        if (amountMl <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addWaterIntake(amountMl);
        Toast.makeText(requireContext(), "+" + amountMl + " ml logged!", Toast.LENGTH_SHORT).show();
        loadDashboardData();
    }

    private void showCustomAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Custom Water Intake");
        builder.setMessage("Enter water amount in ml:");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g. 300");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    int amount = Integer.parseInt(inputText);
                    if (amount > 0 && amount <= 5000) {
                        addWaterAmount(amount);
                    } else {
                        Toast.makeText(requireContext(), "Please enter a realistic amount (1-5000 ml)", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
