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

public class Login extends AppCompatActivity {
    private String username;
    private String password;
    private EditText text_username;
    private EditText text_password;
    private Button loginbtn;
    private FirebaseAuth mAuth;


    private void authLogin(String username, String password){
        if(username.equals("admin") && password.equals("password")){
            Intent adminIntent = new Intent(Login.this, AdminPage.class);
            startActivity(adminIntent);
            finish();
        }else {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent= new Intent(Login.this, Homepage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Incorrect credentials. Tangina mo",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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