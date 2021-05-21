package com.mobdeve.tutorfinderapp;

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

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TutorHomePage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private TabItem currTutees;
    private TabItem reqTutees;
    private ViewPager viewPager;

    private TuteeList currTuteeList;
    private TuteeList reqTuteeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);

        drawerLayout= findViewById(R.id.drawer_layout);
        tabLayout= findViewById(R.id.tabLayout);
        currTutees= findViewById(R.id.currTutees);
        reqTutees= findViewById(R.id.reqTutees);
        viewPager= findViewById(R.id.viewPager);

        Intent i = getIntent();
        ArrayList<String> currTutees = new ArrayList<>();
        ArrayList<String> reqTutees = new ArrayList<>();

        if(i.getStringArrayListExtra("Current Tutees") != null){
            currTutees = i.getStringArrayListExtra("Current Tutees");
        }
        if(i.getStringArrayListExtra("Req Tutees") != null){
            reqTutees = i.getStringArrayListExtra("Req Tutees");
        }

        PagerAdapter pagerAdapter= new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);

        //db.collection("")

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

    }

    public void ClickProfile(View view){
        Intent i = new Intent(TutorHomePage.this, ViewTutorProfile.class);
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