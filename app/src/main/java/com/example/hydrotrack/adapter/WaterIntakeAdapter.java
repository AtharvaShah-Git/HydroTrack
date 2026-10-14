package com.example.hydrotrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hydrotrack.R;
import com.example.hydrotrack.model.WaterIntake;

import java.util.List;

public class WaterIntakeAdapter extends RecyclerView.Adapter<WaterIntakeAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(WaterIntake intake);
    }

    private final List<WaterIntake> intakeList;
    private final OnDeleteClickListener deleteListener;

    public WaterIntakeAdapter(List<WaterIntake> intakeList, OnDeleteClickListener deleteListener) {
        this.intakeList = intakeList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_water_intake, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterIntake intake = intakeList.get(position);
        holder.tvAmount.setText(intake.getAmount() + " ml");
        holder.tvTime.setText(intake.getFormattedTime());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(intake);
            }
        });
    }

    @Override
    public int getItemCount() {
        return intakeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount;
        TextView tvTime;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_intake_amount);
            tvTime = itemView.findViewById(R.id.tv_intake_time);
            btnDelete = itemView.findViewById(R.id.btn_delete_intake);
        }
    }
}
