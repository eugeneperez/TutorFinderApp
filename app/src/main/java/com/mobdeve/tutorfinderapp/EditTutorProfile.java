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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditTutorProfile extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageReference;

    private ImageView profile_picture;
    private TextView text_firstname;
    private TextView text_lastname;
    private TextView text_contact;
    private TextView text_email;
    private TextView text_categories;
    private TextView text_fee;
    private Button btn_save;

    private Uri upload_picture;
    private String firstname;
    private String lastname;
    private String contact;
    private String image;
    private ArrayList<String> categories= new ArrayList<>();
    private String fee;
    private boolean changedProfilePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutor_profile);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent i = getIntent();

        profile_picture = findViewById(R.id.edit_tutor_profile_pic);
        text_firstname = findViewById(R.id.edit_tutor_profile_firstname);
        text_lastname = findViewById(R.id.edit_tutor_profile_lastname);
        text_contact = findViewById(R.id.edit_tutor_profile_contactdetails);
        text_email = findViewById(R.id.edit_tutor_email);
        text_categories= findViewById(R.id.edit_tutor_profile_categories);
        text_fee= findViewById(R.id.edit_tutor_profile_fee);
        btn_save = findViewById(R.id.edit_tutor_profile_save_btn);

        text_email.setText(user.getEmail());
        firstname = i.getStringExtra("First name");
        text_firstname.setText(firstname);
        lastname = i.getStringExtra("Last name");
        text_lastname.setText(lastname);
        contact = i.getStringExtra("Contact details");
        text_contact.setText(contact);
        String strCategories= i.getStringExtra("Categories");
        strCategories= strCategories.substring(0, strCategories.length()-2);
        text_categories.setText(strCategories);
        fee= i.getStringExtra("Fee");
        text_fee.setText(fee);
        image = i.getStringExtra("Profile Picture");
        Picasso.get().load(image).fit().centerInside().into(profile_picture);


        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstname = text_firstname.getText().toString();
                lastname = text_lastname.getText().toString();
                contact = text_contact.getText().toString();
                fee= text_fee.getText().toString();

                String[] strings = text_categories.getText().toString().split(",");
                for(String s:strings){
                    String a = s.trim();
                    Log.d("trimming", "onClick: string a"+a);
                    if(!isBlank(s)){
                        categories.add(a.toLowerCase());
                    }
                }

                if(firstname.isEmpty() || lastname.isEmpty() || contact.isEmpty() || categories.isEmpty() || fee.isEmpty()){
                    Toast.makeText(EditTutorProfile.this, "Please fill all paramaters", Toast.LENGTH_SHORT).show();
                }else if(changedProfilePicture){
                    StorageReference fileRef= storageReference.child(user.getEmail()+"profilepic.jpg");
                    fileRef.putFile(upload_picture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl=taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    image = task.getResult().toString();
                                    saveEditProfile();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditTutorProfile.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    saveEditProfile();
                }
            }
        });
    }

    private void saveEditProfile(){
        db.collection("Tutors")
                .whereEqualTo("Email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.getId() + " => " + document.getData());
                                Gson gson = new Gson();
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();

                                if(changedProfilePicture){
                                    result.put("Profile Picture", image);
                                }

                                result.put("First name", firstname);
                                result.put("Last name", lastname);
                                result.put("Contact details", contact);
                                result.put("Categories", categories);
                                result.put("Fee", fee);

                                db.collection("Tutors")
                                        .document(document.getId())
                                        .set(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SETTING", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("SETTING", "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }

                        Intent i = new Intent(EditTutorProfile.this, ViewTutorProfile.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    public static boolean isBlank(String text) {
        if (text.isEmpty())
            return true;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i)))
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                changedProfilePicture = true;
                Uri imageUri= data.getData();
                Picasso.get().load(imageUri).fit().centerInside().into(profile_picture);
                upload_picture = imageUri;
            }
        }
    }
}