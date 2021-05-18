package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {

    private Spinner spinnerAdapter;
    private EditText search;
    private ArrayList<Offering> offerings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        spinnerAdapter = findViewById(R.id.spinner_searchpage);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("People");
        arrayList.add("Category");
        arrayList.add("Specialization");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(arrayAdapter);

        Intent i= getIntent();
        ArrayList<String> emails= i.getStringArrayListExtra("results");
        // get offerings data

    }

    public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
        private ArrayList<Offering> resultList= new ArrayList<>();

        public ResultsAdapter(ArrayList<Offering> resultList){ this.resultList=resultList;}

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
        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
//            public TextView ifDraftTv;
//            public TextView toEmailTv;
//            public TextView subjectEmailTv;
//            public TextView bodyTv;
//            public LinearLayout rowll;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
//                this.ifDraftTv = (TextView) itemView.findViewById(R.id.ifDraftTv);
//                this.toEmailTv = (TextView) itemView.findViewById(R.id.toEmailTv);
//                this.subjectEmailTv = (TextView) itemView.findViewById(R.id.subjectEmailTv);
//                this.bodyTv = (TextView) itemView.findViewById(R.id.bodyTv);
//                this.rowll = (LinearLayout) itemView.findViewById(R.id.rowll);
//                this.ifDraftTv= (TextView) itemView.findViewById(R.id.ifDraftTv);
            }

        }
    }

}