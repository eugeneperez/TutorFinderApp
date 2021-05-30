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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchPage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;
    private EditText searchfirstname;
    private EditText searchlastname;
    private ImageView searchbtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView results_rv;
    private ResultsAdapter adapter;
    private DrawerLayout drawerLayout;
    private TextView message;
    private String strSearchFirstname = new String();
    private String strSearchLastname = new String();
    private String strSearchCategory = new String();
    private User user;
    private float averageRating = 0;
    private float totalRating = 0;
    private int count = 0;

    public void searchFirstName(String searchterms){
        db.collection("Tutors")
                .orderBy("First name")
                .startAt(searchterms)
                .endAt(searchterms+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.getId() + " => " + document.getData());
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();
                                boolean isNotFound = true;

                                User user = new User(result.get("Email").toString(),
                                        result.get("First name").toString(),
                                        result.get("Last name").toString(),
                                        result.get("Contact details").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());

                                for(User u: users){
                                    if(u.getEmail().equals(user.getEmail())){

                                        isNotFound = false;
                                    }
                                }

                                if(isNotFound){
                                    Log.d("ENTERED IF", "onComplete: entered users"+users);
                                    Log.d("Entered email", "onComplete: result email "+ result.get("Email").toString());
                                    users.add(user);
                                }
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                        adapter.notifyDataSetChanged();
                        if(users.isEmpty()){
                            message.setVisibility(View.VISIBLE);
                        }else{
                            message.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public void searchLastName(String searchterms){
        db.collection("Tutors")
                .orderBy("Last name")
                .startAt(searchterms)
                .endAt(searchterms+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.getId() + " => " + document.getData());
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();
                                boolean isFound = true;
                                Log.d("hello2", result.toString());

                                user = new User(result.get("Email").toString(),
                                        result.get("First name").toString(),
                                        result.get("Last name").toString(),
                                        result.get("Contact details").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());

                                for(User u: users){
                                    if(u.getEmail().equals(user.getEmail())){
                                        isFound = false;
                                    }
                                }

                                if(isFound){
                                    Log.d("ENTERED IF", "onComplete: entered users"+users);
                                    Log.d("Entered email", "onComplete: result email "+ result.get("Email").toString());
                                    users.add(user);
                                }

                                Log.d("Result2", "onComplete: results2"+users);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                        adapter.notifyDataSetChanged();
                        if(users.isEmpty()){
                            message.setVisibility(View.VISIBLE);
                        }else{
                            message.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void searchCategory(String searchterms){
        db.collection("Tutors")
                .whereArrayContains("Categories", searchterms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.getId() + " => " + document.getData());
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();

                                Log.d("hello2", result.toString());
                                if(!(users.contains(result.get("Email").toString()))){
                                    User user = new User(result.get("Email").toString(),
                                            result.get("First name").toString(),
                                            result.get("Last name").toString(),
                                            result.get("Contact details").toString());
                                    user.setCategories((ArrayList<String>) result.get("Categories"));
                                    user.setFee(result.get("Fee").toString());
                                    user.setProfpic(result.get("Profile Picture").toString());
                                    users.add(user);
                                }

                                Log.d("Result2", "onComplete: results2"+users);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                        adapter.notifyDataSetChanged();
                        if(users.isEmpty()){
                            message.setVisibility(View.VISIBLE);
                        }else{
                            message.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void searchTerms(Map searchTermsList){
        if(!searchTermsList.get("First name").toString().isEmpty()){
            strSearchFirstname = searchTermsList.get("First name").toString();
            searchFirstName(strSearchFirstname);
        }else{
            message.setVisibility(View.VISIBLE);
        }
        if(!searchTermsList.get("Last name").toString().isEmpty()){
            strSearchLastname = searchTermsList.get("Last name").toString();
            searchLastName(strSearchLastname);
        }else{
            message.setVisibility(View.VISIBLE);
        }
        if(!searchTermsList.get("Category").toString().isEmpty()){
            strSearchCategory = searchTermsList.get("Category").toString();
            searchCategory(strSearchCategory);
        }else{
            message.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Gson gson = new Gson();

        search = findViewById(R.id.searchbar_searchpage);
        searchfirstname = findViewById(R.id.searchfirstname_searchpage);
        searchlastname = findViewById(R.id.searchlastname_searchpage);
        searchbtn = findViewById(R.id.searchbtn_searchpage);
        spinnerAdapter = findViewById(R.id.spinner_searchpage);
        drawerLayout= findViewById(R.id.drawer_layout);
        message= findViewById(R.id.noResultMessageTv);


        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("People");
        arrayList.add("Category");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);

        spinnerAdapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0){
                    search.setVisibility(View.GONE);
                    search.setText("");
                    searchfirstname.setVisibility(View.VISIBLE);
                    searchlastname.setVisibility(View.VISIBLE);
                }
                if(id == 1){
                    search.setVisibility(View.VISIBLE);
                    searchfirstname.setVisibility(View.GONE);
                    searchlastname.setVisibility(View.GONE);
                    searchfirstname.setText("");
                    searchlastname.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent i= getIntent();
        Map<String, Object> searchTerms = gson.fromJson(i.getStringExtra("Search Terms"), Map.class);
        Log.d("SEARCHINTENT", "onCreate: searchterms "+searchTerms);
        searchTerms(searchTerms);

        results_rv = findViewById(R.id.search_page_rv);
        adapter = new ResultsAdapter(users);
        results_rv.setAdapter(adapter);
        results_rv.setLayoutManager(new LinearLayoutManager(this));
        // get offerings data
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

                Log.d("SEARCHUSERS", "onClick: users "+users);
                users.clear();
                adapter.notifyDataSetChanged();
                Log.d("SEARCHUSERS", "onClick: users "+users);

                strSearchCategory = search.getText().toString().toLowerCase();
                strSearchFirstname = searchfirstname.getText().toString().toLowerCase();
                strSearchLastname = searchlastname.getText().toString().toLowerCase();

                    Map<String, Object> searchTermsList = new HashMap<>();
                    searchTermsList.put("First name", strSearchFirstname);
                    searchTermsList.put("Last name", strSearchLastname);
                    searchTermsList.put("Category", strSearchCategory);


                    searchTerms(searchTermsList);

            }
        });
    }

    public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
        private ArrayList<User> resultList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView text_name;
            private TextView text_fee;
            private TextView text_categories;
            private ImageView image_arrow;
            private ImageView image_profile;

            public ViewHolder(View view){
                super(view);
                text_name = view.findViewById(R.id.result_name);
                text_categories = view.findViewById(R.id.result_categories);
                text_fee = view.findViewById(R.id.result_fee);
                image_arrow = view.findViewById(R.id.result_arrow);
                image_profile = view.findViewById(R.id.result_profilepic);
            }
        }

        public ResultsAdapter(ArrayList<User> resultList){ this.resultList=resultList;}

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(parent.getContext());
            View resultView= inflater.inflate(R.layout.result_row,parent, false);

            ViewHolder viewHolder= new ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position){
            User user = resultList.get(position);
            Gson gson = new Gson();
            String name = user.getFirstname().substring(0,1).toUpperCase() + user.getFirstname().substring(1) + " " +
                    user.getLastname().substring(0,1).toUpperCase() + user.getLastname().substring(1);
            String categories = new String();

            for(String category:user.getCategories()){
                if(!category.equals(user.getCategories().get(user.getCategories().size()-1)))
                categories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
                else
                categories += category.substring(0, 1).toUpperCase()+category.substring(1);
            }

            holder.text_name.setText(name);
            holder.text_fee.setText("₱"+user.getFee()+".00/hour");
            holder.text_categories.setText(categories);
            String imgUri=user.getProfpic();
            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile);
            holder.image_arrow.setImageResource(R.drawable.arrow_icon);
            holder.image_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SearchPage.this, DetailedInfo.class);
                    String json = gson.toJson(user);
                    i.putExtra("User", json);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return resultList.size();
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
        Intent i = new Intent(SearchPage.this, Homepage.class);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        Intent i = new Intent(SearchPage.this, ViewTuteeProfile.class);
        startActivity(i);
        finish();

    }
    public void ClickCurrentTutors(View view){
        Intent i = new Intent(SearchPage.this, ViewTuteeListOfTutors.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(SearchPage.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(SearchPage.this, MainActivity.class);
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