package com.example.bestsummary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminSummaryViewHolder extends RecyclerView.ViewHolder {

    TextView adminSummaryUsername, adminSummaryText, adminSummaryTime, adminLikeCountText;
    ImageView adminLikeBtn, adminSummaryDeletBtn;

    public AdminSummaryViewHolder(@NonNull View itemView) {
        super(itemView);

        adminSummaryUsername = itemView.findViewById(R.id.adminSummaryUsername);
        adminSummaryText = itemView.findViewById(R.id.adminSummaryText);
        adminSummaryTime = itemView.findViewById(R.id.adminSummaryTime);

        adminLikeCountText = itemView.findViewById(R.id.adminLikeCountText);
        adminLikeBtn = itemView.findViewById(R.id.adminLikeBtn);

        adminSummaryDeletBtn = itemView.findViewById(R.id.adminSummaryDeleteBtn);
    }
}
