package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
*  TODO
*   search in database
*
* */


public class Homepage extends AppCompatActivity {

    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerAdapter;
    private EditText search;
    private Button searchbtn;
    private Button signoutbtn;
    private TextView username;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> results = new ArrayList<>();

    public ArrayList<String> searchFirstName(String searchterms){
        ArrayList<String> resulting = new ArrayList<>();
        db.collection("Tutors")
                .whereEqualTo("First name", searchterms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();

                                if(!(resulting.contains(result.get("Email").toString())))
                                    resulting.add(result.get("Email").toString());

                                Log.d("Result1", "onComplete: results1"+resulting);
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return resulting;
    }

    public ArrayList<String> searchLastName(ArrayList<String> existingList, String searchterms){
        db.collection("Tutors")
                .whereEqualTo("Last name", searchterms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Map<String, Object> result = new HashMap<>();
                                result = document.getData();

                                if(!(existingList.contains(result.get("Email").toString())))
                                    existingList.add(result.get("Email").toString());

                                Log.d("Result2", "onComplete: results2"+existingList.toString());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return existingList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        spinnerAdapter = findViewById(R.id.spinner_homepage);
        search = findViewById(R.id.searchbar_homepage);
        searchbtn = findViewById(R.id.searchbtn_homepage);
        signoutbtn = findViewById(R.id.signoutbtn);
        mAuth = FirebaseAuth.getInstance();

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("People");
        arrayList.add("Category");
        arrayList.add("Specialization");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);
        Log.d("TAG", "onCreate: entered");

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
                String searchterms = search.getText().toString();
                //ArrayList<User> results = new ArrayList<>();
                //search in database
                Log.d("TAG", "onClick: searchterms"+searchterms);

                if(spinnerAdapter.getSelectedItem().toString().equals("People")){
                    results = searchFirstName(searchterms);
                    results.addAll(searchLastName(results, searchterms));
                    Log.d("Results", "onClick: results"+results);
                    Intent i = new Intent(Homepage.this, SearchPage.class);
                    i.putStringArrayListExtra("results",results);
                    startActivity(i);
                }
                //start next activity
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
    }
}