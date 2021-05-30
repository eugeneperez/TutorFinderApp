package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedbackPage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ImageView star1Iv;
    private ImageView star2Iv;
    private ImageView star3Iv;
    private ImageView star4Iv;
    private ImageView star5Iv;
    private EditText commentEt;
    private Button submitFeedbackBtn;
    private int countStars=0;
    private int ratings = 0;
    private int total = 0;
    private float aveRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_page);

        star1Iv= findViewById(R.id.star1Iv);
        star2Iv= findViewById(R.id.star2Iv);
        star3Iv= findViewById(R.id.star3Iv);
        star4Iv= findViewById(R.id.star4Iv);
        star5Iv= findViewById(R.id.star5Iv);
        commentEt=findViewById(R.id.commentEt);
        submitFeedbackBtn=findViewById(R.id.submitFeedbackBtn);

        Intent i = getIntent();

        String tutorEmail = i.getStringExtra("Tutor Email");

        star1Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countStars = 1;
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
            }
        });
        star2Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countStars = 2;
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
            }
        });
        star3Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countStars = 3;
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
            }
        });
        star4Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countStars = 4;
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_outline_24);
            }
        });
        star5Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countStars = 5;
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });

        submitFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countStars == 0){
                    Toast.makeText(FeedbackPage.this, "Please enter a rating", Toast.LENGTH_SHORT).show();
                }else{
                    String comment = commentEt.getText().toString();
                    Date date = new Date();

                    Map<String, Object> review = new HashMap<>();

                    review.put("Date", date);
                    review.put("Rating", countStars);
                    review.put("Review", comment);
                    review.put("Tutee", user.getEmail());
                    review.put("Tutor", tutorEmail);

                    db.collection("Reviews")
                            .document()
                            .set(review)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(FeedbackPage.this, "Review has been posted!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error writing document", e);
                                }
                            });
                    db.collection("Tutors")
                            .whereEqualTo("Email", tutorEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> result = document.getData();
                                            if(result.get("Total Tutees") != null){
                                                total = Integer.parseInt(result.get("Total Tutees").toString());
                                            }
                                            total++;
                                            if(result.get("Total Ratings") != null){
                                                ratings = Integer.parseInt(result.get("Total Ratings").toString());
                                            }
                                            ratings += countStars;
                                            aveRating = ratings / total;

                                            result.put("Total Tutees", total);
                                            result.put("Total Ratings", ratings);
                                            result.put("Average Rating", aveRating);

                                            db.collection("Tutors")
                                                    .document(document.getId())
                                                    .set(result)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("TAG", "DocumentSnapshot successfully written!");
                                                            Toast.makeText(FeedbackPage.this, "Review has been posted!", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(FeedbackPage.this, ViewTuteeListOfTutors.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("TAG", "Error writing document", e);
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d("TAG1", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });
    }}
