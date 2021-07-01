package com.example.bestsummary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookPageActivity extends AppCompatActivity {

    private ImageView bookPageImg;
    TextView bookPageText, bookPageAuthor, book2homeBtn;
    Button goPostActBtn, goSummaryActBtn;
    String uid, username, email;

    FirebaseAuth mAuth;
    DatabaseReference userdb, summarydb, booksdb, likedb;

    Boolean testClick = false;

    private int likecount;


    private FirebaseRecyclerOptions<Summary> options;
    private FirebaseRecyclerAdapter<Summary, SummaryViewHolder> adapter;
    private RecyclerView recyclerView;

    private FirebaseRecyclerOptions<Summary> bestoptions;
    private FirebaseRecyclerAdapter<Summary, BestViewHolder> bestadapter;
    private RecyclerView bestRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        String BookID = getIntent().getStringExtra("BookID");

        bookPageImg = findViewById(R.id.bookPageImg);
        bookPageText = findViewById(R.id.bookPageText);
        bookPageAuthor = findViewById(R.id.bookPageAuthor);
        goPostActBtn = findViewById(R.id.goPostActBtn);
        goSummaryActBtn = findViewById(R.id.goSummaryActBtn);
        book2homeBtn = findViewById(R.id.book2homeBtn);

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        userdb = FirebaseDatabase.getInstance().getReference("Users");
        summarydb = FirebaseDatabase.getInstance().getReference().child("Summary");
        booksdb = FirebaseDatabase.getInstance().getReference().child("Books");
        likedb = FirebaseDatabase.getInstance().getReference().child("Likes");

        recyclerView = findViewById(R.id.summaryRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bestRecyclerView = findViewById(R.id.bestRecyclerView);
        bestRecyclerView.setHasFixedSize(true);
        bestRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        book2homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookPageActivity.this, HomeActivity.class));
            }
        });

        goPostActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(BookPageActivity.this, AddPostActivity.class);
                postIntent.putExtra("postBookID", BookID);
                startActivity(postIntent);
            }
        });

        goSummaryActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent summaryIntent = new Intent(BookPageActivity.this, AddSummaryActivity.class);
                summaryIntent.putExtra("summaryBookID", BookID);
                startActivity(summaryIntent);
            }
        });


        booksdb.child(BookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())   {
                    String bookName = snapshot.child("BookName").getValue().toString();
                    String author = snapshot.child("Author").getValue().toString();
                    String imgUrl = snapshot.child("ImageUrl").getValue().toString();

                    Picasso.get().load(imgUrl).into(bookPageImg);
                    bookPageText.setText(bookName);
                    bookPageAuthor.setText("Author : "+author);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query userQuery = userdb.orderByChild("email").equalTo(email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    username = "" + dataSnapshot.child("username").getValue();
                    email = "" + dataSnapshot.child("email").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String start = new StringBuilder().append(BookID).append("_").toString();
        Query bestQuery = summarydb.orderByChild("SummaryLike").startAt(start).endAt(start+"\uf8ff").limitToLast(1);

        bestoptions = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(bestQuery, Summary.class).build();
        bestadapter = new FirebaseRecyclerAdapter<Summary, BestViewHolder>(bestoptions) {
            @Override
            protected void onBindViewHolder(@NonNull BestViewHolder holder, int position, @NonNull Summary model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                String postKey = getRef(position).getKey();

                holder.bestSummaryUsername.setText(model.getSummaryUsername());
                holder.bestSummaryText.setText(model.getSummaryText());
                holder.bestSummaryTime.setText(model.getSummaryTime());

                holder.getLikeButtonStatus(postKey, userID);

                holder.bestLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        testClick = true;
                        likedb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(testClick == true){

                                    if(snapshot.child(postKey).hasChild(userID)){
                                        likedb.child(postKey).child(userID).removeValue();
                                        testClick = false;
                                        likeDec(postKey, BookID);
                                    }
                                    else{
                                        likedb.child(postKey).child(userID).setValue(true);
                                        testClick = false;
                                        likeInc(postKey, BookID);

                                    }
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
            public BestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_summary_view, parent, false);
                return new BestViewHolder(v);
            }
        };

        bestadapter.startListening();
        bestRecyclerView.setAdapter(bestadapter);




        Query summaryQuery = summarydb.orderByChild("SummaryBookID").equalTo(BookID);

        options = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(summaryQuery, Summary.class).build();
        adapter = new FirebaseRecyclerAdapter<Summary, SummaryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SummaryViewHolder holder, int position, @NonNull Summary model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                String postKey = getRef(position).getKey();

                holder.summaryUsername.setText(model.getSummaryUsername());
                holder.summaryText.setText(model.getSummaryText());
                holder.summaryTime.setText(model.getSummaryTime());

                holder.getLikeButtonStatus(postKey, userID);

                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        testClick = true;
                        likedb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(testClick == true){

                                    if(snapshot.child(postKey).hasChild(userID)){
                                        likedb.child(postKey).child(userID).removeValue();
                                        testClick = false;
                                        likeDec(postKey, BookID);
                                    }
                                    else{
                                        likedb.child(postKey).child(userID).setValue(true);
                                        likedb.child(postKey).child(userID).setValue(true);
                                        testClick = false;
                                        likeInc(postKey, BookID);

                                    }
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
            public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_view, parent, false);
                return new SummaryViewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void likeInc(String postKey, String BookID)  {

        likedb.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    likecount = (int) snapshot.getChildrenCount();
                }
                else
                    likecount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String newVal = new StringBuilder().append(BookID).append("_").append(likecount+1).toString();
        summarydb.child(postKey).child("SummaryLike").setValue(newVal);

    }


    private void likeDec(String postKey, String BookID) {

        likedb.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likecount = (int) snapshot.getChildrenCount();
                } else
                    likecount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String newVal = new StringBuilder().append(BookID).append("_").append(likecount-1).toString();
        summarydb.child(postKey).child("SummaryLike").setValue(newVal);
    }

    private void checkUserStatus()  {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)    {
            email = user.getEmail();
            uid = user.getUid();
        }
    }

}