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

public class AddSummaryActivity extends AppCompatActivity {

    TextView testView2, sum2homeBtn;
    EditText summaryEditText;
    Button shareSummaryBtn;


    FirebaseAuth mAuth;
    DatabaseReference booksdb, userdb, postdb, summarydb;
    String uid, username, email;

    String bookName;

    private long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        String summaryBookID = getIntent().getStringExtra("summaryBookID");

        testView2 = findViewById(R.id.testView2);
        sum2homeBtn = findViewById(R.id.sum2homeBtn);
        summaryEditText = findViewById(R.id.summaryEditText);
        shareSummaryBtn = findViewById(R.id.shareSummaryBtn);

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        userdb = FirebaseDatabase.getInstance().getReference("Users");
        postdb = FirebaseDatabase.getInstance().getReference("Posts");
        booksdb = FirebaseDatabase.getInstance().getReference().child("Books");
        summarydb = FirebaseDatabase.getInstance().getReference().child("Summary");

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

        summarydb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    maxid = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        booksdb.child(summaryBookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())   {
                    bookName = snapshot.child("BookName").getValue().toString();
                    testView2.setText("You are posting for : " + bookName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sum2homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddSummaryActivity.this, HomeActivity.class));
            }
        });

        shareSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null)    {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    String summaryTime = dateFormat.format(calendar.getTime());

                    String summaryText = summaryEditText.getText().toString().trim();

                    if(summaryText.isEmpty()){
                        summaryEditText.setError("Please type something.");
                        summaryEditText.requestFocus();
                        return;
                    }
                    if(summaryEditText.length() > 2000){
                        summaryEditText.setError("Maximum characters limit!");
                        summaryEditText.requestFocus();
                        return;
                    }
                    if(summaryEditText.length() < 12){
                        summaryEditText.setError("Please type at least 12 characters.");
                        summaryEditText.requestFocus();
                        return;
                    }

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("SummaryBookID", summaryBookID);
                    hashMap.put("SummaryText", summaryText);
                    hashMap.put("SummaryTime", summaryTime);
                    hashMap.put("SummaryLike", summaryBookID+"_0");
                    hashMap.put("SummaryUserID", uid);
                    hashMap.put("SummaryUsername", username);
                    hashMap.put("SummaryUserMail", email);


                    DatabaseReference summarydb = FirebaseDatabase.getInstance().getReference("Summary");
                    summarydb.child(String.valueOf(maxid+2)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddSummaryActivity.this, "Succesfully shared.", Toast.LENGTH_SHORT).show();
                            summaryEditText.getText().clear();
                            startActivity(new Intent(AddSummaryActivity.this, HomeActivity.class));
                        }
                    });

                }
                else{
                    Toast.makeText(AddSummaryActivity.this, "Login before share a summary.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddSummaryActivity.this, LoginActivity.class));
                    finish();
                }
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