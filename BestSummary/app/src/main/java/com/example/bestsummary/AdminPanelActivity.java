package com.example.bestsummary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {


    DatabaseReference summarydb, postdb;


    private FirebaseRecyclerOptions<Summary> adminSummaryOption;
    private FirebaseRecyclerAdapter<Summary, AdminSummaryViewHolder> adminSummaryAdapter;
    private RecyclerView adminSummaryRecyclerView;

    private FirebaseRecyclerOptions<Posts> adminPostOption;
    private FirebaseRecyclerAdapter<Posts, AdminPostViewHolder> adminPostAdapter;
    private RecyclerView adminPostRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        summarydb = FirebaseDatabase.getInstance().getReference().child("Summary");
        postdb = FirebaseDatabase.getInstance().getReference().child("Posts");

        adminSummaryRecyclerView = findViewById(R.id.adminSummaryRecyclerView);
        adminSummaryRecyclerView.setHasFixedSize(true);
        adminSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adminPostRecyclerView = findViewById(R.id.adminPostRecyclerView);
        adminPostRecyclerView.setHasFixedSize(true);
        adminPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Query adminSummaryQuery = summarydb.orderByChild("SummaryTime");
        adminSummaryOption = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(adminSummaryQuery, Summary.class).build();
        adminSummaryAdapter = new FirebaseRecyclerAdapter<Summary, AdminSummaryViewHolder>(adminSummaryOption) {
            @Override
            protected void onBindViewHolder(@NonNull AdminSummaryViewHolder holder, int position, @NonNull Summary model) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                String postKey = getRef(position).getKey();

                holder.adminSummaryUsername.setText(model.getSummaryUsername());
                holder.adminSummaryText.setText(model.getSummaryText());
                holder.adminSummaryTime.setText(model.getSummaryTime());

                holder.adminSummaryDeletBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        summarydb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(postKey).exists()){
                                    summarydb.child(postKey).removeValue();
                                }
                                else{
                                    Toast.makeText(AdminPanelActivity.this, "An Error occured while deleting!", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public AdminSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_summary_view, parent, false);
                return new AdminSummaryViewHolder(v);
            }
        };

        adminSummaryAdapter.startListening();
        adminSummaryRecyclerView.setAdapter(adminSummaryAdapter);



        Query adminPostQuery = postdb.orderByChild("counter");
        adminPostOption = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(adminPostQuery, Posts.class).build();
        adminPostAdapter = new FirebaseRecyclerAdapter<Posts, AdminPostViewHolder>(adminPostOption) {
            @Override
            protected void onBindViewHolder(@NonNull AdminPostViewHolder holder, int position, @NonNull Posts model) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                String postKey = getRef(position).getKey();

                holder.adminPostUsername.setText(model.getUsername());
                holder.adminPostText.setText(model.getPostText());
                holder.adminPostTime.setText(model.getPostTime());
                holder.adminPostBookName.setText(model.getPostBookName());

                holder.adminPostDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postdb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(postKey).exists()){
                                    postdb.child(postKey).removeValue();
                                }
                                else{
                                    Toast.makeText(AdminPanelActivity.this, "An Error occured while deleting!", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public AdminPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_post_view, parent, false);
                return new AdminPostViewHolder(v);
            }
        };

        adminPostAdapter.startListening();
        adminPostRecyclerView.setAdapter(adminPostAdapter);

    }

}