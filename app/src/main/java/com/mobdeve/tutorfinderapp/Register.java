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
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
* TODO:
*  fix image uri retrieval
* update db
* update registration(dapat may 2nd page)
* */

public class Register extends AppCompatActivity {

    private TextView text_categories_tv;
    private EditText text_username;
    private EditText text_password;
    private EditText text_confirmpass;
    private EditText text_firstname;
    private EditText text_lastname;
    private EditText text_contact;
    private EditText text_categories;
    private EditText text_fee;
    private Button registerbtn;
    private ImageView uploadDpIv;
    private Uri ImageFile;
     String finalUri;
    Map<String, Object> user = new HashMap<>();;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private ArrayList<String> categories = new ArrayList<>();
    private String type;
    private float fee = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        text_categories_tv = findViewById(R.id.reg_categories_tv);
        text_username = findViewById(R.id.regEmailEt);
        text_password = findViewById(R.id.regPasswordEt);
        text_confirmpass= findViewById(R.id.regConfirmPasswordEt);
        text_lastname = findViewById(R.id.regLastNameEt);
        text_firstname = findViewById(R.id.regFirstNameEt);
        text_contact = findViewById(R.id.regContactNumEt);
        text_categories = findViewById(R.id.regCategoriesEt);
        text_fee = findViewById(R.id.regFeeEt);
        registerbtn = findViewById(R.id.regPageBtn);
        uploadDpIv= findViewById(R.id.uploadDpIv);
        text_categories_tv.setVisibility(View.GONE);
        text_categories.setVisibility(View.GONE);
        text_fee.setVisibility(View.GONE);

        Intent i = getIntent();
        type = i.getStringExtra("Type");

        if (type.equals("tutor")){
            text_categories_tv.setVisibility(View.VISIBLE);
            text_categories.setVisibility(View.VISIBLE);
            text_fee.setVisibility(View.VISIBLE);
        }

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

        if(text_username.getText().toString().isEmpty() || text_password.getText().toString().isEmpty() || text_confirmpass.getText().toString().isEmpty()
                || text_firstname.getText().toString().isEmpty()
                || text_lastname.getText().toString().isEmpty()
                || text_contact.getText().toString().isEmpty()){
            if(type.equals("tutor")){
                if(text_categories.getText().toString().isEmpty() || text_fee.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Please fill all paramaters", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(Register.this, "Please fill all paramaters", Toast.LENGTH_SHORT).show();
        }else if (text_password.getText().toString().compareTo(text_confirmpass.getText().toString())!=0){
            Toast.makeText(Register.this, "Both password must match", Toast.LENGTH_SHORT).show();
        }else if(text_password.getText().toString().length()<8 || text_confirmpass.getText().toString().length()<8 ){
            Toast.makeText(Register.this, "password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }else if(ImageFile ==null){
                Toast.makeText(Register.this, "no file selected", Toast.LENGTH_SHORT).show();
        }else {

            if(type.equals("tutor")){
                String[] strings = text_categories.getText().toString().split(",");
                fee = Float.parseFloat(text_fee.getText().toString());

                for(String s:strings){
                    String a = s.trim();
                    Log.d("trimming", "onClick: string a"+a);

                    categories.add(a.toLowerCase());
                }
            }

            createAccount(text_username.getText().toString(),
                            text_password.getText().toString(),
                            type,
                            text_firstname.getText().toString().toLowerCase(),
                            text_lastname.getText().toString().toLowerCase(),
                             text_contact.getText().toString(), categories, fee);

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
               Picasso.get().load(imageUri).fit().centerInside().into(uploadDpIv);
               ImageFile= imageUri;
            }
        }
    }

    private void addUser(Uri imageUri, String username, String firstname, String lastname,
                                 String contact, ArrayList<String> categories, float fee) {

        StorageReference fileRef= storageReference.child(text_username.getText().toString()+"profilepic.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUrl=taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        finalUri = task.getResult().toString();
                        Log.d("boo2",  finalUri);
                        user.put("Profile Picture", finalUri);
                        Log.d("eug2", user.toString());
                        user.put("Contact details", contact);
                        user.put("Email", username);
                        user.put("First name", firstname);
                        user.put("Last name", lastname);
                        if(type.equals("tutor")){
                            user.put("Categories", categories);
                            user.put("Fee", fee);
                        }

                        Log.d("eug", user.toString());

                        if(type.equals("tutor")){
                            db.collection("Tutors").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(Register.this, TutorHomePage.class);
                                    startActivity(intent);
                                    finish();
                                    Log.d("eug3", "entered");
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
                                    startActivity(intent);
                                    finish();
                                    Log.d("eug3tutee", "entered");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG","Error adding document", e);
                                }
                            });
                        }
                    }
                    });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAccount(String username, String password, String type,
                              String firstname, String lastname, String contact, ArrayList<String> categories,
                              float fee){

        Log.d("TAG", "createAccount: username"+username+" password"+password);
        mAuth.createUserWithEmailAndPassword(username, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                             Sign in success, update UI with the signed-in user's information
                            registerbtn.setEnabled(false);
                            Toast toast = Toast.makeText(getApplicationContext(), "Creating profile...", Toast.LENGTH_SHORT);
                            toast.show();
                            addUser(ImageFile, username, firstname, lastname, contact, categories, fee);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Invalid Email. Please Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}