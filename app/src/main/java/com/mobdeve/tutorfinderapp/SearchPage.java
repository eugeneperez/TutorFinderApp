package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchPage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView results_rv;

    private void findOfferings(ArrayList<String> emails){

        for(String email: emails){
            db.collection("Offerings")
                    .whereEqualTo("Email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG2", document.getId() + " => " + document.getData());
                                    Map<String, Object> result = new HashMap<>();
                                    result = document.getData();
                                    String category;
                                    ArrayList<String> specializations = new ArrayList<>();
                                    float fee;

                                }
                            } else {
                                Log.d("TAG2", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Gson gson = new Gson();

        spinnerAdapter = findViewById(R.id.spinner_searchpage);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("People");
        arrayList.add("Category");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);

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
//            final User user= resultList.get(position);
//            holder.toEmailTv.setText("To: " + email.getEmailTo());
//            holder.subjectEmailTv.setText("Subject: "+ email.getEmailSubject());
//            holder.bodyTv.setText(prepareTextForDisplay(email.getEmailBody()));
//            if(email.getIsDraft()==1) {
//                holder.ifDraftTv.setVisibility(View.VISIBLE);
//                holder.rowll.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent= new Intent(MainActivity.this, CreateEmailActivity.class);
//                        intent.putExtra("EmailTo", email.getEmailTo());
//                        intent.putExtra("EmailSubject", email.getEmailSubject());
//                        intent.putExtra("EmailBody", email.getEmailBody());
//                        intent.putExtra("EmailDraft", email.getIsDraft());
//                        startActivity(intent);
//                    }
//                });
//            }
            User user = resultList.get(position);
            String name = user.getFirstname() + " " + user.getLastname();
            String categories = new String();

            for(String category:user.getCategories()){
                categories += category+" ";
            }

            holder.text_name.setText(name);
            holder.text_fee.setText(Float.toString(user.getFee()));
            holder.text_categories.setText(categories);
            //holder.text_rating.setText(rating);

            holder.image_profile.setImageURI(user.getProfpic());
            holder.image_arrow.setImageResource(R.drawable.arrow_icon);
        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }
    }

}