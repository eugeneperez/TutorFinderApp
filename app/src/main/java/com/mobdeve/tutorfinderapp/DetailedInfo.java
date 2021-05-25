package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailedInfo extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Map<String, Object> tutor = new HashMap<>();
    private Map<String, Object> tutee = new HashMap<>();
    private ArrayList<Map> reviews = new ArrayList<>();
    private String tutorUid;
    private String tuteeUid;
    private User user;
    private float aveRating = 0;

    private ReviewAdapter adapter;
    private TextView text_name;
    private TextView text_email;
    private TextView text_categories;
    private TextView text_contact;
    private TextView text_fee;
    private TextView text_average_rating;
    private ImageView image_profile;
    private Button request_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        tuteeUid = currentUser.getUid();

        text_name = findViewById(R.id.detailed_name);
        text_email = findViewById(R.id.detailed_email);
        text_categories = findViewById(R.id.detailed_categories);
        text_contact = findViewById(R.id.detailed_contact);
        text_fee = findViewById(R.id.detailed_fee);
        text_average_rating = findViewById(R.id.review_averating);
        image_profile = findViewById(R.id.detailed_image);
        request_btn = findViewById(R.id.detailed_requestbtn);

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

        request_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.collection("Tutors")
                        .whereEqualTo("Email", user.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG12", document.getId() + " => " + document.getData());
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
                                        tutorUid = document.getId();

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

                                                        Map<String, Object> tutor2 = new HashMap<>();

                                                        tutor2.put("Partner", user.getEmail());
                                                        tutor2.put("Status", "Request");

                                                        if(tutee.get("Tutor List") != null){
                                                            tutorList = (ArrayList<Map>) tutee.get("Tutor List");
                                                            int index = -1;
                                                            for(Map t: tutorList){
                                                                if(t.get("Partner").toString().equals(user.getEmail())){
                                                                    index = tutorList.indexOf(t);
                                                                    Log.d("SETTINGINDEX", "onComplete: INDEX "+index);
                                                                }
                                                            }
                                                            if(index != -1){
                                                                tutorList.remove(index);
                                                            }
                                                        }

                                                        tutorList.add(tutor2);
                                                        Log.d("ADDINGREQUEST", "onComplete: tutor2 "+tutor2);
                                                        Log.d("ADDINGREQUEST", "onComplete: tuteeUid "+tuteeUid+" UID"+document.getId());

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

        RecyclerView rv_review = findViewById(R.id.reviews_rv);
        adapter = new ReviewAdapter(reviews);
        rv_review.setAdapter(adapter);
        rv_review.setLayoutManager(new LinearLayoutManager(DetailedInfo.this));

        db.collection("Reviews")
                .whereEqualTo("Tutor", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG12", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                Log.d("REVIEWS", "onComplete: currentUser "+currentUser.getEmail());

                                Timestamp timestamp = (Timestamp) result.get("Date");
                                Date newDate = timestamp.toDate();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMMM/yyyy");
                                result.put("Date", timestamp.toDate());

                                db.collection("Tutees")
                                        .whereEqualTo("Email", result.get("Tutee").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d("TAG12", document.getId() + " => " + document.getData());
                                                        Map<String, Object> tutee = document.getData();
                                                        Log.d("REVIEWS", "onComplete: currentUser "+currentUser.getEmail());

                                                        String firstname = tutee.get("First name").toString();
                                                        String lastname = tutee.get("Last name").toString();
                                                        String fullname = firstname.substring(0, 1).toUpperCase()+firstname.substring(1) + " " +
                                                                lastname.substring(0, 1).toUpperCase() + lastname.substring(1);


                                                        result.put("Tutee Name", fullname);
                                                        result.put("Tutee Profile Picture", tutee.get("Profile Picture").toString());

                                                        reviews.add(result);

                                                    }
                                                } else {
                                                    Log.d("TAG1", "Error getting documents: ", task.getException());
                                                }

                                                float ratings = 0;
                                                int total = 0;

                                                for(Map review:reviews){
                                                    total++;
                                                    ratings += Float.parseFloat(review.get("Rating").toString());
                                                }

                                                aveRating = ratings / total;

                                                text_average_rating.setText(Float.toString(aveRating));

                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public class ReviewAdapter extends RecyclerView.Adapter<DetailedInfo.ReviewAdapter.ViewHolder> {
        private ArrayList<Map> reviewList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView review_text_name;
            private TextView review_text_rating;
            private TextView review_text_date;
            private TextView review_text_review;
            private ImageView review_image_profile;

            public ViewHolder(View view){
                super(view);
                review_text_name = view.findViewById(R.id.review_name);
                review_text_rating = view.findViewById(R.id.review_rating);
                review_text_date = view.findViewById(R.id.review_date);
                review_text_review = view.findViewById(R.id.review_review);
                review_image_profile = view.findViewById(R.id.review_image);
            }
        }

        public ReviewAdapter(ArrayList<Map> reviewList){
            this.reviewList=reviewList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(parent.getContext());
            View reviewView= inflater.inflate(R.layout.review_row,parent, false);

            ViewHolder viewHolder = new ViewHolder(reviewView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position){
            Map<String, Object> review = reviewList.get(position);

            holder.review_text_name.setText(review.get("Tutee Name").toString());
            holder.review_text_rating.setText(review.get("Rating").toString());
            holder.review_text_review.setText(review.get("Review").toString());
            holder.review_text_date.setText(review.get("Date").toString());
            String imgUri = review.get("Tutee Profile Picture").toString();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.review_image_profile);
        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }
    }

}