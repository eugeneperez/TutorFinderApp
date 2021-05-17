package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private String username;
    private String password;
    private EditText text_username;
    private EditText text_password;
    private Button loginbtn;
    private FirebaseAuth mAuth;


    private int authLogin(String username, String password){
        if(password.length() < 8){
            return 0;
        }else if(username.equals("admin") && password.equals("password")){
            Intent adminintent = new Intent(Login.this, AdminPage.class);
            startActivity(adminintent);
            return 1;
        }else if(username.equals("user") && password.equals("password")){
            Intent userintent = new Intent(Login.this, Homepage.class);
            startActivity(userintent);
            return 2;
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        text_username = findViewById(R.id.loginEmailEt);
        text_password = findViewById(R.id.loginPasswordEt);
        loginbtn = findViewById(R.id.loginPageBtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = text_username.getText().toString();
                password = text_password.getText().toString();

                if(authLogin(username, password) != 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "successfully logged in", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent= new Intent(Login.this, Homepage.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "wrong login creds bro", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser == null){
//            Intent intent = new Intent(Login.this, Login.class);
//            startActivity(intent);
//            finish();
//        }else{
//            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(Login.this, Homepage.class);
//            startActivity(i);
//        }
}

}