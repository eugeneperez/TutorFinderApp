package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewTuteeProfile extends AppCompatActivity {

    private TextView text_name;
    private TextView text_email;
    private TextView text_contact;
    private ImageView image_tutee_profile;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        String user_fullname = userProfile.getFirstname().substring(0, 1).toUpperCase() + userProfile.getFirstname().substring(1)
                + " " + userProfile.getLastname().substring(0, 1).toUpperCase() + userProfile.getLastname().substring(1);

        text_name.setText(user_fullname);
        text_email.setText(userProfile.getEmail());
        text_contact.setText(userProfile.getContact());
        Picasso.get().load(image_uri).fit().centerInside().into(image_tutee_profile);

        //db.collection("")




    }

    public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {
        private ArrayList<TutorList> tutorList= new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView text_name;
            private TextView text_fee;
            private TextView text_categories;
            private TextView text_status;
            private ImageView image_profile;

            public ViewHolder(View view){
                super(view);
                text_name = view.findViewById(R.id.tutee_profile_tutor_name);
                text_categories = view.findViewById(R.id.tutee_profile_tutor_category);
                text_fee = view.findViewById(R.id.tutee_profile_tutor_fee);
                text_status = view.findViewById(R.id.tutee_profile_tutor_status);
                image_profile = view.findViewById(R.id.tutee_profile_tutor_image);
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
//            String name = user.getFirstname().substring(0,1).toUpperCase() + user.getFirstname().substring(1) + " " +
//                    user.getLastname().substring(0,1).toUpperCase() + user.getLastname().substring(1);
//            String categories = new String();
//
//            for(String category:user.getCategories()){
//                if(!category.equals(user.getCategories().get(user.getCategories().size()-1)))
//                    categories += category.substring(0, 1).toUpperCase()+category.substring(1)+", ";
//                else
//                    categories += category.substring(0, 1).toUpperCase()+category.substring(1);
//            }

//            holder.text_name.setText(name);
//
//            holder.text_fee.setText("₱"+user.getFee()+".00/hour");
//            holder.text_categories.setText(categories);
//            //holder.text_rating.setText(rating)
//            String imgUri=user.getProfpic();
//            Picasso.get().load(imgUri).fit().centerInside().into(holder.image_profile);
//            holder.image_arrow.setImageResource(R.drawable.arrow_icon);
//            holder.image_arrow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(SearchPage.this, DetailedInfo.class);
//                    String json = gson.toJson(user);
//                    i.putExtra("User", json);
//                    startActivity(i);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return tutorList.size();
        }
    }
}