package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

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
    private Button signoutbtn;
    private Button viewprofilebtn;
    private TextView username;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private User userProfile;
    private ArrayList<User> users = new ArrayList<>();

    public void searchFirstName2(String searchterms){
        ArrayList<String> resulting = new ArrayList<>();
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
                                boolean duplicate = false;
                                Log.d("hello2", result.toString());
                                User user = new User(result.get("Email").toString(),
                                        result.get("First name").toString(),
                                        result.get("Last name").toString(),
                                        result.get("Contact details").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());

                                for(User userTemp: users){
                                    if(userTemp.getEmail().equals(user.getEmail())){
                                        duplicate = true;
                                    }
                                }

                                if(!duplicate){
                                    users.add(user);
                                    Log.d("Result1", "onComplete: USER"+user.getEmail());
                                }
                                Log.d("Result1", "onComplete: results1"+users);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void searchLastName2(String searchterms){
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
                                boolean duplicate = false;
                                Log.d("hello2", result.toString());
                                User user = new User(result.get("Email").toString(),
                                        result.get("First name").toString(),
                                        result.get("Last name").toString(),
                                        result.get("Contact details").toString());
                                user.setCategories((ArrayList<String>) result.get("Categories"));
                                user.setFee(result.get("Fee").toString());
                                user.setProfpic(result.get("Profile Picture").toString());

                                for(User userTemp: users){
                                    if(userTemp.getEmail().equals(user.getEmail())){
                                        duplicate = true;
                                    }
                                }

                                if(!duplicate){
                                    users.add(user);
                                    Log.d("Result2", "onComplete: USER"+user.getEmail());
                                    Log.d("Result2", "onComplete: ENTERED");
                                }
                                Log.d("Result2", "onComplete: results2"+users);
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void searchCategory(String searchterms){
        Log.d("searchdb", "searchCategory: searchterms "+searchterms);
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
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        spinnerAdapter = findViewById(R.id.spinner_homepage);
        search = findViewById(R.id.searchbar_homepage);
        searchfirstname = findViewById(R.id.searchfirstname_homepage);
        searchlastname = findViewById(R.id.searchlastname_homepage);
        searchbtn = findViewById(R.id.searchbtn_homepage);
        signoutbtn = findViewById(R.id.signoutbtn);
        mAuth = FirebaseAuth.getInstance();



        //placeholder button
        viewprofilebtn = findViewById(R.id.viewprofilebtn);

        search.setVisibility(View.GONE);

        ArrayList<String> spinnerList = new ArrayList<>();

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

                CountDownTimer count = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d("TICK", "onTick: users"+users);
                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(Homepage.this, SearchPage.class);
                        ArrayList<String> userString = new ArrayList<>();

                        Gson gson = new Gson();

                        for (User user: users){
                            userString.add(gson.toJson(user));
                        }
                        users.clear();
                        intent.putStringArrayListExtra("Results",userString);
                        startActivity(intent);
                    }
                };

                if(spinnerAdapter.getSelectedItem().toString().equals("People")){
                    if(!searchtermf.isEmpty()){
                        searchFirstName2(searchtermf);
                    }
                    if(!searchterml.isEmpty()){
                        searchLastName2(searchterml);
                    }
                    if(!searchtermf.isEmpty() || !searchterml.isEmpty()){
                        count.start();
                    }

                }
                else if(spinnerAdapter.getSelectedItem().toString().equals("Category")){
                    searchCategory(searchterms);
                    count.start();
                }
            }
        });

        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Homepage.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        viewprofilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query query = db.collection("Tutees").whereEqualTo("Email", currentUser.getEmail());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document: task.getResult()){
                            Map<String, Object> result = document.getData();
                            Gson gson = new Gson();

                            ArrayList<String> json = new ArrayList<>();
                            ArrayList<Session> sessions = new ArrayList<>();

                            userProfile = new User(result.get("Email").toString(), result.get("First name").toString(),
                                    result.get("Last name").toString(), result.get("Contact details").toString());
                            userProfile.setProfpic(result.get("Profile Picture").toString());

                            if(result.get("Tutor List") != null) {
                                json = (ArrayList<String>) result.get("Tutor List");

                                for (String s : json) {
                                    sessions.add(gson.fromJson(s, Session.class));
                                }
                            }

                            json.clear();

                            for (Session sesh : sessions) {
                                json.add(gson.toJson(sesh));
                            }


                            Intent i = new Intent(Homepage.this, ViewTuteeProfile.class);
                            i.putExtra("User Profile", json);
                            startActivity(i);
                        }
                    }
                });
            }
        });
    }
}