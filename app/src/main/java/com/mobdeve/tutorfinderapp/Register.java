package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    private EditText username;
    private EditText password;


    public void createAccount(String type, String username, String password){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent i = getIntent();
        String type = i.getStringExtra("Type");

        if(type.equals("tutor")){

        }else if(type.equals("tutee")){

        }
    }
}