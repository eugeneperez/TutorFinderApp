package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ViewTuteeProfile extends AppCompatActivity {

    private TextView text_name;
    private TextView text_email;
    private TextView text_contact;
    private ImageView image_tutee_profile;
    private Button btn_edit_profile;
    private Button btn_changepass;

    private DrawerLayout drawerLayout;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private String firstname;
    private String lastname;
    private String contact;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutee_profile);

        text_name = findViewById(R.id.tutee_profile_name);
        text_email = findViewById(R.id.tutee_profile_email);
        text_contact = findViewById(R.id.tutee_profile_contact);
        image_tutee_profile = findViewById(R.id.tutee_profile_image);
        btn_edit_profile = findViewById(R.id.edit_tutee_profile_btn);
        btn_changepass = findViewById(R.id.tutee_change_pass_btn);
        drawerLayout= findViewById(R.id.drawer_layout);

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewTuteeProfile.this, EditTuteeProfile.class);
                i.putExtra("First name", firstname);
                i.putExtra("Last name", lastname);
                i.putExtra("Contact details", contact);
                i.putExtra("Profile Picture", image);
                startActivity(i);
            }
        });

        btn_changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewTuteeProfile.this);
                LinearLayout layout = new LinearLayout(ViewTuteeProfile.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                lp.gravity= Gravity.CENTER;
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText currPass = new EditText(ViewTuteeProfile.this);
                final EditText newPass = new EditText(ViewTuteeProfile.this);
                final EditText confirmNewPass = new EditText(ViewTuteeProfile.this);

                currPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                currPass.setLayoutParams(lp);
                newPass.setLayoutParams(lp);
                confirmNewPass.setLayoutParams(lp);

                currPass.setHint("Current Password");
                newPass.setHint("New Password");
                confirmNewPass.setHint("Confirm New Password");
                alert.setTitle("Change Password");

                layout.addView(currPass);
                layout.addView(newPass);
                layout.addView(confirmNewPass);
                layout.setPadding(60,30,60,30);
                alert.setView(layout);

                alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (newPass.getText().toString().equals(confirmNewPass.getText().toString()) && (currPass.getText().toString().length()>=8 || newPass.getText().toString().length()>=8 || confirmNewPass.getText().toString().length()>=8)) {
                            AuthCredential credentials = EmailAuthProvider.getCredential(user.getEmail(), currPass.getText().toString());
                            user.reauthenticate(credentials).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast toast = Toast.makeText(ViewTuteeProfile.this, "Password Updated", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                } else {
//                                                    Toast.makeText(ViewTutorProfile.this, "Error Password Not Updated", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast toast = Toast.makeText(ViewTuteeProfile.this, "Error Password Not Updated", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast toast = Toast.makeText(ViewTuteeProfile.this, "Incorrect Input. Try Again.", Toast.LENGTH_SHORT);
                            toast.show();
//                            Toast.makeText(ViewTutorProfile.this, "Incorrect Input. Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.collection("Tutees")
                .whereEqualTo("Email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> result = document.getData();

                                firstname = result.get("First name").toString().substring(0, 1).toUpperCase()+
                                        result.get("First name").toString().substring(1);
                                lastname = result.get("Last name").toString().substring(0, 1).toUpperCase()+
                                        result.get("Last name").toString().substring(1);
                                String fullname = firstname+" "+lastname;

                                contact = result.get("Contact details").toString();

                                text_name.setText(fullname);
                                text_email.setText(user.getEmail());
                                text_contact.setText(contact);
                                image = result.get("Profile Picture").toString();
                                Picasso.get().load(image).fit().centerInside().into(image_tutee_profile);
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
        Intent i = new Intent(ViewTuteeProfile.this, Homepage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void ClickProfile(View view){
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    public void ClickCurrentTutors(View view){
        Intent i = new Intent(ViewTuteeProfile.this, ViewTuteeListOfTutors.class);
        startActivity(i);
        finish();
    }
    public void ClickLogout(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(ViewTuteeProfile.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ViewTuteeProfile.this, MainActivity.class);
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