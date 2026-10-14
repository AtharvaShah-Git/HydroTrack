package com.example.hydrotrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hydrotrack.R;
import com.example.hydrotrack.model.DailySummary;

import java.util.List;
import java.util.Locale;

public class DailyHistoryAdapter extends RecyclerView.Adapter<DailyHistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DailySummary summary);
    }

    private final List<DailySummary> summaryList;
    private final OnItemClickListener clickListener;

    public DailyHistoryAdapter(List<DailySummary> summaryList, OnItemClickListener clickListener) {
        this.summaryList = summaryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailySummary summary = summaryList.get(position);
        holder.tvDate.setText(summary.getFormattedDate());
        holder.tvIntakeGoal.setText(String.format(Locale.getDefault(), "%d / %d ml", summary.getTotalAmount(), summary.getGoal()));
        holder.tvPercentage.setText(String.format(Locale.getDefault(), "%d%%", summary.getPercentage()));
        holder.pbProgress.setMax(summary.getGoal());
        holder.pbProgress.setProgress(Math.min(summary.getTotalAmount(), summary.getGoal()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(summary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvIntakeGoal;
        TextView tvPercentage;
        ProgressBar pbProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_history_date);
            tvIntakeGoal = itemView.findViewById(R.id.tv_history_intake_goal);
            tvPercentage = itemView.findViewById(R.id.tv_history_percentage);
            pbProgress = itemView.findViewById(R.id.pb_history_progress);
        }
    }
}
