package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    private TextView username;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<User> users = new ArrayList<>();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
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