package com.example.hydrotrack.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hydrotrack.R;
import com.example.hydrotrack.adapter.DailyHistoryAdapter;
import com.example.hydrotrack.db.DatabaseHelper;
import com.example.hydrotrack.model.DailySummary;
import com.example.hydrotrack.model.WaterIntake;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private TextView tvTotalDaysTracked;
    private TextView tvTotalAllTimeWater;
    private TextView tvEmptyState;
    private RecyclerView rvDailyHistory;

    private DailyHistoryAdapter historyAdapter;
    private final List<DailySummary> summaryList = new ArrayList<>();

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        tvTotalDaysTracked = view.findViewById(R.id.tv_total_days_tracked);
        tvTotalAllTimeWater = view.findViewById(R.id.tv_total_all_time_water);
        tvEmptyState = view.findViewById(R.id.tv_history_empty_state);

        rvDailyHistory = view.findViewById(R.id.rv_daily_history);
        rvDailyHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        historyAdapter = new DailyHistoryAdapter(summaryList, this::showDateDetailsDialog);
        rvDailyHistory.setAdapter(historyAdapter);

        loadHistoryData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistoryData();
    }

    private void loadHistoryData() {
        if (getContext() == null || dbHelper == null) return;

        summaryList.clear();
        summaryList.addAll(dbHelper.getDailySummaries());
        historyAdapter.notifyDataSetChanged();

        int daysCount = summaryList.size();
        long totalMl = 0;
        for (DailySummary summary : summaryList) {
            totalMl += summary.getTotalAmount();
        }

        tvTotalDaysTracked.setText(String.valueOf(daysCount));
        if (totalMl >= 1000) {
            double liters = (double) totalMl / 1000.0;
            tvTotalAllTimeWater.setText(String.format(Locale.getDefault(), "%.1f L", liters));
        } else {
            tvTotalAllTimeWater.setText(String.format(Locale.getDefault(), "%d ml", totalMl));
        }

        if (summaryList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvDailyHistory.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvDailyHistory.setVisibility(View.VISIBLE);
        }
    }

    private void showDateDetailsDialog(DailySummary summary) {
        List<WaterIntake> logs = dbHelper.getIntakesForDate(summary.getDate());
        StringBuilder builder = new StringBuilder();

        builder.append("Goal: ").append(summary.getGoal()).append(" ml\n");
        builder.append("Total Consumed: ").append(summary.getTotalAmount()).append(" ml (")
                .append(summary.getPercentage()).append("%)\n\n");
        builder.append("Individual Logs:\n");

        if (logs.isEmpty()) {
            builder.append("No individual logs recorded.");
        } else {
            for (WaterIntake log : logs) {
                builder.append("• ").append(log.getAmount()).append(" ml at ").append(log.getFormattedTime()).append("\n");
            }
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setTitle("Logs for " + summary.getFormattedDate());
        dialogBuilder.setMessage(builder.toString());
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        dialogBuilder.show();
    }
}
