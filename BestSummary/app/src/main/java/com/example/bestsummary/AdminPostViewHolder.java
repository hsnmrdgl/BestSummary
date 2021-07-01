package com.example.bestsummary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminPostViewHolder extends RecyclerView.ViewHolder {

    TextView adminPostBookName, adminPostUsername, adminPostText, adminPostTime;
    ImageView adminPostDeleteBtn;


    public AdminPostViewHolder(@NonNull View itemView) {
        super(itemView);

        adminPostBookName = itemView.findViewById(R.id.adminPostBookName);
        adminPostUsername = itemView.findViewById(R.id.adminPostUsername);
        adminPostText = itemView.findViewById(R.id.adminPostText);
        adminPostTime = itemView.findViewById(R.id.adminPostTime);

        adminPostDeleteBtn = itemView.findViewById(R.id.adminPostDeleteBtn);

    }
}
