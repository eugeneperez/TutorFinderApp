package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
    private String tutorUid;
    private String tuteeUid;
    private User user;

    private TextView text_name;
    private TextView text_email;
    private TextView text_categories;
    private TextView text_contact;
    private TextView text_fee;
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
                                        Log.d("TAG1", document.getId() + " => " + document.getData());
                                        Map<String, Object> result = new HashMap<>();
                                        tutor = document.getData();
                                        Gson gson = new Gson();
                                        Session session = new Session(tutor.get("Email").toString(), currentUser.getEmail(), "Request");
                                        ArrayList<Session> sessions = new ArrayList<>();

                                        ArrayList<String> json = new ArrayList<>();

                                        if(tutor.get("Tutee List") != null) {
                                            json = (ArrayList<String>) tutor.get("Tutee List");

                                            for (String s : json) {
                                                sessions.add(gson.fromJson(s, Session.class));
                                            }
                                        }

                                        sessions.add(session);
                                        json.clear();

                                        for (Session sesh : sessions) {
                                            json.add(gson.toJson(sesh));
                                        }

                                        tutor.put("Tutee List", json);
                                        tutorUid = document.getId();

                                        db.collection("Tutors")
                                                .document(tutorUid)
                                                .set(tutor)
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
                            }
                        });

                db.collection("Tutees")
                        .whereEqualTo("Email", currentUser.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG1", document.getId() + " => " + document.getData());
                                        Map<String, Object> result = new HashMap<>();
                                        tutee = document.getData();
                                        Gson gson = new Gson();
                                        Session session = new Session(tutee.get("Email").toString(), tutor.get("Email").toString(), "Request");

                                        ArrayList<Session> sessions = new ArrayList<>();

                                        ArrayList<String> json = new ArrayList<>();

                                        if(tutee.get("Tutor List") != null) {
                                            json = (ArrayList<String>) tutee.get("Tutor List");

                                            for (String s : json) {
                                                sessions.add(gson.fromJson(s, Session.class));
                                            }
                                        }

                                        sessions.add(session);
                                        json.clear();

                                        for (Session sesh : sessions) {
                                            json.add(gson.toJson(sesh));
                                        }

                                        tutee.put("Tutor List", json);

                                        db.collection("Tutees")
                                                .document(tuteeUid)
                                                .set(tutee)
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
                            }
                        });
            }
        });
    }

    public class ReviewAdapter extends RecyclerView.Adapter<DetailedInfo.ReviewAdapter.ViewHolder> {
        private ArrayList<User> reviewList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView text_name;
            private TextView text_rating;
            private TextView text_date;
            private TextView text_review;
            private ImageView image_profile;

            public ViewHolder(View view){
                super(view);
                text_name = view.findViewById(R.id.review_name);
                text_rating = view.findViewById(R.id.review_rating);
                text_date = view.findViewById(R.id.review_date);
                text_review = view.findViewById(R.id.review_review);
                image_profile = view.findViewById(R.id.review_image);
            }
        }

        public ReviewAdapter(ArrayList<User> reviewList){
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
        public void onBindViewHolder(@NonNull DetailedInfo.ReviewAdapter.ViewHolder holder, int position){
            User user = reviewList.get(position);
            Gson gson = new Gson();
//            String name = user.getFirstname().substring(0,1).toUpperCase() + user.getFirstname().substring(1) + " " +
//                    user.getLastname().substring(0,1).toUpperCase() + user.getLastname().substring(1);
//            String categories = new String();
//
//            for(String category:user.getCategories()){
//                if(!category.equals(user.getCategories().get(user.getCategories().size()-1)))
//                    categories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
//                else
//                    categories += category.substring(0, 1).toUpperCase()+category.substring(1);
//            }

//            holder.text_name.setText(name);
//
//            holder.text_fee.setText("₱"+user.getFee()+".00/hour");
//            holder.text_categories.setText(categories);
//            //holder.text_rating.setText(rating)
//            String imgUri=user.getProfpic();
//            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile);
//            holder.image_arrow.setImageResource(R.drawable.arrow_icon);
//            holder.image_arrow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(SearchPage.this, DetailedInfo.class);
//                    String json = gson.toJson(user);
//                    i.putExtra("User", json);
//                    startActivity(i);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }
    }

}