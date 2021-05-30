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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
*  TODO
*   search for category and specializations
*
* */


public class Homepage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;
    private EditText searchfirstname;
    private EditText searchlastname;
    private Button searchbtn;

    private TextView firstname;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> ratedTutors = new ArrayList<>();
    private ArrayList<User> popularTutors = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private HomepageAdapter ratingsAdapter;
    private HomepageAdapter popularAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        firstname = findViewById(R.id.homepage_firstname);
        spinnerAdapter = findViewById(R.id.spinner_homepage);
        search = findViewById(R.id.searchbar_homepage);
        searchfirstname = findViewById(R.id.searchfirstname_homepage);
        searchlastname = findViewById(R.id.searchlastname_homepage);
        searchbtn = findViewById(R.id.searchbtn_homepage);
        drawerLayout= findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();

        //placeholder button
        search.setVisibility(View.GONE);

        ArrayList<String> spinnerList = new ArrayList<>();

        db.collection("Tutees")
                .whereEqualTo("Email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG12", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                firstname.setText(result.get("First name").toString().substring(0, 1) + result.get("First name").toString().substring(1));
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                        ratingsAdapter.notifyDataSetChanged();
                    }
                });


        spinnerList.add("People");
        spinnerList.add("Category");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);
        Log.d("TAG1", "onCreate: entered");

        spinnerAdapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    search.setVisibility(View.GONE);
                    searchfirstname.setVisibility(View.VISIBLE);
                    searchlastname.setVisibility(View.VISIBLE);
                }
                if(id == 1){
                    search.setVisibility(View.VISIBLE);
                    searchfirstname.setVisibility(View.GONE);
                    searchlastname.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RecyclerView rv_highrated = findViewById(R.id.highly_rated_rv);
        ratingsAdapter = new HomepageAdapter(ratedTutors);
        rv_highrated.setAdapter(ratingsAdapter);
        LinearLayoutManager ratedLayoutManager = new LinearLayoutManager(
                Homepage.this, LinearLayoutManager.HORIZONTAL, false);
        rv_highrated.setLayoutManager(ratedLayoutManager);

        RecyclerView rv_popular = findViewById(R.id.popular_tutors_rv);
        popularAdapter = new HomepageAdapter(popularTutors);
        rv_popular.setAdapter(popularAdapter);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(
                Homepage.this, LinearLayoutManager.HORIZONTAL, false);
        rv_popular.setLayoutManager(popularLayoutManager);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionID, KeyEvent event){
                if(actionID == EditorInfo.IME_ACTION_DONE){
                    searchbtn.performClick();
                }
                return false;
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spin = spinnerAdapter.getSelectedItem().toString();
                String searchterms = search.getText().toString().toLowerCase();
                String searchtermf = searchfirstname.getText().toString().toLowerCase();
                String searchterml = searchlastname.getText().toString().toLowerCase();
                //ArrayList<User> results = new ArrayList<>();
                //search in database
                Log.d("TAG1", "onClick: searchterms"+searchterms);

                Intent i = new Intent(Homepage.this, SearchPage.class);
                Map<String, Object> searchTermsList = new HashMap<>();
                searchTermsList.put("First name", searchtermf);
                searchTermsList.put("Last name", searchterml);
                searchTermsList.put("Category", searchterms);

                Gson gson = new Gson();
                String json = gson.toJson(searchTermsList);
                i.putExtra("Search Terms", json);
                startActivity(i);
            }
        });

        db.collection("Tutors")
                .orderBy("Average Rating", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG124444", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                Log.d("AVERATING", "onComplete: user "+result.get("Email").toString()+ " averating "+result.get("Average Rating").toString());
                                User user = new User(result.get("Email").toString(), result.get("First name").toString(),
                                        result.get("Last name").toString(), result.get("Contact details").toString());
                                user.setAveRating(Float.parseFloat(result.get("Average Rating").toString()));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                ratedTutors.add(user);
                                ratingsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("Tutors")
                .orderBy("Total Tutees", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG123333", document.getId() + " => " + document.getData());
                                Map<String, Object> result = document.getData();
                                Log.d("TOTALTUTEES", "onComplete: user "+result.get("Email").toString()+ " total tutees "+result.get("Total Tutees").toString());
                                User user = new User(result.get("Email").toString(), result.get("First name").toString(),
                                        result.get("Last name").toString(), result.get("Contact details").toString());
                                user.setAveRating(Float.parseFloat(result.get("Average Rating").toString()));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                user.setTotalTutees(Integer.parseInt(result.get("Total Tutees").toString()));
                                popularTutors.add(user);
                                popularAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public class HomepageAdapter extends RecyclerView.Adapter<HomepageAdapter.ViewHolder> {
        private ArrayList<User> tutors = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text_name_rv;
            private TextView text_categories_rv;
            private TextView text_fee_rv;
            private TextView text_rating_rv;
            private ImageView image_profile_rv;
            private LinearLayout layout_row;

            public ViewHolder(View view) {
                super(view);
                text_name_rv = view.findViewById(R.id.homepage_row_name);
                text_categories_rv = view.findViewById(R.id.homepage_row_categories);
                text_fee_rv = view.findViewById(R.id.homepage_row_fee);
                text_rating_rv = view.findViewById(R.id.homepage_row_star);
                image_profile_rv = view.findViewById(R.id.homepage_row_image);
                layout_row = view.findViewById(R.id.homepage_row_layout);
            }
        }

        public HomepageAdapter(ArrayList<User> tutors) {
            this.tutors = tutors;
        }

        @NonNull
        @Override
        public HomepageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.homepage_row, parent, false);

            HomepageAdapter.ViewHolder viewHolder = new HomepageAdapter.ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HomepageAdapter.ViewHolder holder, int position) {
            User currentTutor = tutors.get(position);

            String fullname = currentTutor.getFirstname().substring(0, 1).toUpperCase() + currentTutor.getFirstname().substring(1) + " " +
                    currentTutor.getLastname().substring(0, 1).toUpperCase() + currentTutor.getLastname().substring(1);

            String categories = new String();

            for(String category:currentTutor.getCategories()){
                if(!category.equals(currentTutor.getCategories().get(currentTutor.getCategories().size()-1)))
                    categories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
                else
                    categories += category.substring(0, 1).toUpperCase()+category.substring(1);
            }

            holder.text_name_rv.setText(fullname);
            holder.text_categories_rv.setText(categories);
            holder.text_rating_rv.setText(Float.toString(currentTutor.getAveRating()));
            holder.text_fee_rv.setText(currentTutor.getFee());
            Picasso.get().load(currentTutor.getProfpic()).fit().centerInside().into(holder.image_profile_rv);

            holder.layout_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(Homepage.this, DetailedInfo.class);
                    String json = gson.toJson(currentTutor);
                    i.putExtra("User", json);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tutors.size();
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
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    public void ClickProfile(View view){
        Intent i = new Intent(Homepage.this, ViewTuteeProfile.class);
        startActivity(i);
        finish();
    }
    public void ClickCurrentTutors(View view){
        Intent i = new Intent(Homepage.this, ViewTuteeListOfTutors.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(Homepage.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Homepage.this, MainActivity.class);
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