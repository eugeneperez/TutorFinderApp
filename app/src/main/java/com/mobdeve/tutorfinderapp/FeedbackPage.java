package com.mobdeve.tutorfinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class FeedbackPage extends AppCompatActivity {

    private ImageView star1Iv;
    private ImageView star2Iv;
    private ImageView star3Iv;
    private ImageView star4Iv;
    private ImageView star5Iv;
    private EditText commentEt;
    private Button submitFeedbackBtn;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_page);

        star1Iv= findViewById(R.id.star1Iv);
        star2Iv= findViewById(R.id.star2Iv);
        star3Iv= findViewById(R.id.star3Iv);
        star4Iv= findViewById(R.id.star4Iv);
        star5Iv= findViewById(R.id.star5Iv);
        commentEt=findViewById(R.id.commentEt);
        submitFeedbackBtn=findViewById(R.id.submitFeedbackBtn);

        star1Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
        star2Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
        star3Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
        star4Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
        star5Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star2Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star3Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star4Iv.setImageResource(R.drawable.ic_baseline_star_24);
                star5Iv.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
    }
}