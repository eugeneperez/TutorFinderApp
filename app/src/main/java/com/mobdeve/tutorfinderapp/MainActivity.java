package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView loading_message;
    private Button registerBtn;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<TuteeList> curTutees = new ArrayList<>();
    private ArrayList<TuteeList> reqTutees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loading_message = findViewById(R.id.loading_message);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);

        AlertDialog dialog = builder.create();
        dialog.setMessage("Checking if you are logged in...");

        CountDownTimer count = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        };

        dialog.show();
        if(currentUser != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            db.collection("Tutors")
                    .whereEqualTo("Email", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG1", document.getId() + " => " + document.getData());
                                    Gson gson = new Gson();
                                    Map<String, Object> result = new HashMap<>();
                                    result = document.getData();

                                    if (user.getEmail().equals(result.get("Email"))) {
                                        Intent i = new Intent(MainActivity.this, TutorHomePage.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                                dialog.dismiss();
                            } else {
                                Log.d("TAG1", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            db.collection("Tutees")
                    .whereEqualTo("Email", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG1", document.getId() + " => " + document.getData());
                                    Map<String, Object> result = new HashMap<>();
                                    result = document.getData();
                                    if (user.getEmail().equals(result.get("Email"))) {
                                        Intent i = new Intent(MainActivity.this, Homepage.class);
                                        i.putExtra("First name", result.get("First name").toString());
                                        startActivity(i);
                                        finish();
                                    }
                                }
                                dialog.dismiss();
                            } else {
                                Log.d("TAG1", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        count.start();

        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.activity_register_role, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation( v, Gravity.CENTER, 0, 0);
                popupWindow.setElevation(40);
                Button tuteeBtn= (Button)popupView.findViewById(R.id.tuteeBtn);
                Button tutorBtn= (Button)popupView.findViewById(R.id.tutorBtn);
                tutorBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        Intent tutorInt = new Intent(MainActivity.this, Register.class);
                        tutorInt.putExtra("Type", "tutor");
                        startActivity(tutorInt);
                    }
                });
                tuteeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        Intent tuteeInt = new Intent(MainActivity.this, Register.class);
                        tuteeInt.putExtra("Type", "tutee");
                        startActivity(tuteeInt);
                    }
                });
            }
        });
    }
}