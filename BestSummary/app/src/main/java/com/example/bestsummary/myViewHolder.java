package com.example.bestsummary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class myViewHolder extends RecyclerView.ViewHolder {

    ImageView bookViewImg;
    TextView bookViewText;
    View v;

     public myViewHolder(@NonNull View itemView) {
        super(itemView);

        bookViewImg = itemView.findViewById(R.id.bookViewImg);
        bookViewText = itemView.findViewById(R.id.bookViewText);
        v = itemView;
    }
}
