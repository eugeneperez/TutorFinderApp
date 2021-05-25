package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorHomePage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private TabItem currTutees;
    private TabItem reqTutees;
    private ViewPager viewPager;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<TuteeList> curTuteesList = new ArrayList<>();
    private ArrayList<TuteeList> reqTuteesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);
        Gson gson = new Gson();

        drawerLayout= findViewById(R.id.drawer_layout);
        tabLayout= findViewById(R.id.tabLayout);
        currTutees= findViewById(R.id.currTutees);
        reqTutees= findViewById(R.id.reqTutees);
        viewPager= findViewById(R.id.viewPager);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);

        AlertDialog dialog = builder.create();
        dialog.setMessage("Loading Tutee List...");



        PagerAdapter pagerAdapter= new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());

        refreshList();
        
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public ArrayList<TuteeList> getCurTuteesList(){
        return curTuteesList;
    }

    public ArrayList<TuteeList> getReqTuteesList(){
        return reqTuteesList;
    }

    public void refreshList(){
        curTuteesList.clear();
        reqTuteesList.clear();

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

                                if(result.get("Tutee List") != null){
                                    Log.d("ABTESTING", "entered if Tutee List exists");
                                    for(Map m: (ArrayList<Map>) result.get("Tutee List")){
                                        //Session session = gson.fromJson(s, Session.class);
                                        if(m.get("Status").toString().equals("Request")){
                                            db.collection("Tutees")
                                                    .whereEqualTo("Email", m.get("Partner").toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                                    Map<String, Object> result = document.getData();
                                                                    Log.d("0TuteesList", "onComplete: partner "+m.get("Partner").toString());

                                                                    String firstname = result.get("First name").toString();
                                                                    String lastname = result.get("Last name").toString();
                                                                    String fullname = firstname.substring(0, 1).toUpperCase()+firstname.substring(1) + " " +
                                                                            lastname.substring(0, 1).toUpperCase() + lastname.substring(1);

                                                                    TuteeList tutee = new TuteeList(result.get("Email").toString(), fullname, result.get("Contact details").toString(),
                                                                            result.get("Profile Picture").toString(), m.get("Status").toString());
                                                                    int index = -1;

                                                                    for(TuteeList t:reqTuteesList){
                                                                        if(t.getEmail().equals(tutee.getEmail())){
                                                                            index = reqTuteesList.indexOf(t);
                                                                        }
                                                                    }

                                                                    if(index != -1){
                                                                        reqTuteesList.remove(reqTuteesList.get(index));
                                                                    }

                                                                    reqTuteesList.add(tutee);
                                                                    Log.d("0TuteesList", "onComplete: reqtutees "+reqTuteesList);
                                                                }

                                                            } else {
                                                                Log.d("TAG1", "Error getting documents: ", task.getException());
                                                            }
                                                        }
                                                    });
                                        }else if (m.get("Status").toString().equals("Current")){
                                            db.collection("Tutees")
                                                    .whereEqualTo("Email", m.get("Partner").toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for(QueryDocumentSnapshot document: task.getResult()){
                                                                    Map<String, Object> result = document.getData();
                                                                    Log.d("0TuteesList", "onComplete: partner "+m.get("Partner").toString());

                                                                    String firstname = result.get("First name").toString();
                                                                    String lastname = result.get("Last name").toString();
                                                                    String fullname = firstname.substring(0, 1).toUpperCase()+firstname.substring(1) + " " +
                                                                            lastname.substring(0, 1).toUpperCase() + lastname.substring(1);

                                                                    TuteeList tutee = new TuteeList(result.get("Email").toString(), fullname, result.get("Contact details").toString(),
                                                                            result.get("Profile Picture").toString(), m.get("Status").toString());
                                                                    curTuteesList.add(tutee);
                                                                    Log.d("0TuteesList", "onComplete: curtutees "+curTuteesList);
                                                                }

                                                            } else {
                                                                Log.d("TAG1", "Error getting documents: ", task.getException());
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addToCurrent(String email){
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

                                Map<String, Object> tutee = new HashMap<>();

                                tutee.put("Partner", email);
                                tutee.put("Status", "Current");

                                ArrayList<Map> tuteeList = new ArrayList<>();

                                if(result.get("Tutee List") != null){
                                    tuteeList = (ArrayList<Map>) result.get("Tutee List");
                                    int index = -1;
                                    for(Map t: tuteeList){
                                        if(t.get("Partner").toString().equals(email)){
                                            index = tuteeList.indexOf(t);
                                        }
                                    }
                                    if(index != -1)
                                    tuteeList.remove(index);
                                }

                                tuteeList.add(tutee);

                                for(Map t: tuteeList){

                                }

                                result.put("Tutee List", tuteeList);

                                String tutorUid = document.getId();

                                db.collection("Tutors")
                                        .document(document.getId())
                                        .set(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SETTING", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("SETTING", "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
                        refreshList();
                    }
                });
        db.collection("Tutees")
                .whereEqualTo("Email", email)
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

                                Map<String, Object> tutee = new HashMap<>();

                                tutee.put("Partner", user.getEmail());
                                tutee.put("Status", "Current");

                                ArrayList<Map> tuteeList = new ArrayList<>();

                                if(result.get("Tutor List") != null){
                                    tuteeList = (ArrayList<Map>) result.get("Tutor List");
                                    int index = -1;
                                    for(Map t: tuteeList){
                                        if(t.get("Partner").toString().equals(user.getEmail())){
                                            index = tuteeList.indexOf(t);
                                        }
                                    }
                                    if(index != -1)
                                        tuteeList.remove(index);
                                }

                                tuteeList.add(tutee);

                                result.put("Tutor List", tuteeList);

                                String tuteeUid = document.getId();

                                db.collection("Tutees")
                                        .document(document.getId())
                                        .set(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SETTING", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("SETTING", "Error writing document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("TAG1", "Error getting documents: ", task.getException());
                        }
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
        Intent i = new Intent(TutorHomePage.this, ViewTutorProfile.class);
        startActivity(i);
        finish();
    }
    public void ClickReview(View view){
        Intent i = new Intent(TutorHomePage.this, CommentsPage.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(TutorHomePage.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(TutorHomePage.this, MainActivity.class);
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


//        db.collection("Tutors")
//                .whereEqualTo("Email", user.getEmail())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("TAG1", document.getId() + " => " + document.getData());
//                                Gson gson = new Gson();
//                                Map<String, Object> result = new HashMap<>();
//                                result = document.getData();
//
//                                if(result.get("Tutee List") != null){
//                                    Log.d("ABTESTING", "entered if Tutee List exists");
//                                    for(Map m: (ArrayList<Map>) result.get("Tutee List")){
//                                        //Session session = gson.fromJson(s, Session.class);
//                                        if(m.get("Status").toString().equals("Request")){
//                                            db.collection("Tutors")
//                                                    .whereEqualTo("Email", user.getEmail())
//                                                    .get()
//                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                            if (task.isSuccessful()) {
//                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                    Log.d("TAG1", document.getId() + " => " + document.getData());
//                                                                    Gson gson = new Gson();
//                                                                    Map<String, Object> result = new HashMap<>();
//                                                                    result = document.getData();
//
//                                                                    if (user.getEmail().equals(result.get("Email"))) {
//                                                                        Log.d("ABTESTING", "entered if");
//                                                                        for(Map m: (ArrayList<Map>) result.get("Tutee List")){
//                                                                            //Session session = gson.fromJson(s, Session.class);
//
//                                                                            if(m.get("Status").toString().equals("Request")){
//                                                                                db.collection("Tutees")
//                                                                                        .whereEqualTo("Email", m.get("Partner").toString())
//                                                                                        .get()
//                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                                if (task.isSuccessful()) {
//                                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                                                        Log.d("TAG1", document.getId() + " => " + document.getData());
//                                                                                                        Gson gson = new Gson();
//                                                                                                        Map<String, Object> result = new HashMap<>();
//                                                                                                        result = document.getData();
//                                                                                                        String firstname = result.get("First name").toString();
//                                                                                                        String lastname = result.get("Last name").toString();
//                                                                                                        String fullname = firstname.substring(0, 1).toUpperCase()+firstname.substring(1) + " " +
//                                                                                                                lastname.substring(0, 1).toUpperCase() + lastname.substring(1);
//
//                                                                                                        TuteeList tutee = new TuteeList(result.get("Email").toString(), fullname, result.get("Contact details").toString(),
//                                                                                                                result.get("Profile Picture").toString(), session.getStatus());
//                                                                                                        reqTuteesList.add(tutee);
//                                                                                                        Log.d("ABTESTING", "requser added "+reqTuteesList);
//                                                                                                    }
//                                                                                                } else {
//                                                                                                    Log.d("TAG1", "Error getting documents: ", task.getException());
//                                                                                                }
//                                                                                                Log.d("ABTESTINGFINAL", "FINAL curuser "+curTuteesList+" requser"+reqTuteesList);
//                                                                                                dialog.dismiss();
//                                                                                            }
//                                                                                        });
//                                                                            }else if(m.get("Status").toString().equals("Current")){
//                                                                                db.collection("Tutees")
//                                                                                        .whereEqualTo("Email", m.get("Partner").toString())
//                                                                                        .get()
//                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                                if (task.isSuccessful()) {
//                                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                                                        Log.d("TAG1", document.getId() + " => " + document.getData());
//                                                                                                        Gson gson = new Gson();
//                                                                                                        Map<String, Object> result = new HashMap<>();
//                                                                                                        result = document.getData();
//                                                                                                        String firstname = result.get("First name").toString();
//                                                                                                        String lastname = result.get("Last name").toString();
//                                                                                                        String fullname = firstname.substring(0, 1).toUpperCase()+firstname.substring(1) + " " +
//                                                                                                                lastname.substring(0, 1).toUpperCase() + lastname.substring(1);
//
//                                                                                                        TuteeList tutee = new TuteeList(result.get("Email").toString(), fullname, result.get("Contact details").toString(),
//                                                                                                                result.get("Profile Picture").toString(), session.getStatus());
//                                                                                                        curTuteesList.add(tutee);
//                                                                                                        Log.d("ABTESTING", "curuser added "+curTuteesList);
//                                                                                                    }
//                                                                                                } else {
//                                                                                                    Log.d("TAG1", "Error getting documents: ", task.getException());
//                                                                                                }
//                                                                                                Log.d("ABTESTINGFINAL", "FINAL curuser "+curTuteesList+" requser"+reqTuteesList);
//                                                                                                dialog.dismiss();
//                                                                                            }
//                                                                                        });
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            } else {
//                                                                Log.d("TAG1", "Error getting documents: ", task.getException());
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                    }
//                                }
//                            }
//                        } else {
//                            Log.d("TAG1", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });