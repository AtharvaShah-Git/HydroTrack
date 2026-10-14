package com.example.hydrotrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hydrotrack.R;
import com.example.hydrotrack.db.DatabaseHelper;
import com.example.hydrotrack.model.DailySummary;

import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private TextView tvWeeklyTotal;
    private TextView tvAverageDaily;
    private TextView tvCompletionRate;
    private TextView tvCurrentStreak;
    private TextView tvLongestStreak;
    private TextView tvBestDay;

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        tvWeeklyTotal = view.findViewById(R.id.tv_stat_weekly_total);
        tvAverageDaily = view.findViewById(R.id.tv_stat_average_daily);
        tvCompletionRate = view.findViewById(R.id.tv_stat_completion_rate);
        tvCurrentStreak = view.findViewById(R.id.tv_stat_current_streak);
        tvLongestStreak = view.findViewById(R.id.tv_stat_longest_streak);
        tvBestDay = view.findViewById(R.id.tv_stat_best_day);

        loadStatisticsData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatisticsData();
    }

    private void loadStatisticsData() {
        if (getContext() == null || dbHelper == null) return;

        int weeklyTotal = dbHelper.getWeeklyConsumption();
        int avgDaily = dbHelper.getAverageDailyIntake();
        int completionRate = dbHelper.getGoalCompletionPercentage();
        int currentStreak = dbHelper.getCurrentStreak();
        int longestStreak = dbHelper.getLongestStreak();
        DailySummary bestDay = dbHelper.getBestHydrationDay();

        tvWeeklyTotal.setText(String.format(Locale.getDefault(), "%d ml", weeklyTotal));
        tvAverageDaily.setText(String.format(Locale.getDefault(), "%d ml", avgDaily));
        tvCompletionRate.setText(String.format(Locale.getDefault(), "%d%%", completionRate));
        tvCurrentStreak.setText(String.format(Locale.getDefault(), "%d Days", currentStreak));
        tvLongestStreak.setText(String.format(Locale.getDefault(), "%d Days", longestStreak));

        if (bestDay != null && bestDay.getTotalAmount() > 0) {
            tvBestDay.setText(String.format(Locale.getDefault(), "%d ml on %s", bestDay.getTotalAmount(), bestDay.getFormattedDate()));
        } else {
            tvBestDay.setText("No data recorded yet");
        }
    }
}
