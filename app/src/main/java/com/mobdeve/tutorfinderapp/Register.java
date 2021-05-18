package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/*
* TODO:
* */

public class Register extends AppCompatActivity {

    private EditText text_username;
    private EditText text_password;
    private EditText text_confirmpass;
    private EditText text_firstname;
    private EditText text_lastname;
    private EditText text_contact;
    private Button registerbtn;
    private ImageView uploadDpIv;
    private Uri ImageFile;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    public Register() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();


        text_username = findViewById(R.id.regEmailEt);
        text_password = findViewById(R.id.regPasswordEt);
        text_confirmpass= findViewById(R.id.regConfirmPasswordEt);
        text_lastname = findViewById(R.id.regLastNameEt);
        text_firstname = findViewById(R.id.regFirstNameEt);
        text_contact = findViewById(R.id.regContactNumEt);
        registerbtn = findViewById(R.id.regPageBtn);
        uploadDpIv= findViewById(R.id.uploadDpIv);


        Intent i = getIntent();
        String type = i.getStringExtra("Type");


        uploadDpIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent openGalleryIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, 1000);
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(text_username.getText().toString().isEmpty() || text_password.getText().toString().isEmpty() || text_confirmpass.getText().toString().isEmpty() || text_firstname.getText().toString().isEmpty() || text_lastname.getText().toString().isEmpty() || text_contact.getText().toString().isEmpty()){
            Toast.makeText(Register.this, "Please fill all paramaters", Toast.LENGTH_SHORT).show();
        }else if (text_password.getText().toString().compareTo(text_confirmpass.getText().toString())!=0){
            Toast.makeText(Register.this, "Both password must match", Toast.LENGTH_SHORT).show();
        }else if(text_password.getText().toString().length()<8 || text_confirmpass.getText().toString().length()<8 ){
            Toast.makeText(Register.this, "password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }else {
            createAccount(text_username.getText().toString(),
                            text_password.getText().toString(),
                            type,
                            text_firstname.getText().toString().substring(0,1).toUpperCase()+text_firstname.getText().toString().substring(1),
                            text_lastname.getText().toString().substring(0,1).toUpperCase()+text_lastname.getText().toString().substring(1),
                             text_contact.getText().toString(), ImageFile);

        }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                Uri imageUri= data.getData();
                uploadDpIv.setImageURI(imageUri);
                ImageFile = imageUri;
            }
        }
    }

    private void uploadImagetoFB(Uri imageUri) {
        StorageReference fileRef= storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Register.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAccount(String username, String password, String type,
                              String firstname, String lastname, String contact, Uri imageFile){
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
                            user.put("Profile Picture", imageFile.toString());

                            if(type.equals("tutor")){
                                db.collection("Tutors").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        uploadImagetoFB(imageFile);
                                        Log.d("PANGET SI EUG", "SUCCESS" );
                                        Intent intent = new Intent(Register.this, Homepage.class);
                                        startActivity(intent);
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
                                        Intent intent = new Intent(Register.this, Homepage.class);
                                        //add Tutor to collection
                                        startActivity(intent);
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
                            Toast.makeText(Register.this, "Email already exist", Toast.LENGTH_SHORT).show();
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