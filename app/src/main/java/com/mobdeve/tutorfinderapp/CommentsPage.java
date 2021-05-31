package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CommentsPage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayList<Map> comments = new ArrayList<>();
    private float aveRating = 0;

    private DrawerLayout drawerLayout;
    private CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);
        drawerLayout= findViewById(R.id.drawer_layout);

        TextView text_aveRating = findViewById(R.id.comments_aveRating);

        //Initialize the recycler view
        RecyclerView rv_comments = findViewById(R.id.rv_comments);
        adapter = new CommentsAdapter(comments);
        rv_comments.setAdapter(adapter);
        rv_comments.setLayoutManager(new LinearLayoutManager(this));

        //Gets the reviews of the tutor
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

                                //Gets the additional information of the tutees
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

                                                        comments.add(result);

                                                    }
                                                } else {
                                                    Log.d("TAG1", "Error getting documents: ", task.getException());
                                                }

                                                float ratings = 0;
                                                int total = 0;

                                                for(Map review:comments){
                                                    total++;
                                                    ratings += Float.parseFloat(review.get("Rating").toString());
                                                }

                                                aveRating = ratings / total;

                                                text_aveRating.setText(Float.toString(aveRating));

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

    //Recycler adapter for comments page
    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
        private ArrayList<Map> commentsList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView comment_text_name;
            private TextView comment_text_rating;
            private TextView comment_text_date;
            private TextView comment_text_review;
            private ImageView comment_image_profile;

            public ViewHolder(View view) {
                super(view);
                comment_text_name = view.findViewById(R.id.result_name);
                comment_text_rating = view.findViewById(R.id.starFeedback);
                comment_text_date = view.findViewById(R.id.result_date);
                comment_text_review = view.findViewById(R.id.commentTv);
                comment_image_profile = view.findViewById(R.id.result_profilepic);
            }
        }

        public CommentsAdapter(ArrayList<Map> commentsList) {
            this.commentsList = commentsList;
        }

        @NonNull
        @Override
        public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.feedback_row, parent, false);

            CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
            Map<String, Object> comment = commentsList.get(position);

            Date date = (Date) comment.get("Date");
            Locale philippineLocale = new Locale.Builder().setLanguage("en").setRegion("PH").build();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMMM/yyyy", philippineLocale);

            String strDate = dateFormat.format(date);

            holder.comment_text_name.setText(comment.get("Tutee Name").toString());
            holder.comment_text_rating.setText(comment.get("Rating").toString());
            holder.comment_text_review.setText(comment.get("Review").toString());
            holder.comment_text_date.setText(strDate);
            String imgUri = comment.get("Tutee Profile Picture").toString();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.comment_image_profile);
        }

        @Override
        public int getItemCount() {
            return commentsList.size();
        }
    }

    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    private static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    private static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    public void ClickHome(View view){
        Intent i = new Intent(CommentsPage.this, TutorHomePage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        Intent i = new Intent(CommentsPage.this, ViewTutorProfile.class);
        startActivity(i);
        finish();
    }
    public void ClickReview(View view){
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(CommentsPage.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(CommentsPage.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}