package com.example.bestsummary;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    TextView postBookName, postUsername, postText, postTime;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        postBookName = itemView.findViewById(R.id.postBookName);
        postUsername = itemView.findViewById(R.id.postUsername);
        postText = itemView.findViewById(R.id.postText);
        postTime = itemView.findViewById(R.id.postTime);
    }
}
