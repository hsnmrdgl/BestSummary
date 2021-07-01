package com.example.bestsummary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    TextView testView, post2homeBtn;
    EditText postEditText;
    Button sharePostBtn;

    FirebaseAuth mAuth;
    DatabaseReference booksdb, userdb, postdb;
    String uid, username, email;

    String bookName;

    private long counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        String postBookID = getIntent().getStringExtra("postBookID");

        testView = findViewById(R.id.testView);
        postEditText = findViewById(R.id.postEditText);
        sharePostBtn = findViewById(R.id.sharePostBtn);
        post2homeBtn = findViewById(R.id.post2homeBtn);


        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        userdb = FirebaseDatabase.getInstance().getReference("Users");
        postdb = FirebaseDatabase.getInstance().getReference("Posts");
        booksdb = FirebaseDatabase.getInstance().getReference().child("Books");

        Query query = userdb.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
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
        
        booksdb.child(postBookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())   {
                    bookName = snapshot.child("BookName").getValue().toString();
                    testView.setText("You are posting for : " + bookName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    counter = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        post2homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostActivity.this, HomeActivity.class));
            }
        });

        sharePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                String postTime = dateFormat.format(calendar.getTime());

                SimpleDateFormat stampFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                String timeStamp = stampFormat.format(calendar.getTime());

                String postText = postEditText.getText().toString().trim();

                if(postText.isEmpty()){
                    postEditText.setError("Please type something.");
                    postEditText.requestFocus();
                    return;
                }

                if(postEditText.length() < 12){
                    postEditText.setError("Please type at least 12 characters.");
                    postEditText.requestFocus();
                    return;
                }

                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("PostText", postText);
                hashMap.put("PostTime", postTime);
                hashMap.put("postBookName", bookName);
                hashMap.put("UserId", uid);
                hashMap.put("Username", username);
                hashMap.put("UserMail", email);
                hashMap.put("counter", String.valueOf(999999-counter));


                DatabaseReference postDb = FirebaseDatabase.getInstance().getReference("Posts");
                postDb.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddPostActivity.this, "Succesfully shared.", Toast.LENGTH_SHORT).show();
                        postEditText.getText().clear();
                        startActivity(new Intent(AddPostActivity.this, PostsActivity.class));
                    }
                });
            }
        });

    }

    private void checkUserStatus()  {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)    {
            email = user.getEmail();
            uid = user.getUid();
        }
    }
}