package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailedInfo extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Map<String, Object> tutor = new HashMap<>();
    private Map<String, Object> tutee = new HashMap<>();
    private User user;

    private TextView text_name;
    private TextView text_email;
    private TextView text_categories;
    private TextView text_contact;
    private TextView text_fee;
    private TextView text_average_rating;
    private ImageView image_profile;
    private Button request_btn;
    private Button see_more_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        text_name = findViewById(R.id.detailed_name);
        text_email = findViewById(R.id.detailed_email);
        text_categories = findViewById(R.id.detailed_categories);
        text_contact = findViewById(R.id.detailed_contact);
        text_fee = findViewById(R.id.detailed_fee);
        text_average_rating = findViewById(R.id.review_averating);
        image_profile = findViewById(R.id.detailed_image);
        request_btn = findViewById(R.id.detailed_requestbtn);
        see_more_btn = findViewById(R.id.detailed_info_see_more_reviews_btn);

        //Gets the chosen tutor from the searchpage or homepage
        Gson gson = new Gson();
        Intent i = getIntent();
        String json = i.getStringExtra("User");
        user = gson.fromJson(json, User.class);
        String name = user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1)
                + " " + user.getLastname().substring(0, 1).toUpperCase() + user.getLastname().substring(1);
        String categories = new String();
        String imgUri=user.getProfpic();

        for(String category:user.getCategories()){
            categories += category.substring(0, 1).toUpperCase()+category.substring(1)+"\n";
        }

        text_name.setText(name);
        text_email.setText(user.getEmail());
        text_categories.setText(categories);
        text_contact.setText(user.getContact());
        text_fee.setText(user.getFee());
        Picasso.get().load(imgUri).fit().centerInside().into(image_profile);

        //checks if the tutee has already hired the tutor
        db.collection("Tutees")
                .whereEqualTo("Email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                tutee = document.getData();

                                ArrayList<Map> tutorList = new ArrayList<>();
                                int index = -1;

                                if(result.get("Tutor List") != null){
                                    tutorList = (ArrayList<Map>) result.get("Tutor List");
                                    for(Map tutor: tutorList){
                                        if(tutor.get("Partner").toString().equals(user.getEmail())){
                                            index = 1;
                                        }
                                    }
                                }

                                if(index != -1){
                                    request_btn.setEnabled(false);
                                    request_btn.setBackgroundColor(Color.parseColor("#d8d8d8"));
                                    request_btn.setTextColor(Color.parseColor("#8b8b8b"));
                                }
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updates the tutee list of the tutor
                db.collection("Tutors")
                        .whereEqualTo("Email", user.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        tutor = document.getData();
                                        ArrayList<Map> tuteeList = new ArrayList<>();

                                        Map<String, Object> tutee = new HashMap<>();

                                        tutee.put("Partner", currentUser.getEmail());
                                        tutee.put("Status", "Request");

                                        if(tutor.get("Tutee List") != null){
                                            tuteeList = (ArrayList<Map>) tutor.get("Tutee List");
                                            int index = -1;
                                            for(Map t: tuteeList){
                                                if(t.get("Partner").toString().equals(currentUser.getEmail())){
                                                    index = tuteeList.indexOf(t);
                                                }
                                            }
                                            if(index != -1)
                                                tuteeList.remove(index);
                                        }

                                        tuteeList.add(tutee);

                                        tutor.put("Tutee List", tuteeList);

                                        db.collection("Tutors")
                                                .document(document.getId())
                                                .set(tutor)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("SETTING1", "DocumentSnapshot successfully written!");
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
                                //updates the tutor list of the user
                                db.collection("Tutees")
                                        .whereEqualTo("Email", currentUser.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> result = document.getData();
                                                        tutee = document.getData();
                                                        ArrayList<Map> tutorList = new ArrayList<>();

                                                        Map<String, Object> tutor2 = new HashMap<>();

                                                        tutor2.put("Partner", user.getEmail());
                                                        tutor2.put("Status", "Request");

                                                        if(tutee.get("Tutor List") != null){
                                                            tutorList = (ArrayList<Map>) tutee.get("Tutor List");
                                                            int index = -1;
                                                            for(Map t: tutorList){
                                                                if(t.get("Partner").toString().equals(user.getEmail())){
                                                                    index = tutorList.indexOf(t);
                                                                }
                                                            }
                                                            if(index != -1){
                                                                tutorList.remove(index);
                                                            }
                                                        }

                                                        tutorList.add(tutor2);

                                                        tutee.put("Tutor List", tutorList);
                                                        db.collection("Tutees")
                                                                .document(document.getId())
                                                                .set(tutee)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d("SETTING2", "DocumentSnapshot successfully written!");
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
                                                Intent i = new Intent(DetailedInfo.this, ViewTuteeListOfTutors.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                            }
                        });
            }
        });

        see_more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedInfo.this, SeeMoreReviews.class);
                intent.putExtra("User", json);
                startActivity(intent);
            }
        });

        //gets the average rating of the tutor
        db.collection("Tutors")
                .whereEqualTo("Email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> result = document.getData();
                                if(result.get("Average Rating")!=null) {
                                    text_average_rating.setText(result.get("Average Rating").toString());
                                }
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}