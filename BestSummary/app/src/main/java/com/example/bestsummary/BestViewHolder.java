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

public class BestViewHolder extends RecyclerView.ViewHolder {

    TextView bestSummaryUsername, bestSummaryText, bestSummaryTime, bestLikeCountText;
    ImageView bestLikeBtn;

    DatabaseReference likedb;

    public BestViewHolder(@NonNull View itemView) {
        super(itemView);

        bestSummaryUsername = itemView.findViewById(R.id.bestSummaryUsername);
        bestSummaryText = itemView.findViewById(R.id.bestSummaryText);
        bestSummaryTime = itemView.findViewById(R.id.bestSummaryTime);

        bestLikeCountText = itemView.findViewById(R.id.bestLikeCountText);
        bestLikeBtn = itemView.findViewById(R.id.bestLikeBtn);

    }

    public void getLikeButtonStatus(final String postKey, final String userID){
        likedb = FirebaseDatabase.getInstance().getReference().child("Likes");
        likedb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(userID)){
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    bestLikeCountText.setText(" "+String.valueOf(likeCount)+" Likes");
                    bestLikeBtn.setImageResource(R.drawable.ic_baseline_liked);
                }

                else{
                    int likeCount = (int)snapshot.child(postKey).getChildrenCount();
                    bestLikeCountText.setText(" "+String.valueOf(likeCount)+" Likes");
                    bestLikeBtn.setImageResource(R.drawable.ic_baseline_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
