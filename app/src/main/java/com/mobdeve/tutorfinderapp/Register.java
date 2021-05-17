package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/*
* TODO:
*  line 91
*  line 101
*  amazing
* */

public class Register extends AppCompatActivity {

    private EditText text_username;
    private EditText text_password;
    private EditText text_firstname;
    private EditText text_lastname;
    private EditText text_contact;
    private Button registerbtn;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        text_username = findViewById(R.id.regEmailEt);
        text_password = findViewById(R.id.regPasswordEt);
        text_lastname = findViewById(R.id.regLastNameEt);
        text_firstname = findViewById(R.id.regFirstNameEt);
        text_contact = findViewById(R.id.regContactNumEt);
        registerbtn = findViewById(R.id.regPageBtn);


        Intent i = getIntent();
        String type = i.getStringExtra("Type");



        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(text_username.getText().toString(),
                        text_password.getText().toString(),
                        type, text_firstname.getText().toString(),
                        text_lastname.getText().toString(), text_contact.getText().toString());
            }
        });
    }

    public void createAccount( String username, String password, String type,
                               String firstname, String lastname, String contact){
        Log.d("TAG", "createAccount: username"+username+" password"+password);
        mAuth.createUserWithEmailAndPassword(username, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Map<String, Object> user = new HashMap<>();
                            user.put("Contact details", contact);
                            user.put("Email", username);
                            user.put("First name", firstname);
                            user.put("Last name", lastname);
                            if(type.equals("tutor")){
                                db.collection("Tutors").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Intent pasokgago = new Intent(Register.this, Homepage.class);
                                        startActivity(pasokgago);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error adding document", e);
                                    }
                                });
                        }else if (type.equals("tutee")){
                                db.collection("Tutees").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Intent pasokgago = new Intent(Register.this, Homepage.class);
                                        //add Tutor to collection
                                        startActivity(pasokgago);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG","Error adding document", e);
                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}