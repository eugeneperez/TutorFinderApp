package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewTuteeProfile extends AppCompatActivity {

    private TextView text_name;
    private TextView text_email;
    private TextView text_contact;
    private ImageView image_tutee_profile;
    private RecyclerView tutor_list_rv;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<TutorList> tutors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutee_profile);
        Gson gson = new Gson();

        text_name = findViewById(R.id.tutee_profile_name);
        text_email = findViewById(R.id.tutee_profile_email);
        text_contact = findViewById(R.id.tutee_profile_contact);
        image_tutee_profile = findViewById(R.id.tutee_profile_image);

        Intent i = getIntent();
        String json = i.getStringExtra("User Profile");
        User userProfile = gson.fromJson(json, User.class);
        String image_uri = userProfile.getProfpic();
        tutors = userProfile.getTutorList();

        String user_fullname = userProfile.getFirstname().substring(0, 1).toUpperCase() + userProfile.getFirstname().substring(1)
                + " " + userProfile.getLastname().substring(0, 1).toUpperCase() + userProfile.getLastname().substring(1);

        text_name.setText(user_fullname);
        text_email.setText(userProfile.getEmail());
        text_contact.setText(userProfile.getContact());
        Picasso.get().load(image_uri).fit().centerInside().into(image_tutee_profile);

        tutor_list_rv = findViewById(R.id.tutee_profile_rv);
        TutorListAdapter tutorListAdapter = new TutorListAdapter(tutors);
        tutor_list_rv.setAdapter(tutorListAdapter);
        tutor_list_rv.setLayoutManager(new LinearLayoutManager(this));
        Log.d("TUTORS", "onCreate: user "+userProfile.getEmail());
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
}