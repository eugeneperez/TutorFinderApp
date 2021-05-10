package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register_role extends AppCompatActivity {

    private Button tutorBtn;
    private Button tuteeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_role);

        tutorBtn = findViewById(R.id.tutorBtn);
        tuteeBtn = findViewById(R.id.tuteeBtn);

        tutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tutorInt = new Intent(Register_role.this, Register.class);
                tutorInt.putExtra("Type", "tutor");
                startActivity(tutorInt);
            }
        });
        tuteeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuteeInt = new Intent(Register_role.this, Register.class);
                tuteeInt.putExtra("Type", "tutee");
                startActivity(tuteeInt);
            }
        });
    }
}