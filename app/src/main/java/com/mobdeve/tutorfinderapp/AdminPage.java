package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPage extends AppCompatActivity {

    private Button resetbtn;
    private FirebaseAuth mAuth;


    private void destroyDB(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        resetbtn = findViewById(R.id.resetdbBtn);

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //destroyDB();
                //populateDB();
            }
        });
    }
}