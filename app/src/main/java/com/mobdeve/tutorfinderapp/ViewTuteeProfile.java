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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ViewTuteeProfile extends AppCompatActivity {

    private TextView text_name;
    private TextView text_email;
    private TextView text_contact;
    private ImageView image_tutee_profile;
    private RecyclerView tutor_list_rv;
    private DrawerLayout drawerLayout;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutee_profile);

        text_name = findViewById(R.id.tutee_profile_name);
        text_email = findViewById(R.id.tutee_profile_email);
        text_contact = findViewById(R.id.tutee_profile_contact);
        image_tutee_profile = findViewById(R.id.tutee_profile_image);
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
                                String firstname = result.get("First name").toString().substring(0, 1).toUpperCase()+
                                        result.get("First name").toString().substring(1);
                                String lastname = result.get("Last name").toString().substring(0, 1).toUpperCase()+
                                        result.get("Last name").toString().substring(1);
                                String fullname = firstname+" "+lastname;

                                text_name.setText(fullname);
                                text_email.setText(result.get("Email").toString());
                                text_contact.setText(result.get("Contact details").toString());
                                String imgUri = result.get("Profile Picture").toString();
                                Picasso.get().load(imgUri).fit().centerInside().into(image_tutee_profile);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {
        private ArrayList<TutorList> tutorList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView text_name_rv;
            private TextView text_fee_rv;
            private TextView text_categories_rv;
            private TextView text_status_rv;
            private TextView text_contact_rv;
            private ImageView image_profile_rv;

            public ViewHolder(View view){
                super(view);
                text_name_rv = view.findViewById(R.id.tutee_profile_tutor_name);
                text_categories_rv = view.findViewById(R.id.tutee_profile_tutor_category);
                text_fee_rv = view.findViewById(R.id.tutee_profile_tutor_fee);
                text_status_rv = view.findViewById(R.id.tutee_profile_tutor_status);
                text_contact_rv = view.findViewById(R.id.tutee_profile_tutor_contact);
                image_profile_rv = view.findViewById(R.id.tutee_profile_tutor_image);
            }
        }

        public TutorListAdapter(ArrayList<TutorList> tutorList){ this.tutorList=tutorList;}

        @NonNull
        @Override
        public TutorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(parent.getContext());
            View resultView= inflater.inflate(R.layout.tutor_list, parent, false);

            TutorListAdapter.ViewHolder viewHolder= new TutorListAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TutorListAdapter.ViewHolder holder, int position){
            TutorList currentUser = tutorList.get(position);
            Gson gson = new Gson();
            ArrayList<String> categoriesArray = currentUser.getCategories();
            String categories = new String();

            for(String category: categoriesArray){
                if(!category.equals(categoriesArray.size()-1))
                    categories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
                else
                    categories += category.substring(0, 1).toUpperCase()+category.substring(1);
            }

            holder.text_name_rv.setText(currentUser.getFullname());
            holder.text_categories_rv.setText(categories);
            holder.text_fee_rv.setText(currentUser.getFee());
            holder.text_status_rv.setText(currentUser.getStatus());
            holder.text_contact_rv.setText(currentUser.getContact());
            String imgUri=currentUser.getImage_uri();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile_rv);
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
        Intent i = new Intent(ViewTuteeProfile.this, Homepage.class);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    public void ClickCurrentTutors(View view){
        Intent i = new Intent(ViewTuteeProfile.this, ViewTuteeListOfTutors.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(ViewTuteeProfile.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ViewTuteeProfile.this, MainActivity.class);
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