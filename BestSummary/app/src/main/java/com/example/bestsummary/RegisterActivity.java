package com.example.bestsummary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.nio.file.ReadOnlyFileSystemException;

public class RegisterActivity extends AppCompatActivity {

    private TextView welcomeText,welcomeText2,textButtonRegister;
    private EditText editUsername,editMail,editPassw,editPassw2;
    private Button buttonRegister;
    private ProgressBar progressBar;

    DatabaseReference db;
    private FirebaseAuth mAuth;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        textButtonRegister = (TextView) findViewById(R.id.textButtonRegister);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editUsername = (EditText) findViewById(R.id.editUsername);
        editMail = (EditText) findViewById(R.id.editMail);
        editPassw = (EditText) findViewById(R.id.editPassw);
        editPassw2 = (EditText) findViewById(R.id.editPassw2);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
            }

        });

        textButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignIn();
                editUsername.getText().clear();
                editMail.getText().clear();
                editPassw.getText().clear();
                editPassw2.getText().clear();
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        else    {
            Toast.makeText(RegisterActivity.this, "Press again to exit", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void registerUser(){

        String username = editUsername.getText().toString().trim();
        String email = editMail.getText().toString().trim();
        String passw = editPassw.getText().toString().trim();
        String passw2 = editPassw2.getText().toString().trim();

        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").equalTo(username);

        if(username.isEmpty()){
            editUsername.setError("Username Required!");
            editUsername.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editMail.setError("Email Required!");
            editMail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editMail.setError("Please Enter Valid Email!");
            editMail.requestFocus();
            return;
        }
        if(passw.isEmpty()){
            editPassw.setError("Password Required!");
            editPassw.requestFocus();
            return;
        }
        if(passw.length() < 6 || passw.length() > 12){
            editPassw.setError("Password Length 6-12 Characters!");
            editPassw.requestFocus();
            return;
        }
        if(passw2.isEmpty()){
            editPassw2.setError("Password Again Required!");
            editPassw2.requestFocus();
            return;
        }
        if(!passw.equals(passw2)){
            editPassw2.setError("Password Mismatch");
            editPassw2.requestFocus();
            return;
        }

        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(RegisterActivity.this,
                            "Failed to Register! This username can be using by another user.", Toast.LENGTH_LONG).show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, passw)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Users user = new Users(username, email, passw);

                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Succesfully Registered! Please sign-in now.",
                                                            Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    goSignIn();
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, "Failed to Register! Please try again.", Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Failed to Register! This email can be using by another user.",
                                                Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void goSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}