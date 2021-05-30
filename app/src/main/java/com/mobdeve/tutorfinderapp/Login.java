package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private String username;
    private String password;
    private EditText text_username;
    private EditText text_password;
    private Button loginbtn;
    private FirebaseAuth mAuth;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private void authLogin(String username, String password){
        if(username.equals("admin") && password.equals("password")) {
            Intent adminIntent = new Intent(Login.this, AdminPage.class);
            startActivity(adminIntent);
            finish();
        }else {
            if(username.equals("") || password.equals("")){
                Toast.makeText(Login.this, "Input username and password",
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Login.this, "Logging in...",
                        Toast.LENGTH_SHORT).show();
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    db.collection("Tutors")
                                            .whereEqualTo("Email", user.getEmail())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d("TAG1", document.getId() + " => " + document.getData());
                                                            Map<String, Object> result = new HashMap<>();
                                                            result = document.getData();
                                                            if (user.getEmail().equals(result.get("Email"))) {
                                                                Intent i = new Intent(Login.this, TutorHomePage.class);
                                                                //current tutee and request tutee put in intent extra
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        }
                                                    } else {
                                                        Log.d("TAG1", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                    db.collection("Tutees")
                                            .whereEqualTo("Email", user.getEmail())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d("TAG1", document.getId() + " => " + document.getData());
                                                            Map<String, Object> result = new HashMap<>();
                                                            result = document.getData();
                                                            if (user.getEmail().equals(result.get("Email"))) {
                                                                Intent i = new Intent(Login.this, Homepage.class);
                                                                i.putExtra("First name", result.get("First name").toString());
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        }
                                                    } else {
                                                        Log.d("TAG1", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Incorrect credentials. Try Again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }

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

                authLogin(username, password);
            }
        });
    }


}