package com.example.bestsummary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SummaryViewHolder extends RecyclerView.ViewHolder {

    TextView summaryUsername, summaryText, summaryTime, likeCountText;
    ImageView likeBtn;

    DatabaseReference likedb;

    public SummaryViewHolder(@NonNull View itemView) {
        super(itemView);

        summaryUsername = itemView.findViewById(R.id.summaryUsername);
        summaryText = itemView.findViewById(R.id.summaryText);
        summaryTime = itemView.findViewById(R.id.summaryTime);

        likeCountText = itemView.findViewById(R.id.likeCountText);
        likeBtn = itemView.findViewById(R.id.likeBtn);

    }

    public void getLikeButtonStatus(final String postKey, final String userID){
        likedb = FirebaseDatabase.getInstance().getReference().child("Likes");
        likedb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(userID)){
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    likeCountText.setText(" "+String.valueOf(likeCount)+" Likes");
                    likeBtn.setImageResource(R.drawable.ic_baseline_liked);
                }

                else{
                    int likeCount = (int)snapshot.child(postKey).getChildrenCount();
                    likeCountText.setText(" "+String.valueOf(likeCount)+" Likes");
                    likeBtn.setImageResource(R.drawable.ic_baseline_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
