package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class SeeMoreReviews extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private float aveRating = 0;
    private ArrayList<Map> reviews = new ArrayList<>();

    private TextView text_average_rating;
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more_reviews);

        text_average_rating = findViewById(R.id.see_reviews_ratings);

        Intent i = getIntent();
        Gson gson = new Gson();
        String json = i.getStringExtra("User");
        User user = gson.fromJson(json, User.class);

        RecyclerView rv_more_reviews = findViewById(R.id.see_reviews_rv);
        adapter = new ReviewAdapter(reviews);
        rv_more_reviews.setAdapter(adapter);
        rv_more_reviews.setLayoutManager(new LinearLayoutManager(SeeMoreReviews.this));

        db.collection("Reviews")
                .whereEqualTo("Tutor", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> result = document.getData();

                                Timestamp timestamp = (Timestamp) result.get("Date");
                                result.put("Date", timestamp.toDate());

                                db.collection("Tutees")
                                        .whereEqualTo("Email", result.get("Tutee").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> tutee = document.getData();

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

    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
        private ArrayList<Map> reviewList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView review_text_name;
            private TextView review_text_rating;
            private TextView review_text_date;
            private TextView review_text_review;
            private ImageView review_image_profile;

            public ViewHolder(View view){
                super(view);
                review_text_name = view.findViewById(R.id.result_name);
                review_text_rating = view.findViewById(R.id.starFeedback);
                review_text_date = view.findViewById(R.id.result_date);
                review_text_review = view.findViewById(R.id.commentTv);
                review_image_profile = view.findViewById(R.id.result_profilepic);
            }
        }

        public ReviewAdapter(ArrayList<Map> reviewList){
            this.reviewList=reviewList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(parent.getContext());
            View reviewView= inflater.inflate(R.layout.feedback_row,parent, false);

            ViewHolder viewHolder = new ViewHolder(reviewView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position){
            Map<String, Object> review = reviewList.get(position);

            Date date = (Date) review.get("Date");
            Locale philippineLocale = new Locale.Builder().setLanguage("en").setRegion("PH").build();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMMM/yyyy", philippineLocale);

            String strDate = dateFormat.format(date);

            holder.review_text_name.setText(review.get("Tutee Name").toString());
            holder.review_text_rating.setText(review.get("Rating").toString());
            holder.review_text_review.setText(review.get("Review").toString());
            holder.review_text_date.setText(strDate);
            String imgUri = review.get("Tutee Profile Picture").toString();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.review_image_profile);
        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }
    }
}