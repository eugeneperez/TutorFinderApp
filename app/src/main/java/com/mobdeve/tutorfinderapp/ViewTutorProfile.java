package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewTutorProfile extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private User currentUser;

    private TextView text_name;
    private TextView text_email;
    private TextView text_contact;
    private TextView text_categories;
    private TextView text_fee;
    private ImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutor_profile);
        drawerLayout= findViewById(R.id.drawer_layout);

        text_name =  findViewById(R.id.tutor_profile_name);
        text_email = findViewById(R.id.tutor_profile_email);
        text_contact = findViewById(R.id.tutor_profile_contact);
        text_categories = findViewById(R.id.tutor_profile_category);
        text_fee = findViewById(R.id.tutor_profile_fee);
        image_profile = findViewById(R.id.tutor_profile_image);

        db.collection("Tutors")
                .whereEqualTo("Email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG12", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                String firstname = result.get("First name").toString().substring(0, 1).toUpperCase()+
                                        result.get("First name").toString().substring(1);
                                String lastname = result.get("Last name").toString().substring(0, 1).toUpperCase()+
                                        result.get("Last name").toString().substring(1);
                                String fullname = firstname+" "+lastname;
                                ArrayList<String> categories = (ArrayList<String>) result.get("Categories");
                                String strCategories = new String();

                                for(String category: categories){
                                    if(!category.equals(categories.size()-1))
                                        strCategories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
                                    else
                                        strCategories += category.substring(0, 1).toUpperCase()+category.substring(1);
                                }

                                text_name.setText(fullname);
                                text_email.setText(result.get("Email").toString());
                                text_contact.setText(result.get("Contact details").toString());
                                text_categories.setText(strCategories);
                                text_fee.setText(result.get("Fee").toString());
                                String imgUri = result.get("Profile Picture").toString();
                                Log.d("IMAGEURI", "onComplete: profile pic "+imgUri);
                                Picasso.get().load(imgUri).fit().centerInside().into(image_profile);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });

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
        Intent i = new Intent(ViewTutorProfile.this, TutorHomePage.class);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    public void ClickReview(View view){
        Intent i = new Intent(ViewTutorProfile.this, CommentsPage.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(ViewTutorProfile.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ViewTutorProfile.this, MainActivity.class);
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