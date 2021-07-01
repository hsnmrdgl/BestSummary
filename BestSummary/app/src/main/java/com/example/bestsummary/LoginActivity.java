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

public class LoginActivity extends AppCompatActivity {

    TextView loginWelc,textButtonLogin;
    EditText loginMail,loginPassw;
    Button buttonLogin;
    ProgressBar loginProgressBar;

    private FirebaseAuth mAuth;

    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textButtonLogin = (TextView) findViewById(R.id.textLoginButton);

        loginMail = (EditText) findViewById(R.id.loginMail);
        loginPassw = (EditText) findViewById(R.id.loginPassw);

        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
            Toast.makeText(LoginActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }



    public void loginUser(){
        String email = loginMail.getText().toString().trim();
        String passw = loginPassw.getText().toString().trim();

        if(email.isEmpty()){
            loginMail.setError("Email Required!");
            loginMail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginMail.setError("Please Enter Valid Email!");
            loginMail.requestFocus();
            return;
        }
        if(passw.isEmpty()) {
            loginPassw.setError("Password Required!");
            loginPassw.requestFocus();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (email.equals("admin@bestsummary.com") && task.isSuccessful()) {
                    loginProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            "Welcome Admin!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class));
                    loginMail.getText().clear();
                    loginPassw.getText().clear();
                    loginPassw.clearFocus();
                }


                else if(task.isSuccessful()){
                    loginProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            "Succesfully signed in.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    loginMail.getText().clear();
                    loginPassw.getText().clear();
                    loginPassw.clearFocus();
                }
                else{
                    loginProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            "Failed to sign in! Please check email/password.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}