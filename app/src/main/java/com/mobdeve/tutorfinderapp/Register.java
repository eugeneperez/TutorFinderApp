package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText text_username;
    private EditText text_password;
    private EditText text_firstname;
    private EditText text_lastname;
    private EditText text_contact;

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String contact;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        text_username = findViewById(R.id.regEmailEt);
        text_password = findViewById(R.id.regPasswordEt);
        //text_firstname = findViewById(R.id.reg);
        text_contact = findViewById(R.id.regContactNumEt);

        Intent i = getIntent();
        String type = i.getStringExtra("Type");

        createAccount(username, password, type, firstname, lastname, contact);
    }

    public void createAccount( String username, String password, String type,
                               String firstname, String lastname, String contact){

        mAuth.createUserWithEmailAndPassword(username, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent pasokgago = new Intent(Register.this, Homepage.class);
                            

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "", Toast.LENGTH_SHORT).show();.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}