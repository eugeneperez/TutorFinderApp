package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class ViewTuteeListOfTutors extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<TutorList> tutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutee_list_of_tutors);
        drawerLayout= findViewById(R.id.drawer_layout);

        db.collection("Tutees")
                .whereEqualTo("Email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG12", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();

                                ArrayList<Map> list = (ArrayList<Map>) result.get("Tutor List");
                                Map<String, Object> item = new HashMap<>();

                                String firstname = result.get("First name").toString().substring(0, 1).toUpperCase()+
                                        result.get("First name").toString().substring(1);
                                String lastname = result.get("Last name").toString().substring(0, 1).toUpperCase()+
                                        result.get("Last name").toString().substring(1);
                                String fullname = firstname+" "+lastname;

                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {
        private ArrayList<TutorList> tutorList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text_name_rv;
            private TextView text_categories_rv;
            private TextView text_fee_rv;
            private TextView text_contact_rv;
            private ImageView image_profile_rv;
            private LinearLayout lil_butt;
            private LinearLayout tutees;

            public ViewHolder(View view) {
                super(view);
                text_name_rv = view.findViewById(R.id.current_tutor_name);
                text_categories_rv = view.findViewById(R.id.current_tutor_category);
                text_fee_rv = view.findViewById(R.id.current_tutor_fee);
                text_contact_rv = view.findViewById(R.id.current_tutor_contact);
                image_profile_rv = view.findViewById(R.id.current_tutor_image);
                lil_butt = view.findViewById(R.id.tutee_end_session_btn);
                tutees = view.findViewById(R.id.tutees);
            }
        }

        public TutorListAdapter(ArrayList<TutorList> tutorList) {
            this.tutorList = tutorList;
        }

        @NonNull
        @Override
        public TutorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.rv_tutor_fragment, parent, false);

            TutorListAdapter.ViewHolder viewHolder = new TutorListAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TutorListAdapter.ViewHolder holder, int position) {
            TutorList currentUser = tutorList.get(position);

            holder.tutees.setVisibility(View.GONE);

            ArrayList<String> categories = currentUser.getCategories();
            String strCategories = new String();

            for(String category:categories){
                if(!category.equals(categories.size()-1))
                    strCategories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
                else
                    strCategories += category.substring(0, 1).toUpperCase()+category.substring(1);
            }

            holder.text_name_rv.setText(currentUser.getFullname());
            holder.text_contact_rv.setText(currentUser.getContact());
            holder.text_fee_rv.setText(currentUser.getFee());
            holder.text_categories_rv.setText(strCategories);
            String imgUri = currentUser.getImage_uri();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile_rv);

            holder.lil_butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(ViewTuteeListOfTutors.this, "removing... not really", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return tutorList.size();
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
        Intent i = new Intent(ViewTuteeListOfTutors.this, Homepage.class);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        Intent i = new Intent(ViewTuteeListOfTutors.this, ViewTuteeProfile.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(ViewTuteeListOfTutors.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ViewTuteeListOfTutors.this, MainActivity.class);
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
    public void ClickTutors(View view){
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}