package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditTuteeProfile extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageReference;

    private ImageView profile_picture;
    private TextView text_firstname;
    private TextView text_lastname;
    private TextView text_contact;
    private TextView text_email;
    private Button btn_save;

    private Uri upload_picture;
    private String firstname;
    private String lastname;
    private String contact;
    private String image;
    private boolean changedProfilePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutee_profile);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent i = getIntent();

        profile_picture = findViewById(R.id.edit_tutee_profile_pic);
        text_firstname = findViewById(R.id.edit_tutee_profile_firstname);
        text_lastname = findViewById(R.id.edit_tutee_profile_lastname);
        text_contact = findViewById(R.id.edit_tutee_profile_contactdetails);
        text_email = findViewById(R.id.edit_tutee_email);
        btn_save = findViewById(R.id.edit_tutee_profile_save_btn);

        text_email.setText(user.getEmail());
        firstname = i.getStringExtra("First name");
        text_firstname.setText(firstname);
        lastname = i.getStringExtra("Last name");
        text_lastname.setText(lastname);
        contact = i.getStringExtra("Contact details");
        text_contact.setText(contact);
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

                if(firstname.isEmpty() || lastname.isEmpty() || contact.isEmpty()){
                    Toast.makeText(EditTuteeProfile.this, "Please fill all paramaters", Toast.LENGTH_SHORT).show();
                }else if(changedProfilePicture){
                    StorageReference fileRef= storageReference.child(user.getEmail()+"profilepic.jpg");
                    fileRef.putFile(upload_picture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl=taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    image = task.getResult().toString();
                                    firstname.toLowerCase();
                                    lastname.toLowerCase();
                                    saveEditProfile();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditTuteeProfile.this, "Image Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    saveEditProfile();
                }
            }
        });
    }

    private void saveEditProfile(){
        db.collection("Tutees")
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

                                db.collection("Tutees")
                                        .document(document.getId())
                                        .set(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SETTING", "DocumentSnapshot successfully written!");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(EditTuteeProfile.this);
                                                alert.setTitle("Updated Profile");
                                                alert.setMessage("Your Profile has been updated.");
                                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                alert.show();
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
                    }
                });
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