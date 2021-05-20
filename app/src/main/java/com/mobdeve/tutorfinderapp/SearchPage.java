package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchPage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;
    private EditText searchfirstname;
    private EditText searchlastname;
    private Button searchbtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView results_rv;


    public void searchFirstName2(String searchterms){
        Log.d("searchdb", "searchFirstName2: searchterms "+searchterms);
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
                                Log.d("hello2", result.toString());
                                if(!(users.contains(result.get("Email").toString()))){
                                    User user = new User(result.get("Email").toString(),
                                            result.get("First name").toString(),
                                            result.get("Last name").toString(),
                                            result.get("Contact details").toString(),
                                            (ArrayList<String>) result.get("Categories"),
                                            result.get("Fee").toString());
                                    if(!result.get("Profile Picture").equals(null)){
                                        user.setProfpic(result.get("Profile Picture").toString());
                                    }
                                    users.add(user);
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
        Log.d("searchdb", "searchLastName2: searchterms "+searchterms);
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
                                Log.d("hello2", result.toString());
                                if(!(users.contains(result.get("Email").toString()))){
                                    User user = new User(result.get("Email").toString(),
                                            result.get("First name").toString(),
                                            result.get("Last name").toString(),
                                            result.get("Contact details").toString(),
                                            (ArrayList<String>) result.get("Categories"),
                                            result.get("Fee").toString());
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
                                            result.get("Contact details").toString(),
                                            (ArrayList<String>) result.get("Categories"),
                                            result.get("Fee").toString());
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
        setContentView(R.layout.activity_search_page);
        Gson gson = new Gson();

        search = findViewById(R.id.searchbar_searchpage);
        searchfirstname = findViewById(R.id.searchfirstname_searchpage);
        searchlastname = findViewById(R.id.searchlastname_searchpage);
        searchbtn = findViewById(R.id.searchbtn_searchpage);
        spinnerAdapter = findViewById(R.id.spinner_searchpage);

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

        Intent i= getIntent();
        ArrayList<String> results = i.getStringArrayListExtra("Results");
        Log.d("TAG2", "onCreate: emails"+results);

        for(String user: results){
            users.add(gson.fromJson(user, User.class));
        }

        results_rv = findViewById(R.id.search_page_rv);
        ResultsAdapter adapter = new ResultsAdapter(users);
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
                String spin = spinnerAdapter.getSelectedItem().toString();
                String searchterms = search.getText().toString();
                String searchtermf = searchfirstname.getText().toString().toLowerCase();
                String searchterml = searchlastname.getText().toString().toLowerCase();
                //ArrayList<User> results = new ArrayList<>();
                //search in database
                Log.d("TAG1", "onClick: searchterms"+searchterms);

                CountDownTimer count = new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d("TICK", "onTick: users"+users);
                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(SearchPage.this, SearchPage.class);
                        ArrayList<String> userString = new ArrayList<>();

                        Gson gson = new Gson();

                        for (User user: users){
                            userString.add(gson.toJson(user));
                        }
                        intent.putStringArrayListExtra("Results",userString);
                        startActivity(intent);
                        finish();
                    }
                };

                if(spinnerAdapter.getSelectedItem().toString().equals("People")){
                    users.clear();
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
                    users.clear();
                    searchCategory(searchterms);
                    count.start();
                }
            }
        });
    }

    public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
        private ArrayList<User> resultList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView text_name;
            private TextView text_fee;
            private TextView text_categories;
            private TextView text_rating;
            private ImageView image_arrow;
            private ImageView image_profile;

            public ViewHolder(View view){
                super(view);
                text_name = view.findViewById(R.id.result_name);
                text_categories = view.findViewById(R.id.result_categories);
                text_fee = view.findViewById(R.id.result_fee);
                text_rating = view.findViewById(R.id.result_rating);
                image_arrow = view.findViewById(R.id.result_arrow);
                image_profile = view.findViewById(R.id.result_profilepic);
            }
        }

        public ResultsAdapter(ArrayList<User> resultList){ this.resultList=resultList;}

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(parent.getContext());
            Context context= parent.getContext();
            View resultView= inflater.inflate(R.layout.result_row,parent, false);

            ViewHolder viewHolder= new ViewHolder(resultView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position){
            User user = resultList.get(position);
            Gson gson = new Gson();
            String name = user.getFirstname().substring(0).toUpperCase() + " " + user.getLastname().substring(0).toUpperCase();
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
            //holder.text_rating.setText(rating)
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

}